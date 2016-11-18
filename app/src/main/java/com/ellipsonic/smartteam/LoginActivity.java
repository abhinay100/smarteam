package com.ellipsonic.smartteam;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.ellipsonic.smartteam.Activity.MainActivity;
import com.ellipsonic.smartteam.AppConfig.ApiConstant;
import com.ellipsonic.smartteam.AppConfig.NetworkStatus;
import com.ellipsonic.smartteam.AppConfig.StaticConstants;
import com.ellipsonic.smartteam.Database.SharedPreference;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

/* for the google sign-in code is from below link & configuration

1) https://github.com/googlesamples/google-services/blob/master/android/signin/app/src/main/java/com/google/samples/quickstart/signin/SignInActivity.java#L51-L55
2) https://developers.google.com/identity/sign-in/android/start
3) https://developers.google.com/identity/sign-in/android/start-integrating
*/

/**
 * Activity to demonstrate basic retrieval of the Google user's ID, email address, and basic
 * profile.
 */
public class LoginActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;

    private GoogleApiClient mGoogleApiClient;
    private TextView mStatusTextView;
    private ProgressDialog mProgressDialog;


    //Image Loader
    private ImageLoader imageLoader;
    ProgressDialog mprogressBar;
    String person_name;
    String person_email;
    String Person_id;
    private SharedPreference sharedPreference;
    Activity context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Button listeners & All Initializations
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        sharedPreference = new SharedPreference();


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(gso.getScopeArray());

    }

    // [START onActivityResult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }
    // [END onActivityResult]

    // [START handleSignInResult]
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
      //  hideProgressDialog();
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            StaticConstants.user_name = person_name = acct.getDisplayName();
            StaticConstants.user_email = person_email = acct.getEmail();
            StaticConstants.photo_url = acct.getPhotoUrl().toString();
            sharedPreference.saveGmailDetails(context,person_name,person_email,acct.getPhotoUrl().toString());

            //Initializing image loader
//            imageLoader = CustomVolleyRequest.getInstance(this.getApplicationContext()).getImageLoader();
//            imageLoader.get(acct.getPhotoUrl().toString(),ImageLoader.getImageListener(profilePhoto,R.drawable.user,R.drawable.user));
//            profilePhoto.setImageUrl(acct.getPhotoUrl().toString(), imageLoader);

            Toast.makeText(getApplicationContext(),"Sigin-in result: Success", Toast.LENGTH_SHORT).show();
            new CheckUserExistance().execute(ApiConstant.USEREXISTANCE + acct.getEmail());

        } else {

            hideProgressDialog();
            Toast.makeText(getApplicationContext(),"Sigin-in result: Failure", Toast.LENGTH_SHORT).show();
            // Signed out, show unauthenticated UI.
        }
    }
    // [END handleSignInResult]

    // [START signIn]
    private void signIn() {
        showProgressDialog();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signIn]


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                boolean internet_availability = NetworkStatus.isNetworkAvailable(this);
                if (internet_availability == true) {
                    //Calling signin
                    signIn();
                 //   showProgressDialog();
                } else {
                    Toast.makeText(getApplicationContext(), "Please check Your internet Connection", Toast.LENGTH_SHORT).show();
                }


                break;
        }
    }

    private class CheckUserExistance extends AsyncTask<String, String, String> {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        @Override
        protected String doInBackground(String... urls) {
            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;
            try {
                URL url = new URL(urls[0]);
                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();
                return forecastJsonStr;
            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("[]\n")) {
                Log.i("json", s);
                insert_user(person_name, person_email);
            } else {
                Log.i("json", s);
                try {
                    JSONArray jobj = new JSONArray(s);
                    JSONObject serverData = (JSONObject) jobj.get(0);
                    sharedPreference.saveUserId(getApplicationContext(),serverData.getString("id"));
                }catch (JSONException e){
                    e.printStackTrace();
                }
                hideProgressDialog();
                Intent goto_profile = new Intent(getApplicationContext(), MainActivity.class);
                goto_profile.putExtra("viewpager_position", 3);
                startActivity(goto_profile);
                finish();
            }
        }
    }
    private void insert_user(String person_name, String person_email) {
        if (NetworkStatus.isNetworkAvailable(this) == true) {
            new InsertUser(person_name, person_email).execute(ApiConstant.ALLUSER);
        } else {
            Toast.makeText(getApplicationContext(), "Check with Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }
    public class InsertUser extends AsyncTask<String, Void, String> {
        private String first_name;
        private String email;
        public InsertUser(String first_name, String email) {
            this.first_name = first_name;
            this.email = email;
        }
        protected void onPreExecute() {
        }
        protected String doInBackground(String... arg0) {
            try {
                URL url = new URL("http://104.236.73.97/api/v1/persons"); // here is your URL path
                JSONObject postDataParams = new JSONObject();
                postDataParams.put("first_name", this.first_name);
                postDataParams.put("email", this.email);
                Log.e("params", postDataParams.toString());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));
                writer.flush();
                writer.close();
                os.close();
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new
                            InputStreamReader(
                            conn.getInputStream()));
                    StringBuffer sb = new StringBuffer("");
                    String line = "";
                    while ((line = in.readLine()) != null) {
                        sb.append(line);
                        break;
                    }
                    in.close();
                    return sb.toString();
                } else {
                    return new String("message : " + responseCode);
                }
            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }
        }
        @Override
        protected void onPostExecute(String result) {
            try {
                hideProgressDialog();
                JSONObject jsonObject = new JSONObject(result);
                Toast.makeText(getApplicationContext(), result,
                        Toast.LENGTH_LONG).show();
                String msg = jsonObject.getString("message");
                if (msg.equals("Inserted Person")) {
                    StaticConstants.user_id = Person_id = jsonObject.getString("person_id");
                    sharedPreference.saveUserId(context,Person_id);
                    Intent profile = new Intent(getApplicationContext(), MainActivity.class);
                    profile.putExtra("viewpager_position", 3);
                    startActivity(profile);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "User Not insterted", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public String getPostDataString(JSONObject params) throws Exception {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        Iterator<String> itr = params.keys();
        while (itr.hasNext()) {
            String key = itr.next();
            Object value = params.get(key);
            if (first)
                first = false;
            else
                result.append("&");
            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));
        }
        return result.toString();
    }
}
