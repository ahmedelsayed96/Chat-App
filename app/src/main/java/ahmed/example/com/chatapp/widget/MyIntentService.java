package ahmed.example.com.chatapp.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import ahmed.example.com.chatapp.models.Data;
import ahmed.example.com.chatapp.models.FriendData;
import ahmed.example.com.chatapp.models.RoomData;
import ahmed.example.com.chatapp.models.UserData;
import ahmed.example.com.chatapp.R;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 */
public class MyIntentService extends IntentService {


    public static ArrayList<RoomData> roomDatas = new ArrayList<>();
    ArrayList<String> roomKeys = new ArrayList<>();
    Intent intent;
    private String uid;
    private DatabaseReference reference;

    public MyIntentService() {
        super("MyIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        this.intent = intent;
        // get User Email then get Room that contain that Email
        if (intent != null && FirebaseAuth.getInstance().getCurrentUser() != null) {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            reference = FirebaseDatabase.getInstance().getReference();
            reference.keepSynced(true);
            reference.child(Data.USERS).child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    UserData user = dataSnapshot.getValue(UserData.class);
                    Log.e("emaill", "" + user.getEMail());
                    getUserRooms(user.getEMail());


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }

    /**
     * get User Chat Rooms
     *
     * @param email
     */
    private void getUserRooms(String email) {
        reference.child(Data.ROOMS).orderByChild("/" + Data.MEMBERS + "/" + uid + "/email").equalTo(
                email).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                roomDatas = new ArrayList<RoomData>();
                roomKeys = new ArrayList<String>();
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

                    roomDatas.add(roomData);
                    roomKeys.add(key);


                }


                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(MyIntentService.this);
                int[] allWidgetIds = intent
                        .getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);


                for (int widgetId : allWidgetIds) {

                    RemoteViews views = new RemoteViews(MyIntentService.this.getPackageName(),
                                                        R.layout.chat_app_widget
                    );

                    Intent widgetServiceIntent = new Intent(MyIntentService.this,
                                                            WidgetServiceIntent.class

                    );
                    widgetServiceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
                                                 allWidgetIds
                    );
                    views.setRemoteAdapter(R.id.widget_list, widgetServiceIntent);
                    views.setEmptyView(R.id.widget_list, R.id.empty_view);
                    appWidgetManager.updateAppWidget(widgetId, views);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MyIntentService.this,
                               databaseError.getMessage(),
                               Toast.LENGTH_SHORT
                ).show();
            }
        });
    }


}
