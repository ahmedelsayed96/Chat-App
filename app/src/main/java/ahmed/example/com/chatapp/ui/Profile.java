package ahmed.example.com.chatapp.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ahmed.example.com.chatapp.GlideApp;
import ahmed.example.com.chatapp.models.Data;
import ahmed.example.com.chatapp.models.UserData;
import ahmed.example.com.chatapp.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class Profile extends AppCompatActivity {

    public static final String ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%";
    @BindView(R.id.email)
    TextView emailTextView;
    @BindView(R.id.name)
    TextView nameTextView;
    @BindView(R.id.age)
    TextView ageTextView;
    @BindView(R.id.phone_Number)
    TextView phoneNumberTextView;
    @BindView(R.id.profile_image)
    ImageView imageView;
    List<String> roomsId;
    private UserData mUserData;
    private String uid;
    private DatabaseReference reference;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        ButterKnife.bind(this);
        getUserData();

    }

    private void getUserData() {
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        reference = FirebaseDatabase.getInstance().getReference();
        reference.keepSynced(true);
        reference.child(Data.USERS).child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUserData = dataSnapshot.getValue(UserData.class);
                setData();
                roomsId = new ArrayList<String>();
                for (DataSnapshot roomsSnapshot : dataSnapshot.child(Data.ROOMS).getChildren()) {

                    roomsId.add(roomsSnapshot.getKey());

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Profile.this,
                               "NO InternetConnection",
                               Toast.LENGTH_SHORT
                ).show();

            }
        });
    }

    private void setData() {
        emailTextView.setText(mUserData.getEMail());
        nameTextView.setText(mUserData.getName());
        ageTextView.setText(mUserData.getAge());
        phoneNumberTextView.setText(mUserData.getPhone());

        GlideApp.with(this)
                .load(mUserData.getImage())
                .placeholder(R.drawable.user_pic)
                .error(R.drawable.user_pic)
                .apply(RequestOptions.circleCropTransform())
                .into(imageView);
    }

    public void changeImage(View view) {

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select your image"), 1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();

            try {
                uploadImage(uri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }

    }

    void uploadImage(final Uri uri) throws FileNotFoundException {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage
                .getReferenceFromUrl("gs://firebase-hatemido123.appspot.com/" + Data.USERS + "/" + uid + "/profile");
//        StorageReference mountainsRef = storageReference.child(Data.USERS).child(uid).child("profile");
        InputStream image_stream = getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(image_stream);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] mBytes = baos.toByteArray();
        UploadTask uploadTask = storageReference.putBytes(mBytes);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                String url = taskSnapshot.getDownloadUrl().toString();
                Map<String, Object> map = new HashMap<>();
                map.put("/" + Data.USERS + "/" + uid + "/image", url);

                for (String roomId : roomsId) {
                    map.put("/" + Data.ROOMS + "/" + roomId + "/" + Data.MEMBERS + "/" + uid + "/image",
                            url
                    );
                }
                reference.updateChildren(map);

            }
        });


    }

    public void editUser(View view) {
        if (mUserData == null) {
            Toast.makeText(this, R.string.no_intenet_connection, Toast.LENGTH_SHORT).show();
            return;
        }
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.edit_user);
        final EditText name = dialog.findViewById(R.id.name);
        final EditText age = dialog.findViewById(R.id.age);
        final EditText phone = dialog.findViewById(R.id.phone_Number);
        TextView edit = dialog.findViewById(R.id.edit);
        name.setText(mUserData.getName());
        age.setText(mUserData.getAge());
        phone.setText(mUserData.getPhone());
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> map = new HashMap<>();
                map.put("/" + Data.USERS + "/" + uid + "/name", name.getText().toString());
                map.put("/" + Data.USERS + "/" + uid + "/age", age.getText().toString());
                map.put("/" + Data.USERS + "/" + uid + "/phone", phone.getText().toString());
                for (String roomId : roomsId) {
                    map.put("/" + Data.ROOMS + "/" + roomId + "/" + Data.MEMBERS + "/" + uid + "/name",
                            name.getText().toString()
                    );
                    map.put("/" + Data.ROOMS + "/" + roomId + "/" + Data.MEMBERS + "/" + uid + "/age",
                            age.getText().toString()
                    );
                    map.put("/" + Data.ROOMS + "/" + roomId + "/" + Data.MEMBERS + "/" + uid + "/phone",
                            phone.getText().toString()
                    );
                }
                reference.updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Profile.this, R.string.done, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });

            }
        });
        dialog.show();
    }
}
