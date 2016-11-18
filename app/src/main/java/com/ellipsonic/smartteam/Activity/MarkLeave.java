package com.ellipsonic.smartteam.Activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.ellipsonic.smartteam.AppConfig.ApiConstant;
import com.ellipsonic.smartteam.AppConfig.NetworkStatus;
import com.ellipsonic.smartteam.AppConfig.StaticConstants;
import com.ellipsonic.smartteam.Database.SharedPreference;
import com.ellipsonic.smartteam.R;


import org.json.JSONException;
import org.json.JSONObject;

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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Ellip sonic on 20-10-2016.
 */
public class MarkLeave extends Activity implements View.OnClickListener,AdapterView.OnItemSelectedListener{


    Button btnDatePicker, reset_btn,toDatePicker,applyBtn;
    EditText fromDate,toDate;
    private int mYear, mMonth, mDay, mHour, mMinute;
    String Person_id;
    private SharedPreference sharedPreference;
    Activity context = this;
    String person_id;
    String spinner_id = null;
    List<String> categories;
    //  String person_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_markabsence);

        btnDatePicker=(Button)findViewById(R.id.fromDatePicker);
        toDatePicker=(Button)findViewById(R.id.toDatePicker);
        applyBtn=(Button)findViewById(R.id.apply_btn);
        toDate = (EditText)findViewById(R.id.to_date);
        fromDate =(EditText)findViewById(R.id.from_date);
        // Spinner element
        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        reset_btn = (Button)findViewById(R.id.reset_btn);
        btnDatePicker.setOnClickListener(this);
        toDatePicker.setOnClickListener(this);
        spinner.setOnItemSelectedListener(this);
        reset_btn.setOnClickListener(this);
        applyBtn.setOnClickListener(this);


        sharedPreference = new SharedPreference();
        person_id = sharedPreference.getValue(this,"USER_ID");
              // Spinner Drop down elements
        categories = new ArrayList<String>();
        categories.add("Casual Leave");
        categories.add("Festival Leave");
        categories.add("Maternity leave");
        categories.add("Sick leave");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.reset_btn:
                finish();
                break;

            case R.id.fromDatePicker:
                // Get Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                fromDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
                break;

            case R.id.toDatePicker:
                // Get Current Date
                final Calendar f = Calendar.getInstance();
                mYear = f.get(Calendar.YEAR);
                mMonth = f.get(Calendar.MONTH);
                mDay = f.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog PickerDialog = new DatePickerDialog(this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                toDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            }
                        }, mYear, mMonth, mDay);
                PickerDialog.show();
                break;

            case R.id.apply_btn:
                String from_date = fromDate.getText().toString().trim();
                String to_date = toDate.getText().toString().trim();

                if(from_date.equals("") || to_date.equals("") || spinner_id.equals(null)){

                    Toast.makeText(getApplicationContext(), "select From-Date,To-Date & Reason", Toast.LENGTH_LONG).show();

                }else {
                    leave_user(person_id,from_date,to_date,spinner_id);
                }
                break;
        }

    }





    @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        // On selecting a spinner item
        spinner_id = parent.getItemAtPosition(position).toString().trim();
        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "Selected: " + spinner_id, Toast.LENGTH_LONG).show();

    }




    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void leave_user(String personId,String fromDate , String toDate, String leaveId) {
        if (NetworkStatus.isNetworkAvailable(this) == true) {
            new UserLeave(fromDate,toDate,leaveId).execute(ApiConstant.ALLUSER+"/"+personId+"/leaves");
        } else {
            Toast.makeText(getApplicationContext(), "Check with Internet Connection", Toast.LENGTH_SHORT).show();
        }

    }

    public class UserLeave extends AsyncTask<String,Void,String>{

        private String from_date;
        private String to_date;
        private String leave_id;

        public UserLeave(String from_date,String to_date,String leave_id){
            this.from_date = from_date;
            this.to_date = to_date;
            this.leave_id = leave_id;
        }


        protected void onPreExecute(){

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {
                URL url = new URL(arg0[0]); // here is your URL path
                JSONObject postDataParams = new JSONObject();
                postDataParams.put("from_date", this.from_date);
                postDataParams.put("to_date", this.to_date);
                for(int i=0 ; i<categories.size(); i++){
                    if(categories.get(i).equals(leave_id)) postDataParams.put("leave_type_id", i+1);
                }
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



            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (JSONException e) {
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
              //  progress_hide();

                JSONObject jsonObject = new JSONObject(result);
                String msg = jsonObject.getString("message");

                if (msg.equals("applied leave")) {
//                    StaticConstants.user_id = Person_id = jsonObject.getString("person_id");
//                    sharedPreference.saveUserId(context,Person_id);
                    Toast.makeText(getApplicationContext(), "Leave Applied", Toast.LENGTH_LONG).show();
                    Intent profile = new Intent(getApplicationContext(), MainActivity.class);
                    profile.putExtra("viewpager_position", 0);
                    startActivity(profile);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "User Not inserted", Toast.LENGTH_SHORT).show();
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
