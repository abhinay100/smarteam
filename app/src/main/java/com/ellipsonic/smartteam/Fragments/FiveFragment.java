package com.ellipsonic.smartteam.Fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ellipsonic.smartteam.Activity.EditProfile;
import com.ellipsonic.smartteam.AppConfig.ApiConstant;
import com.ellipsonic.smartteam.AppConfig.NetworkStatus;
import com.ellipsonic.smartteam.AppConfig.StaticConstants;
import com.ellipsonic.smartteam.Database.SharedPreference;
import com.ellipsonic.smartteam.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by Ellip sonic on 10-10-2016.
 */
public class FiveFragment extends Fragment implements View.OnClickListener{

    ImageView profile_pic;
    TextView user_name;
    ImageView edit_info;
    TextView profile_discription;
    TextView summary_discription;
    TextView experience_discription;
    TextView volunteer_discription;
    TextView projects_discription;
    TextView language_discription;
    TextView education_discription;
    TextView personal_discription;
    TextView showMore_profile;
    TextView showMore_summary;
    TextView showMore_experience;
    TextView showMore_volunteer;
    TextView showMore_projects;
    TextView showMore_languages;
    TextView showMore_education;
    TextView showMore_personal;

    android.support.v7.widget.CardView profile_card ,summary_card,experience_card,volunteer_card,project_card,language_card,education_card,personal_card;

    private ProgressBar bar;
    private SharedPreference sharedPreference;

    public FiveFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_five, container, false);

        // Shared preference working on....
        sharedPreference = new SharedPreference();

        profile_card = (android.support.v7.widget.CardView) view.findViewById(R.id.profile_card);
        summary_card = (android.support.v7.widget.CardView) view.findViewById(R.id.summary_card);
        experience_card = (android.support.v7.widget.CardView) view.findViewById(R.id.experience_card);
        volunteer_card = (android.support.v7.widget.CardView) view.findViewById(R.id.volunteer_card);
        project_card = (android.support.v7.widget.CardView) view.findViewById(R.id.project_card);
        language_card = (android.support.v7.widget.CardView) view.findViewById(R.id.language_card);
        education_card = (android.support.v7.widget.CardView) view.findViewById(R.id.education_card);
        personal_card = (android.support.v7.widget.CardView) view.findViewById(R.id.personal_card);

        hide_cards_firstTime();

        bar = (ProgressBar) view.findViewById(R.id.progressBar);
        edit_info = (ImageView) view.findViewById(R.id.edit_info);
        profile_pic = (ImageView) view.findViewById(R.id.profile_pic);
        user_name = (TextView) view.findViewById(R.id.person_name);
        profile_discription = (TextView) view.findViewById(R.id.profile_discription);
        summary_discription = (TextView) view.findViewById(R.id.summary_discription);
        experience_discription = (TextView) view.findViewById(R.id.experience_discription);
        volunteer_discription = (TextView) view.findViewById(R.id.volunteer_discription);
        projects_discription = (TextView) view.findViewById(R.id.projects_discription);
        language_discription = (TextView) view.findViewById(R.id.language_discription);
        education_discription = (TextView) view.findViewById(R.id.education_discription);
        personal_discription = (TextView) view.findViewById(R.id.personal_discription);

        showMore_profile = (TextView) view.findViewById(R.id.sm_profile);
        showMore_summary = (TextView) view.findViewById(R.id.sm_summary);
        showMore_experience = (TextView) view.findViewById(R.id.sm_experience);
        showMore_volunteer = (TextView) view.findViewById(R.id.sm_volunteer);
        showMore_projects = (TextView) view.findViewById(R.id.sm_projects);
        showMore_languages = (TextView) view.findViewById(R.id.sm_language);
        showMore_education = (TextView) view.findViewById(R.id.sm_education);
        showMore_personal = (TextView) view.findViewById(R.id.sm_personal);


        if(NetworkStatus.isNetworkAvailable(getContext()) == true){
            new DisplayUserInfo().execute(ApiConstant.ALLUSER+"/"+sharedPreference.getValue(getActivity(),"USER_EMAIL"));
        }else {
            Toast.makeText(getActivity(),"Check your Internet Connection",Toast.LENGTH_SHORT).show();
        }

        edit_info.setOnClickListener(this);

        return view;
    }

    private void hide_cards_firstTime() {

        summary_card.setVisibility(View.INVISIBLE);
        experience_card.setVisibility(View.INVISIBLE);
        volunteer_card.setVisibility(View.INVISIBLE);
        project_card.setVisibility(View.INVISIBLE);
        language_card.setVisibility(View.INVISIBLE);
        education_card.setVisibility(View.INVISIBLE);
        personal_card.setVisibility(View.INVISIBLE);

    }
    private void display_cards() {

        summary_card.setVisibility(View.VISIBLE);
        experience_card.setVisibility(View.VISIBLE);
        volunteer_card.setVisibility(View.VISIBLE);
        project_card.setVisibility(View.VISIBLE);
        language_card.setVisibility(View.VISIBLE);
        education_card.setVisibility(View.VISIBLE);
        personal_card.setVisibility(View.VISIBLE);

    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.edit_info:
                Intent intent = new Intent(getActivity(), EditProfile.class);
                this.startActivity(intent);
                break;
        }
    }

    private class DisplayUserInfo extends AsyncTask<String, String, String> {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            bar.setVisibility(View.VISIBLE);
        }

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
            } catch (ProtocolException e) {
                Log.e("ProtocolException", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            }catch (MalformedURLException e) {
                Log.e("MalformedURLException", "Error ", e);
                return null;
            }catch (IOException e) {
                Log.e("MalformedURLException", "Error ", e);
                return null;
            }finally{
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
            bar.setVisibility(View.GONE);

            if (s.equals("[]\n")) {
                Log.i("json", s);
                hide_cards_firstTime();
            } else {

                populateData(s);
                Log.i("json", s);

            }
        }
    }

    private void populateData(String s) {

        display_cards();
        try {
            JSONArray jobj = new JSONArray(s);
            JSONObject serverData = (JSONObject) jobj.get(0);

            user_name.setText(serverData.getString("first_name"));
            profile_pic.setImageResource(R.drawable.user);
            profile_discription.setText(serverData.getString("profile_description"));
            summary_discription.setText(serverData.getString("summary_description"));
            experience_discription.setText(serverData.getString("experience_description"));
            volunteer_discription.setText(serverData.getString("volunteer_experience_causes_description"));
            projects_discription.setText(serverData.getString("projects_description"));
            education_discription.setText(serverData.getString("education_description"));
            language_discription.setText(serverData.getString("language_description"));
            personal_discription.setText(serverData.getString("personal_details_description"));


        }catch (JSONException e){
            e.printStackTrace();
        }

    }
}