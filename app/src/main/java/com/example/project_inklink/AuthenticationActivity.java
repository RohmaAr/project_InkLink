package com.example.project_inklink;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class  AuthenticationActivity extends AppCompatActivity {
    FragmentManager manager;
    View loginView, signupView;
    User user;
    private FirebaseAuth auth;
    Button btnLLogin, btnSLogin, btnLSignup, btnSSignup;
    TextInputEditText etLUsername, etLPassword,etSEmail, etSUsername, etSPassword, etSConfirmPassword;
    Fragment fragLogIn,fragSignUp;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        init();
        auth=FirebaseAuth.getInstance();
        btnLSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manager.beginTransaction()
                        .hide(fragLogIn)
                        .show(fragSignUp)
                        .commit();
            }
        });

        btnSLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manager.beginTransaction()
                        .show(fragLogIn)
                        .hide(fragSignUp)
                        .commit();
            }
        });
        btnLLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String us,pas;
                us= Objects.requireNonNull(etLUsername.getText()).toString().trim();
                pas= Objects.requireNonNull(etLPassword.getText()).toString().trim();
                if(!us.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(us).matches()
                )
                {
                    if(!pas.isEmpty()) {
                        auth.signInWithEmailAndPassword(us, pas).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                Toast.makeText(AuthenticationActivity.this, "Logged In", Toast.LENGTH_SHORT).show();
                                DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

                                Query query = usersRef.orderByChild("email").equalTo(us);

                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            for (DataSnapshot snapshotting : snapshot.getChildren()) {
                                                 user= snapshotting.getValue(User.class);
                                                if (user != null) {
                                                    // Handle the retrieved user object
                                                    System.out.println("Username: " + user.getUsername());
                                                    System.out.println("Email: " + user.getEmail());
                                                    System.out.println("Profile Picture URL: " + user.getPfpUrl());
                                                    Intent intent = new Intent(AuthenticationActivity.this, MainActivity2.class);

                                                    intent.putExtra("user",user);
                                                    startActivity(intent);

                                                    finish();
                                                    
                                                }
                                            }
                                        }
                                        else {
                                            System.out.println("NO snapshot by this user");
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        System.out.println("Error fetching user: " + error.getMessage());

                                    }


                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AuthenticationActivity.this, "Could not log in", Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                    else
                    {
                        etLPassword.setError("Password cannot be empty");
                    }
                }
                else if(us.isEmpty())
                {
                    etLUsername.setError("Email cannot be empty");
                }
                else{
                    etLUsername.setError("Please enter valid username");
                }
            }
        });
        btnSSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String us,pas,conpas,email;
                email=Objects.requireNonNull(etSEmail.getText()).toString().trim();
                us= Objects.requireNonNull(etSUsername.getText()).toString().trim();
                pas= Objects.requireNonNull(etSPassword.getText()).toString().trim();
                conpas=Objects.requireNonNull(etSConfirmPassword.getText()).toString().trim();

                String regexPattern = ".*[!@#$%^&*()+\\-=\\[\\]{};':\"\\\\|,<>\\/?].*";

                if(us.isEmpty())
                {
                    etSUsername.setError("Username cannot be empty");
                }
                else if(us.matches(regexPattern))
                {
                    etSUsername.setError("Can only use _ or . in username");
                }
                else if(email.isEmpty())
                {
                    etSEmail.setError("Email cannot be empty");

                }
                 else if(pas.isEmpty())
                {
                    etSPassword.setError("Password cannot be empty");
                }
                 else if(conpas.isEmpty())
                {
                    etSConfirmPassword.setError("Cannot be empty");
                }
                else if(!conpas.equals(pas))
                {
                    etSConfirmPassword.setError("Does not match password");
                }
                else {
                     auth.createUserWithEmailAndPassword(email,pas).addOnCompleteListener(
                             new OnCompleteListener<AuthResult>() {
                                 @Override
                                 public void onComplete(@NonNull Task<AuthResult> task) {
                                     if(task.isSuccessful())
                                     {
                                         Toast.makeText(AuthenticationActivity.this,"Account successfully made",Toast.LENGTH_SHORT).show();
                                         databaseReference= FirebaseDatabase.getInstance().getReference("Users");
                                         String key=databaseReference.push().getKey();
                                         User user=new User(null,us,email);
                                         assert key != null;
                                         databaseReference.child(key).setValue(user);

                                         btnSLogin.performClick();
                                     }
                                     else{
                                         Toast.makeText(AuthenticationActivity.this,"Couldn't create account",Toast.LENGTH_SHORT).show();

                                     }
                                 }
                             }
                     );
                }
            }
        });
    }
    public void init(){
        manager=getSupportFragmentManager();
        loginView= Objects.requireNonNull(manager.findFragmentById(R.id.fragLogin)).requireView();
        signupView= Objects.requireNonNull(manager.findFragmentById(R.id.fragSignup)).requireView();
        btnLSignup=loginView.findViewById(R.id.btnLSignup);
        btnLLogin=loginView.findViewById(R.id.btnLLogin);
        btnSLogin=signupView.findViewById(R.id.btnSLogin);
        btnSSignup=signupView.findViewById(R.id.btnSSignup);

        etLPassword=loginView.findViewById(R.id.etLPassword);
        etLUsername=loginView.findViewById(R.id.etLUsername);
        etSUsername=signupView.findViewById(R.id.etSUsername);
        etSPassword=signupView.findViewById(R.id.etSPassword);
        etSConfirmPassword=signupView.findViewById(R.id.etSConfirmPassword);
        etSEmail=signupView.findViewById(R.id.etSEmail);

        fragSignUp=manager.findFragmentById(R.id.fragSignup);
        fragLogIn=manager.findFragmentById(R.id.fragLogin);

    }
}