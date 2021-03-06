package com.ayansh.hindishayari.android;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.ayansh.hanudroid.Application;
import com.ayansh.hanudroid.HanuFragmentInterface;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class Main extends AppCompatActivity implements PostListFragment.Callbacks,
        PostDetailFragment.Callbacks{

    private boolean dualPane;
    private Application app;
    private HanuFragmentInterface fragmentUI;
    private int postIndex;
    private PostPagerAdapter pagerAdapter;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, "ca-app-pub-4571712644338430~6266849909");

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        if(savedInstanceState != null){
            postIndex = savedInstanceState.getInt("PostIndex");
        }
        else{
            postIndex = 0;
        }

        if (findViewById(R.id.post_list) != null) {
            dualPane = true;
        }
        else{
            dualPane = false;
            FrameLayout postDetail = (FrameLayout) findViewById(R.id.post_detail);
            if(postDetail != null){
                postDetail.setVisibility(View.GONE);
            }
        }

        // Get Application Instance.
        app = Application.getApplicationInstance();
        app.setContext(this);

        // Start the Main Activity
        startMainScreen();

        // Show Swipe Help
        showSwipeHelp();

    }

    private void showSwipeHelp(){

        final LinearLayout swipeHelpLayout = (LinearLayout) findViewById(R.id.swipe_help);

        if(swipeHelpLayout == null){
            return;
        }

        String swipeHelp = app.readParameterValue("SwipeHelp");

        if(swipeHelp != null && swipeHelp.contentEquals("Skip")){
            // Skip the swipe help
            swipeHelpLayout.setVisibility(View.GONE);
        }
        else{

            final CheckBox showHelpAgain = (CheckBox) swipeHelpLayout.findViewById(R.id.show_again);

            Button dismissHelp = (Button) swipeHelpLayout.findViewById(R.id.dismiss_help);
            dismissHelp.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    // Hide the swipe help
                    swipeHelpLayout.setVisibility(View.GONE);

                    if(showHelpAgain.isChecked()){
                        Application.getApplicationInstance().addParameter("SwipeHelp", "Skip");
                    }
                }
            });

        }

    }

    private void startMainScreen() {

        // Show Ad.
        Bundle extras = new Bundle();
        extras.putString("max_ad_content_rating", "G");

        AdRequest adRequest = new AdRequest.Builder()
                .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("8C44DABF2A81BBE341ECAFD882A49F09").build();

        AdView adView = (AdView) findViewById(R.id.adView);

        // Start loading the ad in the background.
        adView.loadAd(adRequest);

        // Request InterstitialAd
        MyInterstitialAd.getInterstitialAd(this);
        MyInterstitialAd.requestNewInterstitial();

        // Load Posts.
        Application.getApplicationInstance().getAllPosts();

        // Create the Fragment.
        FragmentManager fm = this.getSupportFragmentManager();
        Fragment fragment;

        if (dualPane) {
            // Create Post List Fragment
            fragment = new PostListFragment();
            Bundle arguments = new Bundle();
            arguments.putInt("PostIndex", postIndex);
            //arguments.putBoolean("DualPane", dualPane);
            //arguments.putBoolean("ShowFirstItem", true);
            fragment.setArguments(arguments);
            fm.beginTransaction().replace(R.id.post_list, fragment).commitAllowingStateLoss();

            fragmentUI = (HanuFragmentInterface) fragment;

        } else {
            // Create view Pager
            viewPager = (ViewPager) findViewById(R.id.post_pager);
            viewPager.setClipToPadding(false);
            viewPager.setPageMargin(-50);
            pagerAdapter = new PostPagerAdapter(getSupportFragmentManager(),app.getPostList().size());
            viewPager.setAdapter(pagerAdapter);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id;

        switch (item.getItemId()){

            case R.id.Help:
                Intent help = new Intent(Main.this, DisplayFile.class);
                help.putExtra("File", "help.html");
                help.putExtra("Title", "Help: ");
                Main.this.startActivity(help);
                break;

            case R.id.ShowEula:
                Intent eula = new Intent(Main.this, DisplayFile.class);
                eula.putExtra("File", "eula.html");
                eula.putExtra("Title", "Terms and Conditions: ");
                Main.this.startActivity(eula);
                break;

            case R.id.Settings:
                Intent settings = new Intent(Main.this, SettingsActivity.class);
                Main.this.startActivity(settings);
                break;

            case R.id.About:
                Intent info = new Intent(Main.this, DisplayFile.class);
                info.putExtra("File", "about.html");
                info.putExtra("Title", "About: ");
                Main.this.startActivity(info);
                break;

            case R.id.Upload:
                Intent upload = new Intent(Main.this, CreateNewPost.class);
                Main.this.startActivity(upload);
                break;

        }

        return true;
    }

    @Override
    public void onItemSelected(int id) {

        if (dualPane) {
            Bundle arguments = new Bundle();
            arguments.putInt("PostIndex", id);
            PostDetailFragment fragment = new PostDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.post_detail, fragment)
                    .commit();

        }
        else{
            Intent postDetail = new Intent(Main.this, PostDetailActivity.class);
            postDetail.putExtra("PostIndex", id);
            Main.this.startActivity(postDetail);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(fragmentUI != null){
            outState.putInt("PostIndex", fragmentUI.getSelectedItem());
        }
    }

    @Override
    protected void onDestroy(){
        app.close();
        super.onDestroy();
    }

    @Override
    public void loadPostsByCategory(String taxonomy, String name) {

        if(taxonomy.contentEquals("category")){
            app.getPostsByCategory(name);
        }
        else if(taxonomy.contentEquals("post_tag")){
            app.getPostsByTag(name);
        }
        else if(taxonomy.contentEquals("author")){
            app.getPostsByAuthor(name);
        }

        this.runOnUiThread(new Runnable() {
            public void run(){
                if(fragmentUI != null) {
                    fragmentUI.reloadUI();
                }
            }
        });
    }

    @Override
    public boolean isDualPane() {
        return dualPane;
    }
}
