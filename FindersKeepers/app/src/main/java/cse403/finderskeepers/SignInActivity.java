package cse403.finderskeepers;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.*;
import android.view.View;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.*;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

import cse403.finderskeepers.data.UserInfoHolder;

public class SignInActivity extends AppCompatActivity implements OnConnectionFailedListener, View.OnClickListener {

    GoogleApiClient mGoogleApiClient;

    // constant for sign in intent
    private static final int RC_SIGN_IN = 403;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient= new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        View button = findViewById(R.id.sign_in_button);
        if (button != null) {
            button.setOnClickListener(this);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {

    }

    @Override
    public void onClick(View v) {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        TextView signInText = (TextView) findViewById(R.id.sign_in_text);
        if(result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            signInText.setText("Signed In");

            // initialize user info in global singleton
            UserInfoHolder.getInstance().initializeUser(result);

            Intent intent = new Intent(SignInActivity.this, HomePage.class);
            finish();
            startActivity(intent);
        } else {
            signInText.setText("Sign In Failed");
        }
    }
}
