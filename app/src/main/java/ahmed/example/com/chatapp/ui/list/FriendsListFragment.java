package ahmed.example.com.chatapp.ui.list;


import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ahmed.example.com.chatapp.models.Data;
import ahmed.example.com.chatapp.models.FriendData;
import ahmed.example.com.chatapp.models.RoomData;
import ahmed.example.com.chatapp.models.UserData;
import ahmed.example.com.chatapp.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsListFragment extends Fragment implements FriendsAdapter.onItemClickListener {


    List<RoomData> roomDatas = new ArrayList<>();
    List<String> roomKeys = new ArrayList<>();
    FriendsAdapter adapter;
    @BindView(R.id.friend_recycler)
    RecyclerView friendsRecyclerView;
    @BindView(R.id.friend_progress)
    ProgressBar friendProgressBar;
    OnChatRoomClickListener mOnChatRoomClickListener;
    private View view;
    private String uid;
    private UserData mUser;
    private Dialog dialog;
    private DatabaseReference reference;
    private boolean isTablet;
    private boolean isFirst = true;

    public FriendsListFragment() {
        // Required empty public constructor
    }

    public void setUid(String uid, boolean isTablet) {
        this.uid = uid;
        this.isTablet = isTablet;


    }

    public void setmUser(UserData mUser, OnChatRoomClickListener onChatRoomClickListener) {
        this.mUser = mUser;
        this.mOnChatRoomClickListener = onChatRoomClickListener;
        getFriendsRoomsFromServer();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState
    ) {
        view = inflater.inflate(R.layout.fragment_friends_list, container, false);
        ButterKnife.bind(this, view);
        setRecyclerView();

        return view;
    }

    void setRecyclerView() {
        adapter = new FriendsAdapter(getActivity(), roomDatas, this);
        friendsRecyclerView.setAdapter(adapter);
        friendsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),
                                                                     LinearLayoutManager.VERTICAL,
                                                                     false
        ));

    }

    private void getFriendsRoomsFromServer() {
        friendProgressBar.setVisibility(View.VISIBLE);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.keepSynced(true);
        reference.child(Data.ROOMS).orderByChild("/" + Data.MEMBERS + "/" + uid + "/email").equalTo(
                mUser.getEMail()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                friendProgressBar.setVisibility(View.GONE);
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    RoomData roomData = snapshot.getValue(RoomData.class);
                    String key = snapshot.getKey();
                    for (DataSnapshot child : snapshot.child(Data.MEMBERS).getChildren()) {
                        if (!child.getKey().equals(uid)) {
                            UserData data = child.getValue(UserData.class);

                            roomData.setFriendData(new FriendData(data.getName(), data.getImage()));
                            break;
                        }
                    }

                    if (!containRoom(roomData, key)) {
                        roomDatas.add(roomData);
                        roomKeys.add(key);
                    }

                }
                adapter.notifyDataSetChanged();
                if (isTablet && roomKeys.size() > 0 && isFirst) {
                    mOnChatRoomClickListener.OnChatRoomClick(roomKeys.get(0));
                    isFirst = false;

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(),
                               databaseError.getMessage(),
                               Toast.LENGTH_SHORT
                ).show();
            }
        });

    }


    boolean containRoom(RoomData roomData, String key) {
        for (int i = 0; i < roomDatas.size(); i++) {
            if (roomKeys.get(i).equals(key) && roomDatas.get(i).getLastDate() == roomData.getLastDate()) {
                return true;
            } else if (roomKeys.get(i).equals(key) && roomDatas.get(i).getLastDate() != roomData.getLastDate()) {
                roomKeys.remove(i);
                roomDatas.remove(i);
                return false;
            }
        }

        return false;
    }

    @Override
    public void OnItemClickListener(int position) {
        mOnChatRoomClickListener.OnChatRoomClick(roomKeys.get(position));

    }

    @OnClick(R.id.fab)
    void addFriend() {
        if (mUser == null) {
            Toast.makeText(getActivity(),
                           R.string.no_intenet_connection,
                           Toast.LENGTH_SHORT
            ).show();
            return;
        }
        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.add_friend);

        final EditText editText = dialog.findViewById(R.id.friend_email);
        Button addFriend = dialog.findViewById(R.id.add_friend_button);
        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkFriendEmail(editText.getText().toString());

            }
        });
        dialog.show();


    }

    private void checkFriendEmail(String friendEmail) {
        reference = FirebaseDatabase.getInstance().getReference();
        reference.keepSynced(true);
        reference.child(Data.USERS).orderByChild("email").equalTo(friendEmail).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            UserData userData = snapshot.getValue(UserData.class);
                            if (userData != null) {
                                Log.e("data", "/" + snapshot.toString());
                                String key = reference.getRoot().child(Data.ROOMS).push().getKey();
                                Map<String, Object> childUpdate = new HashMap<>();
                                childUpdate.put("/" + Data.ROOMS + "/" + key + "/" + Data.MEMBERS + "/" + snapshot.getKey(),
                                                userData
                                );
                                childUpdate.put("/" + Data.ROOMS + "/" + key + "/" + Data.MEMBERS + "/" + uid,
                                                mUser
                                );
                                childUpdate.put("/" + Data.USERS + "/" + uid + "/" + Data.ROOMS + "/" + key,
                                                "true"
                                );
                                childUpdate.put("/" + Data.USERS + "/" + snapshot.getKey() + "/" + Data.ROOMS + "/" + key,
                                                "true"
                                );
                                reference.updateChildren(childUpdate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        dialog.dismiss();
                                        Toast.makeText(getActivity(),
                                                       R.string.friend_added,
                                                       Toast.LENGTH_SHORT
                                        ).show();
                                    }
                                });

                            } else {
                                Toast.makeText(getActivity(),
                                               R.string.emai_doesnt_exist,
                                               Toast.LENGTH_SHORT
                                ).show();
                            }
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    public interface OnChatRoomClickListener {

        void OnChatRoomClick(String roomId);
    }
}
