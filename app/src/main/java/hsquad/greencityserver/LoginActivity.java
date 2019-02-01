package hsquad.greencityserver;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import hsquad.greencityserver.Common.Common;
import hsquad.greencityserver.Model.User;

public class LoginActivity extends AppCompatActivity {

    EditText mPhone, mPassword;
    Button mLogin;

    FirebaseDatabase db;
    DatabaseReference users;

    SharedPreferences sharePref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mLogin = (Button)findViewById(R.id.loginBtn);
        mPhone = (EditText)findViewById(R.id.phoneET);
        mPassword = (EditText)findViewById(R.id.passwordET);

        //Init Firebase
        db = FirebaseDatabase.getInstance();
        users = db.getReference("Users List");


        //Shared Preference
        sharePref = getSharedPreferences("userInfoRef", Context.MODE_PRIVATE);
        if(!sharePref.getString("phoneRef", "").equals(""))
        {
            String phn = sharePref.getString("phoneRef", "");
            String pwd = sharePref.getString("passwordRef", "");
            logIn(phn, pwd);
        }

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInUser(mPhone.getText().toString(), mPassword.getText().toString());
            }
        });
    }


    private void signInUser(String phone, String password) {

        final ProgressDialog mDialog = new ProgressDialog(LoginActivity.this);
        mDialog.setMessage("Connecting..");
        mDialog.show();

        final String localPhone = phone;
        final String localPassword = password;

        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mDialog.dismiss();
                if(!localPhone.isEmpty() && !localPassword.isEmpty()) {

                    if (dataSnapshot.child(localPhone).exists()) {
                        User user = dataSnapshot.child(localPhone).getValue(User.class);
                        user.setPhone(localPhone);

                        if (Boolean.parseBoolean(user.getIsStaff())) //If Staff == true
                        {
                            if (user.getPassword().equals(localPassword)) {
                                Toast.makeText(LoginActivity.this, "Sign in done", Toast.LENGTH_SHORT).show();

                                //Shared Preference
                                editor = sharePref.edit();
                                editor.putString("phoneRef", localPhone);
                                editor.putString("passwordRef", localPassword);
                                editor.apply();

                                Intent loginIntent = new Intent(LoginActivity.this, HomeActivity.class);
                                Common.currentUser = user;
                                startActivity(loginIntent);
                                finish();

                            } else {
                                Toast.makeText(LoginActivity.this, "Wrong Password!", Toast.LENGTH_SHORT).show();
                                mPassword.setText("");
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Please login with staff account", Toast.LENGTH_SHORT).show();
                            mPassword.setText("");
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "User not exist in Database", Toast.LENGTH_SHORT).show();
                        mPassword.setText("");
                    }
                }else{
                    Toast.makeText(LoginActivity.this, "Information Missing", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void logIn(final String phn, final String pwd) {

        final ProgressDialog mDialog = new ProgressDialog(LoginActivity.this);
        mDialog.setMessage("Connecting..");
        mDialog.show();

        //Initial Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("Users List");


        if(Common.isConnectedToInternet(getBaseContext())) {

            users.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    mDialog.dismiss();


                    if (dataSnapshot.child(phn).exists()) {
                        User user = dataSnapshot.child(phn).getValue(User.class);
                        user.setPhone(phn);

                        if (Boolean.parseBoolean(user.getIsStaff())) //If Staff == true
                        {
                            if (user.getPassword().equals(pwd)) {
                                Toast.makeText(LoginActivity.this, "Sign in done", Toast.LENGTH_SHORT).show();

                                Intent loginIntent = new Intent(LoginActivity.this, HomeActivity.class);
                                Common.currentUser = user;
                                startActivity(loginIntent);
                                finish();

                            } else {
                                Toast.makeText(LoginActivity.this, "Wrong Password!", Toast.LENGTH_SHORT).show();
                                mPassword.setText("");
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Please login with staff account", Toast.LENGTH_SHORT).show();
                            mPassword.setText("");
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "User not exist in Database", Toast.LENGTH_SHORT).show();
                        mPassword.setText("");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else{
            Toast.makeText(LoginActivity.this, "Check Internet Connection", Toast.LENGTH_SHORT).show();
            return;
        }
    }
}
