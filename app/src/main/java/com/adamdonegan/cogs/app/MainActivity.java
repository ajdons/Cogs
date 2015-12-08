package com.adamdonegan.cogs.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.adamdonegan.cogs.R;
import com.adamdonegan.cogs.models.Identity;
import com.adamdonegan.cogs.models.Profile;
import com.adamdonegan.cogs.models.Release;
import com.adamdonegan.cogs.models.Result;
import com.adamdonegan.cogs.models.SearchResults;
import com.adamdonegan.cogs.util.CircleTransformation;
import com.adamdonegan.cogs.util.DiscogsClient;
import com.adamdonegan.cogs.util.PreferencesManager;
import com.google.android.gms.vision.barcode.Barcode;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private boolean mReturningWithResult = false;

    private static final String CONSUMER_KEY = "tZplWaLrLakbPmeKDnNR";
    private static final String CONSUMER_SECRET = "WlvAHSrMKkEokrhICslQndFmlwjafEwW";
    private static final String USER_AGENT = "Cogs/0.1 +https://github.com/ajdons/Cogs";
    private ImageView userImage;
    private TextView textUsername;
    private TextView textActualName;
    private ProgressBar mProgress;
    Moshi moshi;
    DiscogsClient client;
    private PreferencesManager prefsManager;
    private LoadProfileTask task;
    private Profile profile;
    String barcode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Timber.plant(new Timber.DebugTree());
        moshi = new Moshi.Builder().build();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        prefsManager = PreferencesManager.getInstance();

        userImage = (ImageView) findViewById(R.id.userImage);
        textUsername = (TextView) findViewById(R.id.textUsername);
        textActualName = (TextView) findViewById(R.id.textActualName);
        mProgress = (ProgressBar) findViewById(R.id.progressBar);

        client = new DiscogsClient(CONSUMER_KEY, CONSUMER_SECRET, USER_AGENT, prefsManager.getValue("oauth_token"), prefsManager.getValue("oauth_token_secret"));
        Timber.d(prefsManager.getValue("oauth_token"));
        Timber.d(prefsManager.getValue("oauth_token_secret"));
        new LoadProfileTask().execute();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            barcode = data.getStringExtra("barcode");
            Timber.d(barcode);
            mReturningWithResult = true;

        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        //Returning from Barcode Activity
        if (mReturningWithResult) {

            new LoadSearchTask().execute(barcode);
        }
        // Reset the boolean flag back to false for next time.
        mReturningWithResult = false;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                searchView.clearFocus();
                new LoadSearchTask().execute(query);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {

            return true;
        }else if(id == R.id.action_barcode) {
            Timber.d("Launching Barcode Activity");
            Intent intent = new Intent(MainActivity.this, BarcodeScannerActivity.class);
            startActivityForResult(intent, 0);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager fragmentManager = getSupportFragmentManager();


        if (id == R.id.nav_profile) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, ProfileFragment.newInstance(R.id.nav_profile, null))
                    .commit();
        } else if (id == R.id.nav_collection) {

        } else if (id == R.id.nav_wantlist) {

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_contact) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public class LoadSearchTask extends AsyncTask<String, Void, Object> {
        SearchResults searchResults;
        @Override
        protected void onPreExecute() {
            mProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected Object doInBackground(String... params) {
            Timber.d("Retrieving search results...");
            try {
                String query = params[0];
                JsonAdapter<SearchResults> searchResultsAdapter = moshi.adapter(SearchResults.class);
                searchResults = searchResultsAdapter.fromJson(client.search(query));

            } catch (Exception e){
                e.printStackTrace();
            }
            return "Done";
        }

        @Override
        protected void onPostExecute(Object result) {
            Timber.d("Done.");
            mProgress.setVisibility(View.INVISIBLE);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, SearchResultFragment.newInstance(searchResults))
                    .commit();
        }
    }
    public class LoadProfileTask extends AsyncTask<String, Void, Object> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... strings) {
            Timber.d("Loading profile information...");
            try {
                JsonAdapter<Identity> identityAdapter = moshi.adapter(Identity.class);
                JsonAdapter<Profile> profileAdapter = moshi.adapter(Profile.class);
                Identity identity = identityAdapter.fromJson(client.identity());

                profile = profileAdapter.fromJson(client.genericGet(identity.getResource_url()));
            } catch (Exception e){
                e.printStackTrace();
            }
            return "Done";
        }

        @Override
        protected void onPostExecute(Object result) {
            Timber.d("Updating profile UI...");
            try {
                textUsername.setText(profile.getUsername());
                textActualName.setText(profile.getName());
                Picasso.with(getApplicationContext()).load(profile.getAvatar_url()).transform(new CircleTransformation()).into(userImage);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
