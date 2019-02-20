package ahmed.example.com.chatapp.ui.chat;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ahmed.example.com.chatapp.models.Data;
import ahmed.example.com.chatapp.models.MessageData;
import ahmed.example.com.chatapp.models.UserData;
import ahmed.example.com.chatapp.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatRoomFragment extends Fragment {

    View view;
    String roomId;
    String mUid;
    List<MessageData> messages = new ArrayList<>();
    UserData myUserData, friendUserData;
    @BindView(R.id.message_recycler)
    RecyclerView messagesRecyclerView;
    @BindView(R.id.message_edit_text)
    EditText messageEditText;
    MessagesAdapter adapter;
    private String mName;
    private DatabaseReference reference;


    public ChatRoomFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_chat_room, container, false);
        ButterKnife.bind(this, view);
        setRecycler();
        getRoom();
        return view;
    }

    private void setRecycler() {
        adapter = new MessagesAdapter(messages, getActivity(), mName);
        messagesRecyclerView.setAdapter(adapter);
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),
                                                                      LinearLayoutManager.VERTICAL,
                                                                      true
        ));


    }

    private void getRoom() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.keepSynced(true);
        reference.child(Data.ROOMS).child(roomId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot messageSnapshot : dataSnapshot.child(Data.MESSAGES).getChildren()) {
                    MessageData messageData = messageSnapshot.getValue(MessageData.class);
                    messageData.setId(messageSnapshot.getKey());
                    Log.e("messages", "here");
                    if (!messageExist(messageData)) {
                        messages.add(0, messageData);
                    }
                }


                for (DataSnapshot member : dataSnapshot.child(Data.MEMBERS).getChildren()) {
                    if (member.getKey().equals(mUid)) {
                        myUserData = member.getValue(UserData.class);
                        myUserData.setUid(mUid);


                    } else {
                        friendUserData = member.getValue(UserData.class);
                        friendUserData.setUid(member.getKey());


                    }
                }
                adapter.setMembersData(myUserData, friendUserData);

                adapter.notifyDataSetChanged();
                Log.e("messages", messages.size() + "");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private boolean messageExist(MessageData messageData) {
        for (int i = 0; i < messages.size(); i++) {
            MessageData message = messages.get(i);
            if (message.getId().equals(messageData.getId()) && message.getType().equals("text")) {
                return true;
            } else if (message.getId().equals(messageData.getId())
                    && message.getType().equals("image")
                    && message.getImage() == null
                    && messageData.getImage() != null) {
                Log.e("Id", message.getId());
                messages.remove(i);
                messages.add(i, messageData);
                return true;
            } else if (message.getId().equals(messageData.getId())
                    && message.getType().equals("image")
                    && message.getImage() != null) {
                return true;
            }

        }
        return false;
    }

    public void setRoom(String mUid, String roomId, String mName) {
        this.mUid = mUid;
        this.mName = mName;
        this.roomId = roomId;
    }

    @OnClick(R.id.send_text)
    void sendMessage() {
        long time = new Date().getTime();
        String message = messageEditText.getText().toString();
        if (message.equals(""))
            Toast.makeText(getActivity(), R.string.no_message, Toast.LENGTH_SHORT).show();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.keepSynced(true);
        String messageKey = reference.child(Data.ROOMS).child(roomId).child(Data.MESSAGES).push().getKey();

        Map<String, Object> updateChildren = new HashMap<>();
        updateChildren.put("/" + Data.ROOMS + "/" + roomId + "/" + Data.MESSAGES + "/" + messageKey,
                           new MessageData("text", time, message, null, mUid)
        );
        updateChildren.put("/" + Data.ROOMS + "/" + roomId + "/lastMessage", message);
        updateChildren.put("/" + Data.ROOMS + "/" + roomId + "/lastDate", time);
        reference.updateChildren(updateChildren).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                messageEditText.setText("");
            }
        });


    }

    @OnClick(R.id.send_image)
    void sendMessageImage() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select your image"), 1);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();

            sendImageMessage(uri);

        }
    }

    private void sendImageMessage(final Uri uri) {
        long time = new Date().getTime();
        reference = FirebaseDatabase.getInstance().getReference();
        reference.keepSynced(true);
        final String messageKey = reference.child(Data.ROOMS).child(roomId).child(Data.MESSAGES).push().getKey();

        Map<String, Object> updateChildren = new HashMap<>();
        updateChildren.put("/" + Data.ROOMS + "/" + roomId + "/" + Data.MESSAGES + "/" + messageKey,
                           new MessageData("image", time, null, null, mName)
        );
        updateChildren.put("/" + Data.ROOMS + "/" + roomId + "/lastMessage", "Photo");
        updateChildren.put("/" + Data.ROOMS + "/" + roomId + "/lastDate", time);
        reference.updateChildren(updateChildren).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                try {
                    uploadImage(uri, messageKey);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    void uploadImage(final Uri uri, final String messageKey) throws FileNotFoundException {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReferenceFromUrl(
                "gs://firebase-hatemido123.appspot.com/");
        StorageReference mountainsRef = storageReference.child(Data.ROOMS).child(roomId).child(Data.MEMBERS).child(
                messageKey);
        InputStream image_stream = getActivity().getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(image_stream);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] mBytes = baos.toByteArray();
        UploadTask uploadTask = mountainsRef.putBytes(mBytes);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                String url = taskSnapshot.getDownloadUrl().toString();
                reference.child(Data.ROOMS).child(roomId).child(Data.MESSAGES).child(messageKey).child(
                        "image").setValue(url);

            }
        });


    }
}
