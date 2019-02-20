package ahmed.example.com.chatapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ahmed.example.com.chatapp.models.Data;
import ahmed.example.com.chatapp.models.UserData;
import ahmed.example.com.chatapp.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SignUpActivity extends AppCompatActivity {

    @BindView(R.id.email)
    EditText emailTextView;
    @BindView(R.id.password)
    EditText passwordTextView;
    @BindView(R.id.name)
    EditText nameTextView;
    @BindView(R.id.conf_password)
    EditText confPasswordTextView;
    @BindView(R.id.age)
    EditText ageTextView;
    @BindView(R.id.phone_Number)
    EditText phoneNumberTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
    }

    /**
     * Create Account
     *
     * @param view
     */
    public void signUp(View view) {
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        final String email = emailTextView.getText().toString();
        final String password = passwordTextView.getText().toString();
        String confPassword = confPasswordTextView.getText().toString();
        final String name = nameTextView.getText().toString();
        final String age = ageTextView.getText().toString();
        final String phoneNumber = phoneNumberTextView.getText().toString();

        // check for data validation
        if (password.equals(confPassword)
                && !email.equals("")
                && !password.equals("")
                && !name.equals("")) {
            // create account
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //on Success put Data in RealTime Database
                        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                        DatabaseReference reference = firebaseDatabase.getReference();
                        reference.child(Data.USERS)
                                 .child(authResult.getUser().getUid())
                                 .setValue(new UserData(name,
                                                        email,
                                                        phoneNumber,
                                                        null,
                                                        age)).addOnSuccessListener(
                                new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("email", email);
                                        bundle.putString("password", password);
                                        setResult(RESULT_OK, new Intent().putExtras(bundle));
                                        finish();
                                    }
                                });


                    }
                }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            Toast.makeText(this, R.string.check_your_data, Toast.LENGTH_SHORT).show();
        }
    }

    public void getImage(View view) {

    }
}
