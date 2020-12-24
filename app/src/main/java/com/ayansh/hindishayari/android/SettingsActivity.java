package com.ayansh.hindishayari.android;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;

import com.ayansh.hanudroid.Application;

public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

	@SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
		
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getFragmentManager().beginTransaction().replace(R.id.content_frame, new MyPreferenceFragment()).commit();

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        Application app = Application.getApplicationInstance();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean preference_value;

        if(key.contentEquals("show_memes")){

            preference_value = sharedPref.getBoolean("show_memes",true);

            if(preference_value){
                app.addSyncCategory("Meme");
            }
            else{
                app.removeSyncCategory("Meme");
            }

        }

        if(key.contentEquals("love_shayari")){

            preference_value = sharedPref.getBoolean("love_shayari",true);

            if(preference_value){
                app.addSyncCategory("Love Shayari");
            }
            else{
                app.removeSyncCategory("Love Shayari");
            }

        }

        if(key.contentEquals("adult_shayari")){

            preference_value = sharedPref.getBoolean("adult_shayari",true);

            if(preference_value){
                app.addSyncCategory("Adult Shayari");
            }
            else{
                app.removeSyncCategory("Adult Shayari");
            }

        }

    }

    public static class MyPreferenceFragment extends PreferenceFragment {

        @Override
        public void onCreate(final Bundle savedInstanceState){

            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);
        }

    }
}