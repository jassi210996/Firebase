package com.example.kingj.practicefirebase;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    FirebaseUser user;
    private CallbackManager callbackManager;
    TextView textView;
    AccessTokenTracker accessTokenTracker;
    ProfileTracker profileTracker;
    LoginButton loginButton;
    FirebaseAuth.AuthStateListener authStateListener;
    FacebookCallback<LoginResult> callback;
    private String TAG = "TaAAAg";
    private AccessToken accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {

            }
        };

        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
        callbackManager= CallbackManager.Factory.create();

        loginButton = findViewById(R.id.login_button);
        textView= findViewById(R.id.textView);

        loginButton.setReadPermissions("email","public_profile");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(),"facebook:onCancel",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(),"facebook:onError",Toast.LENGTH_LONG).show();
            }
        });

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser fUser = firebaseAuth.getCurrentUser();
                if(fUser!=null)
                {
                    textView.setText(fUser.getDisplayName());
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"out",Toast.LENGTH_LONG).show();
                }
            }
        };


//        FacebookSdk.sdkInitialize(getApplicationContext());


//            Intent intent =new Intent(this,ProfileActivity.class);
//            startActivity(intent);


    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }


    private void handleFacebookAccessToken(AccessToken accessToken) {

        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user);
                }
                else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.getException());
                    Toast.makeText(MainActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }
            }
        });

    }


//    @Override
//    public void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);
//


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void updateUI(FirebaseUser currentProfile) {

        if(currentProfile!=null)
        {
            textView.setText(""+currentProfile.getDisplayName());
        }
    }


}
