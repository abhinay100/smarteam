package com.ellipsonic.smartteam.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ellipsonic.smartteam.AppConfig.ApiConstant;
import com.ellipsonic.smartteam.AppConfig.NetworkStatus;
import com.ellipsonic.smartteam.Database.SharedPreference;
import com.ellipsonic.smartteam.HelperClasses.AppLocationService;
import com.ellipsonic.smartteam.HelperClasses.LocationAddress;
import com.ellipsonic.smartteam.Manifest;
import com.ellipsonic.smartteam.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import javax.net.ssl.HttpsURLConnection;

import static com.ellipsonic.smartteam.R.id.save_btn;

public class MarkAttendence extends AppCompatActivity implements View.OnClickListener{

    Button  camara_intent,cancel_btn;
    ImageView face_pic;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    TextView userId,dateAndTime,userName,location;
    SharedPreference sharedPreference;
    Button markAttendence;
    private ProgressDialog mProgressDialog;
    AppLocationService appLocationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_attendence);

        face_pic = (ImageView)findViewById(R.id.face_pic);
        cancel_btn = (Button)findViewById(R.id.cancel_btn);
        camara_intent =(Button)findViewById(R.id.camara_intent);
        userId = (TextView)findViewById(R.id.user_id);
        dateAndTime = (TextView)findViewById(R.id.dateAndTime);
        userName = (TextView)findViewById(R.id.userName);
        sharedPreference = new SharedPreference();
        markAttendence = (Button)findViewById(save_btn);
        location = (TextView)findViewById(R.id.location);

        camara_intent.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                finish();
            }
        });
        dateAndTime.setText(getCurrentTimeStamp());
        userId.setText(sharedPreference.getValue(this,"USER_ID"));
        userName.setText(sharedPreference.getValue(this,"USER_NAME"));
        markAttendence.setOnClickListener(this);
 //       appLocationService = new AppLocationService(MarkAttendence.this);
//        showLocationName();

    }

//    private void showLocationName() {
//        Location location = appLocationService.getLocation(LocationManager.GPS_PROVIDER);
//
//        if (location != null) {
//            double latitude = location.getLatitude();
//            double longitude = location.getLongitude();
//            LocationAddress locationAddress = new LocationAddress();
//            locationAddress.getAddressFromLocation(latitude, longitude,
//                    getApplicationContext(), new GeocoderHandler());
//        } else {
//            showSettingsAlert();
//        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            face_pic.setImageBitmap(imageBitmap);
        }
    }

    public static String getCurrentTimeStamp(){
        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDateTime = dateFormat.format(new Date()); // Find todays date

            return currentDateTime;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.save_btn:
                if(NetworkStatus.isNetworkAvailable(this) == true){

                    new MarkAttendance().execute(ApiConstant.USEREXISTANCE+""+sharedPreference.getValue(this,"USER_ID")+"/attendance");
                }else {

                    Toast.makeText(this,"Check your Internet Connection",Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    public class MarkAttendance extends AsyncTask<String,Void,String> {

        protected void onPreExecute(){
            showProgressDialog();
        }

        @Override
        protected String doInBackground(String... arg0) {

            try {
                URL url = new URL(arg0[0]); // here is your URL path
                JSONObject postDataParams = new JSONObject();
                Log.e("params", postDataParams.toString());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
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

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            try {

                hideProgressDialog();

                JSONObject jsonObject = new JSONObject(result);
                String msg = jsonObject.getString("message");

                if (msg.equals("Inserted Attendance")) {

                    Toast.makeText(getApplicationContext(), "Attendance Applied", Toast.LENGTH_LONG).show();
                    Intent profile = new Intent(getApplicationContext(), MainActivity.class);
                    profile.putExtra("viewpager_position", 0);
                    startActivity(profile);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Attendance Not applied", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
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
//
//    public void showSettingsAlert() {
//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
//                MarkAttendence.this);
//        alertDialog.setTitle("SETTINGS");
//        alertDialog.setMessage("Enable Location Provider! Go to settings menu?");
//        alertDialog.setPositiveButton("Settings",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        Intent intent = new Intent(
//                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                        MarkAttendence.this.startActivity(intent);
//                    }
//                });
//        alertDialog.setNegativeButton("Cancel",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                });
//        alertDialog.show();
//    }
//
//    private class GeocoderHandler extends Handler {
//        @Override
//        public void handleMessage(Message message) {
//            String locationAddress;
//            switch (message.what) {
//                case 1:
//                    Bundle bundle = message.getData();
//                    locationAddress = bundle.getString("address");
//                    break;
//                default:
//                    locationAddress = null;
//            }
//            location.setText(locationAddress);
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case 8: {
//                // grantResults[0] = -1
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                } else {
//
//                }
//                return;
//            }
//        }
//    }
}
