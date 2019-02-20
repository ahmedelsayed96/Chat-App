package ahmed.example.com.chatapp.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Trigger;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ahmed.example.com.chatapp.models.Data;
import ahmed.example.com.chatapp.models.UserData;
import ahmed.example.com.chatapp.MyJobServices;
import ahmed.example.com.chatapp.R;
import ahmed.example.com.chatapp.ui.chat.ChatRoomFragment;
import ahmed.example.com.chatapp.ui.drawer.DrawerFragment;
import ahmed.example.com.chatapp.ui.list.FriendsListFragment;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatActivity extends AppCompatActivity implements
                                                    FriendsListFragment.OnChatRoomClickListener {

    String uid;
    UserData mUserData;
    @BindView(R.id.drawer)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private boolean isTablet = false;
    private FriendsListFragment friendsListFragment;
    private DrawerFragment drawerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        drawerFragment = (DrawerFragment) getSupportFragmentManager()
                .findFragmentById(R.id.drawer_fragment);

        drawerFragment.setUpDrawer(mDrawerLayout, toolbar);


        if (findViewById(R.id.container2) != null) {
            isTablet = true;
        }
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();


        if (savedInstanceState != null) {

        } else {
            friendsListFragment = new FriendsListFragment();
            friendsListFragment.setUid(uid, isTablet);
            changeListFragment(friendsListFragment);
            getUserData();
        }
        setJob();
    }

    /**
     * the dispatcher will send notification
     * to user
     */
    private void setJob() {
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
        Job myJob = dispatcher.newJobBuilder()
                              .setService(MyJobServices.class)
                              .setTag("MyDispatcher")
                              .setRecurring(true)
                              .setTrigger(Trigger.executionWindow(0, 86400))
                              .setReplaceCurrent(true)
                              .build();
        dispatcher.mustSchedule(myJob);
    }

    private void getUserData() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.keepSynced(true);
        reference.child(Data.USERS).child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUserData = dataSnapshot.getValue(UserData.class);
                friendsListFragment.setmUser(mUserData, ChatActivity.this);
                drawerFragment.setUserData(mUserData,ChatActivity.this);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ChatActivity.this,
                               R.string.no_intenet_connection,
                               Toast.LENGTH_SHORT
                ).show();

            }
        });
    }

    void changeListFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                                   .addToBackStack("MYSTACK")
                                   .replace(R.id.container1, fragment)
                                   .commit();
    }

    void changeChatRoomFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                                   .replace(R.id.container2, fragment)
                                   .commit();
    }

    @Override
    public void onBackPressed() {
        // empty the stack to the first fragment and if it first fragment  call super
        if (getSupportFragmentManager().getBackStackEntryCount() > 1 && !isTablet) {
            getSupportFragmentManager().popBackStack();

        } else {
            finish();
        }
    }

    @Override
    public void OnChatRoomClick(String roomId) {
        ChatRoomFragment chatRoomFragment = new ChatRoomFragment();
        chatRoomFragment.setRoom(uid, roomId, mUserData.getName());
        if (isTablet) {
            changeChatRoomFragment(chatRoomFragment);
        } else {

            changeListFragment(chatRoomFragment);
        }
    }
}
