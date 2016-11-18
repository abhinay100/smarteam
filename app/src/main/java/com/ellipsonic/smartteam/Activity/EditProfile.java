package com.ellipsonic.smartteam.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ellipsonic.smartteam.AppConfig.ApiConstant;
import com.ellipsonic.smartteam.AppConfig.NetworkStatus;
import com.ellipsonic.smartteam.Database.SharedPreference;
import com.ellipsonic.smartteam.HttpCommunicationClasses.SaveProfile;
import com.ellipsonic.smartteam.R;

public class EditProfile extends AppCompatActivity implements View.OnClickListener{

    Button cancel_btn , save_btn, save_profile;
    EditText first_name,last_name,cur_company,pre_company,education_info,summary_info,experience_info,volunteer_info,projects_info,languages_info,personal_info;
    private SharedPreference sharedPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_edit_profile);
        assert getSupportActionBar() != null;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbar_text = (TextView)findViewById(R.id.toolbar_text);
        toolbar_text.setText("Edit Profile");
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        // Shared preference working on....
        sharedPreference = new SharedPreference();

        cancel_btn = (Button) findViewById(R.id.cancel_btn);
        save_btn = (Button)findViewById(R.id.save_btn);

        first_name = (EditText)findViewById(R.id.first_name);
        last_name = (EditText)findViewById(R.id.last_name);
        cur_company = (EditText)findViewById(R.id.current_job);
        pre_company = (EditText)findViewById(R.id.previous_job);
        education_info = (EditText)findViewById(R.id.education);
        summary_info = (EditText)findViewById(R.id.summary);
        experience_info = (EditText)findViewById(R.id.experience);
        volunteer_info = (EditText)findViewById(R.id.volunteer_experience);
        projects_info = (EditText)findViewById(R.id.projects);
        languages_info = (EditText)findViewById(R.id.language);
        personal_info = (EditText)findViewById(R.id.personal_details);

        save_profile = (Button)findViewById(R.id.save_profile);

        cancel_btn.setOnClickListener(this);
        save_btn.setOnClickListener(this);
        save_profile.setOnClickListener(this);

    }

    // Menu icons are inflated just as they were with acd ctionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login_actionbar, menu);
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.save_btn:
                insert_updataions();
                break;
            case R.id.save_profile:
                insert_updataions();
                break;
            case R.id.cancel_btn:
                Intent goto_profile = new Intent(getApplicationContext(),MainActivity.class);
                goto_profile.putExtra("viewpager_position", 3);
                startActivity(goto_profile);
                break;
        }
    }

    private void insert_updataions() {

        String f_name = first_name.getText().toString().trim();
        String l_name = last_name.getText().toString().trim();
        String cur_comp = cur_company.getText().toString().trim();
        String pre_comp = pre_company.getText().toString().trim();
        String education = education_info.getText().toString().trim();
        String summary = summary_info.getText().toString().trim();
        String experience = experience_info.getText().toString().trim();
        String volunteer = volunteer_info.getText().toString().trim();
        String projects = projects_info.getText().toString().trim();
        String languages = languages_info.getText().toString().trim();
        String personal = personal_info.getText().toString().trim();

        if(f_name.equals("")){
            Toast.makeText(getApplicationContext(),"Please fill the details",Toast.LENGTH_SHORT).show();
        }else {
            if(f_name.equals("")) f_name=null;
            if(l_name.equals("")) l_name=null;
            if(cur_comp.equals("")) cur_comp=null;
            if(pre_comp.equals("")) pre_comp=null;
            if(education.equals("")) education=null;
            if(summary.equals("")) summary=null;
            if(experience.equals("")) experience=null;
            if(volunteer.equals("")) volunteer=null;
            if(projects.equals("")) projects=null;
            if(languages.equals("")) languages=null;
            if(personal.equals("")) personal=null;

            if(NetworkStatus.isNetworkAvailable(this) == true){
                SaveProfile backgroundTask = new SaveProfile(getApplicationContext(), f_name,l_name,cur_comp,pre_comp, f_name+"\t"+l_name+"\n"+cur_comp+"\n"+pre_comp,
                        education,summary, experience, volunteer, projects,languages, personal);
                backgroundTask.execute(ApiConstant.ALLUSER+"/"+sharedPreference.getValue(this,"USER_ID"));
            }else {
                Toast.makeText(getApplicationContext(),"Please check your Interent Connection",Toast.LENGTH_SHORT).show();
            }
        }
    }


}
