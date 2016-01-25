package com.adamdonegan.cogs.app;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.adamdonegan.cogs.R;
import com.adamdonegan.cogs.models.Collection;
import com.adamdonegan.cogs.models.CollectionRelease;
import com.adamdonegan.cogs.models.Identity;
import com.adamdonegan.cogs.models.MasterReleaseVersions;
import com.adamdonegan.cogs.models.Pagination;
import com.adamdonegan.cogs.models.Profile;
import com.adamdonegan.cogs.models.Release;
import com.adamdonegan.cogs.models.SearchResults;
import com.adamdonegan.cogs.models.Version;
import com.adamdonegan.cogs.models.Want;
import com.adamdonegan.cogs.models.Wantlist;
import com.adamdonegan.cogs.util.CircleTransformation;
import com.adamdonegan.cogs.util.DiscogsClient;
import com.adamdonegan.cogs.util.PreferencesManager;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private boolean mReturningWithBarcode = false;

    private static final String CONSUMER_KEY = "tZplWaLrLakbPmeKDnNR";
    private static final String CONSUMER_SECRET = "WlvAHSrMKkEokrhICslQndFmlwjafEwW";
    private static final String USER_AGENT = "Cogs/0.1 +https://github.com/ajdons/Cogs";
    private static final int BARCODE_ACTIVITY = 100;
    private static final int DETAIL_ACTIVITY = 101;
    private ImageView userImage;
    private TextView textUsername;
    private TextView textActualName;
    private ProgressBar mProgress;
    Moshi moshi;
    DiscogsClient client;
    private PreferencesManager prefsManager;
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

        if (requestCode == BARCODE_ACTIVITY && resultCode == Activity.RESULT_OK) {
            barcode = data.getStringExtra("barcode");
            Timber.d(barcode);
            mReturningWithBarcode = true;

        }
        else if(requestCode == DETAIL_ACTIVITY && resultCode == Activity.RESULT_OK){
            //TODO: For Master Release, Artist, Label- send user to list of releases
//            try {
//                JsonAdapter<MasterReleaseVersions> releaseVersionsJsonAdapter = moshi.adapter(MasterReleaseVersions.class);
//                MasterReleaseVersions masterReleaseVersions = releaseVersionsJsonAdapter.fromJson(data.getStringExtra("jsonObject"));
//                getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.container, ListReleasesFragment.newInstance(masterReleaseVersions.getVersions(), data.getStringExtra("heading1"), data.getStringExtra("heading2")))
//                        .commit();
//            }catch(Exception e){
//                e.printStackTrace();
//            }
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        //Returning from Barcode Activity
        if (mReturningWithBarcode) {

            new LoadSearchTask().execute(barcode);
        }
        // Reset the flag back to false for next time.
        mReturningWithBarcode = false;
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

        if (id == R.id.action_search) {
        //Search action handled above
            return true;
        }else if(id == R.id.action_barcode) {
            Timber.d("Launching Barcode Activity");
            Intent intent = new Intent(MainActivity.this, BarcodeScannerActivity.class);
            startActivityForResult(intent, BARCODE_ACTIVITY);

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


        if (id == R.id.nav_collection) {
            new LoadCollectionTask().execute();
        } else if (id == R.id.nav_wantlist) {
            new LoadWantlistTask().execute();
        } else if (id == R.id.nav_contact) {
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("plain/text");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { getString(R.string.my_email) });
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject));
            startActivity(Intent.createChooser(emailIntent, getString(R.string.email)));

        } else if (id == R.id.nav_logout) {
            new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.logout_confirmation))
                    .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            prefsManager.clear();
                            Intent logoutIntent = new Intent(MainActivity.this, LoginActivity.class);
                            logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(logoutIntent);
                        }
                    })
                    .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public class LoadSearchTask extends AsyncTask<String, Void, Object> {
        SearchResults searchResults;
        String query;
        @Override
        protected void onPreExecute() {
            mProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected Object doInBackground(String... params) {
            Timber.d("Retrieving search results...");
            try {
                query = params[0];
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
                    .replace(R.id.container, SearchResultFragment.newInstance(searchResults, getResources().getString(R.string.search_results), query))
                    .commit();
        }
    }

    public class LoadCollectionTask extends AsyncTask<String, Void, Object> {
        Collection collection;

        @Override
        protected void onPreExecute() {
            mProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected Object doInBackground(String... params) {
            Timber.d("Loading user's collection...");
            try {
                JsonAdapter<Collection> collectionAdapter = moshi.adapter(Collection.class);
                collection = collectionAdapter.fromJson(loadEntireCollection(profile.getUsername()));

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
                    .replace(R.id.container, CollectionFragment.newInstance(collection, profile))
                    .commit();
        }
    }

    public class LoadWantlistTask extends AsyncTask<String, Void, Object> {
        Wantlist wantlist;

        @Override
        protected void onPreExecute() {
            mProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected Object doInBackground(String... params) {
            Timber.d("Loading user's wantlist...");
            try {
                JsonAdapter<Wantlist> collectionAdapter = moshi.adapter(Wantlist.class);
                wantlist = collectionAdapter.fromJson(loadEntireWantlist(profile.getUsername()));

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
                    .replace(R.id.container, WantlistFragment.newInstance(wantlist))
                    .commit();
        }
    }

    public class LoadProfileTask extends AsyncTask<String, Void, Object> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... strings) {
            Timber.d("Loading user's profile information...");
            try {
                JsonAdapter<Identity> identityAdapter = moshi.adapter(Identity.class);
                JsonAdapter<Profile> profileAdapter = moshi.adapter(Profile.class);
                Identity identity = identityAdapter.fromJson(client.identity());

                profile = profileAdapter.fromJson(client.genericGet(identity.getResource_url()));
                prefsManager.setValue("username", profile.getUsername());
            } catch (Exception e){
                e.printStackTrace();
            }
            return "Done";
        }

        @Override
        protected void onPostExecute(Object result) {
            Timber.d("Done.");
            try {
                textUsername.setText(profile.getUsername());
                textActualName.setText(profile.getName());
                Picasso.with(getApplicationContext()).load(profile.getAvatar_url()).transform(new CircleTransformation()).into(userImage);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public String loadEntireCollection(String username){
        JsonAdapter<Collection> collectionAdapter = moshi.adapter(Collection.class);
        List<CollectionRelease> allReleases = new ArrayList<>();

        try {
            Map<String, String> extraParams = new HashMap<>();
            extraParams.put("per_page", "100");

            Collection currentCollection = collectionAdapter.fromJson(client.collectionReleases(username, "0", extraParams));
            Pagination currentPagination = currentCollection.getPagination();
            List<CollectionRelease> currentReleases = currentCollection.getReleases();
            allReleases.addAll(currentReleases);

            if(currentPagination.getUrls() != null) {
                while (currentPagination.getUrls().get("next") != null) {
                    String nextUrl = currentPagination.getUrls().get("next");
                    currentCollection = collectionAdapter.fromJson(client.genericGet(nextUrl));
                    currentPagination = currentCollection.getPagination();
                    currentReleases = currentCollection.getReleases();
                    allReleases.addAll(currentReleases);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        Collection entireCollection = new Collection();
        entireCollection.setReleases(allReleases);
        return collectionAdapter.toJson(entireCollection);
    }

    public String loadEntireWantlist(String username){
        JsonAdapter<Wantlist> wantlistAdapter = moshi.adapter(Wantlist.class);
        List<Want> allWants = new ArrayList<>();

        try {
            Map<String, String> extraParams = new HashMap<>();
            extraParams.put("per_page", "100");

            Wantlist currentWantlist = wantlistAdapter.fromJson(client.wantlist(username, extraParams));
            Pagination currentPagination = currentWantlist.getPagination();
            List<Want> currentWants = currentWantlist.getWants();
            allWants.addAll(currentWants);

            if(currentPagination.getUrls() != null) {
                while (currentPagination.getUrls().get("next") != null) {
                    String nextUrl = currentPagination.getUrls().get("next");
                    currentWantlist = wantlistAdapter.fromJson(client.genericGet(nextUrl));
                    currentPagination = currentWantlist.getPagination();
                    currentWants = currentWantlist.getWants();
                    allWants.addAll(currentWants);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        Wantlist entireWantlist = new Wantlist();
        entireWantlist.setWants(allWants);
        return wantlistAdapter.toJson(entireWantlist);
    }

    public boolean isInCollection(Collection collection, String release_id){
        for(CollectionRelease release : collection.getReleases()) {
            if(release.getId().equals(release_id))
                return true;
        }
        return false;
    }
}
