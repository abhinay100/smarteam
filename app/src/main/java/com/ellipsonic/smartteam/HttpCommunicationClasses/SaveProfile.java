package com.ellipsonic.smartteam.HttpCommunicationClasses;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.ellipsonic.smartteam.Activity.MainActivity;
import com.ellipsonic.smartteam.AppConfig.ApiConstant;
import com.ellipsonic.smartteam.AppConfig.StaticConstants;
import com.ellipsonic.smartteam.Database.SharedPreference;
import com.ellipsonic.smartteam.LoginActivity;

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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by USER on 10/15/2016.
 */

public class SaveProfile extends AsyncTask<String,Void,String> {
    Context ctx;
    AlertDialog alertDialog;

    String first_name;
    String last_name;
    String current_company;
    String previous_company;
    String profile_description;
    String education_description;
    String summary_description;
    String experience_description;
    String volunteer_experience_causes_description;
    String projects_description;
    String language_description;
    String personal_details_description;
    private SharedPreference sharedPreference;

//    ProgressDialog mprogressBar;

    public SaveProfile(Context ctx,
                String first_name ,
                String last_name ,
                String current_company,
                String previous_company ,
                String profile_description,
                String education_description,
                String summary_description,
                String experience_description,
                String volunteer_experience_causes_description,
                String projects_description,
                String language_description,
                String personal_details_description){

        this.ctx=ctx;
        this.first_name = first_name;
        this.last_name = last_name;
        this.current_company = current_company;
        this.previous_company = previous_company;
        this.profile_description = profile_description;
        this.education_description = education_description;
        this.summary_description = summary_description;
        this.experience_description = experience_description;
        this.volunteer_experience_causes_description = volunteer_experience_causes_description;
        this.projects_description = projects_description;
        this.language_description = language_description;
        this.personal_details_description = personal_details_description;

        sharedPreference = new SharedPreference();
    }

    @Override
    protected void onPreExecute() {
//        alertDialog=new AlertDialog.Builder(ctx).create();
//        alertDialog.setTitle("Item Information....");

//        mprogressBar = new ProgressDialog(ctx);
//        mprogressBar.setMax(100);
//        mprogressBar.setMessage("Its loading....");
//        mprogressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);

   //     progress_show();

    }

    protected String doInBackground(String... arg0) {

        try {

            URL url = new URL(ApiConstant.ALLUSER+"/"+sharedPreference.getValue(ctx,"USER_ID")); // here is your URL path

            JSONObject postDataParams = new JSONObject();
//            postDataParams.put("first_name", this.first_name);
//            postDataParams.put("last_name", this.email);
//            postDataParams.put("current_company", this.current_company);
//            postDataParams.put("previous_company", this.previous_company);
            postDataParams.put("profile_description", this.profile_description);
            postDataParams.put("summary_description", this.summary_description);
            postDataParams.put("experience_description", this.experience_description);
            postDataParams.put("volunteer_experience_causes_description", this.volunteer_experience_causes_description);
            postDataParams.put("projects_description", this.projects_description);
            postDataParams.put("language_description", this.language_description);
            postDataParams.put("education_description", this.education_description);
            postDataParams.put("personal_details_description", this.personal_details_description);


            Log.e("params", postDataParams.toString());

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestProperty("X-HTTP-Method-Override", "PATCH");
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
    protected void onProgressUpdate(Void... values) {

        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String result) {
        try {

  //          progress_hide();

            JSONObject jsonObject = new JSONObject(result);
            Toast.makeText(ctx, result,
                    Toast.LENGTH_LONG).show();
            String msg = jsonObject.getString("message");

            if (msg.equals("Updated Person")) {

                Intent profile = new Intent(ctx, MainActivity.class);
                profile.putExtra("viewpager_position", 3);
                profile.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(profile);

            } else {
                Toast.makeText(ctx, "User Not Updated", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
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

//    private void progress_hide() {
//        mprogressBar.dismiss();
//    }
//
//    private void progress_show() {
//        mprogressBar.show();
//    }
}
