package com.adamdonegan.cogs.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adamdonegan.cogs.R;
import com.adamdonegan.cogs.models.Artist;
import com.adamdonegan.cogs.models.ArtistReleaseVersions;
import com.adamdonegan.cogs.models.BasicInformation;
import com.adamdonegan.cogs.models.Collection;
import com.adamdonegan.cogs.models.CollectionRelease;
import com.adamdonegan.cogs.models.Image;
import com.adamdonegan.cogs.models.Label;
import com.adamdonegan.cogs.models.MasterRelease;
import com.adamdonegan.cogs.models.MasterReleaseVersions;
import com.adamdonegan.cogs.models.Member;
import com.adamdonegan.cogs.models.Release;
import com.adamdonegan.cogs.models.SearchResults;
import com.adamdonegan.cogs.models.Track;
import com.adamdonegan.cogs.models.Want;
import com.adamdonegan.cogs.util.DiscogsClient;
import com.adamdonegan.cogs.util.PreferencesManager;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class DetailActivity extends AppCompatActivity {
    private static final String CONSUMER_KEY = "tZplWaLrLakbPmeKDnNR";
    private static final String CONSUMER_SECRET = "WlvAHSrMKkEokrhICslQndFmlwjafEwW";
    private static final String USER_AGENT = "Cogs/0.1 +https://github.com/ajdons/Cogs";
    private final String TYPE_MASTER = "master";
    private final String TYPE_RELEASE = "release";
    private final String TYPE_ARTIST = "artist";
    private final String TYPE_LABEL = "label";
    private final String TYPE_COLLECTIONRELEASE = "collectionrelease";
    private final String TYPE_WANT = "want";
    private static final int RESULT_RELEASELIST = 999;
    private static DiscogsClient client;
    private static PreferencesManager prefsManager;
    private static Moshi moshi;

    private RecyclerView mRecyclerView;
    private GenericAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Release helperRelease;
    private String mUsername;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        prefsManager = PreferencesManager.getInstance();
        client = new DiscogsClient(CONSUMER_KEY, CONSUMER_SECRET, USER_AGENT, prefsManager.getValue("oauth_token"), prefsManager.getValue("oauth_token_secret"));
        moshi = new Moshi.Builder().build();
        mUsername = prefsManager.getValue("username");

        ImageView image = (ImageView)findViewById(R.id.image);
        String jsonObject = getIntent().getStringExtra("jsonObject");
        String type = getIntent().getStringExtra("type");

        JsonAdapter<MasterRelease> masterAdapter = moshi.adapter(MasterRelease.class);
        final JsonAdapter<Release> releaseAdapter = moshi.adapter(Release.class);
        JsonAdapter<Artist> artistAdapter = moshi.adapter(Artist.class);
        JsonAdapter<Label> labelAdapter = moshi.adapter(Label.class);
        JsonAdapter<CollectionRelease> cReleaseAdapter = moshi.adapter(CollectionRelease.class);
        JsonAdapter<Want> wantAdapter = moshi.adapter(Want.class);

        if(getIntent().getStringExtra("helperRelease") != null) {
            try{
                helperRelease = releaseAdapter.fromJson(getIntent().getStringExtra("helperRelease"));
            } catch (Exception e){
                e.printStackTrace();
            }
        }


        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Set title of Detail page
        try {
            if (type.equals(TYPE_MASTER)) {
                MasterRelease master = masterAdapter.fromJson(jsonObject);
                mAdapter = new GenericAdapter(master);
                Picasso.with(this).load(primaryImage(master.getImages()).getResource_url()).into(image);

            } else if (type.equals(TYPE_RELEASE)) {
                Release release = releaseAdapter.fromJson(jsonObject);
                mAdapter = new GenericAdapter(release);
                Picasso.with(this).load(primaryImage(release.getImages()).getResource_url()).into(image);
                setupFab(release);

            } else if (type.equals(TYPE_ARTIST)) {
                Artist artist = artistAdapter.fromJson(jsonObject);
                mAdapter = new GenericAdapter(artist);
                Picasso.with(this).load(primaryImage(artist.getImages()).getResource_url()).into(image);

            } else if (type.equals(TYPE_LABEL)) {
                Label label = labelAdapter.fromJson(jsonObject);
                mAdapter = new GenericAdapter(label);
                Picasso.with(this).load(primaryImage(label.getImages()).getResource_url()).into(image);

            } else if (type.equals(TYPE_COLLECTIONRELEASE)) {
                final CollectionRelease collectionRelease = cReleaseAdapter.fromJson(jsonObject);
                mAdapter = new GenericAdapter(collectionRelease);
                Picasso.with(this).load(primaryImage(helperRelease.getImages()).getResource_url()).into(image);

            } else if (type.equals(TYPE_WANT)) {
                final Want want = wantAdapter.fromJson(jsonObject);
                mAdapter = new GenericAdapter(want);
                Picasso.with(this).load(primaryImage(helperRelease.getImages()).getResource_url()).into(image);

            }
        } catch (Exception e){
            e.printStackTrace();
        }


        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(DetailActivity.this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);
    }

    public void setupFab(final Release release) {
        final FloatingActionMenu menu = (FloatingActionMenu) findViewById(R.id.fabmenu);

        menu.setVisibility(View.VISIBLE);
        final FloatingActionButton collectionFab = new FloatingActionButton(this);
        collectionFab.setButtonSize(FloatingActionButton.SIZE_NORMAL);
        collectionFab.setLabelText(getString(R.string.add_to_collection));
        collectionFab.setImageResource(R.mipmap.ic_package_variant);
        collectionFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        Timber.d(client.addReleaseToFolder(mUsername, "1", release.getId()));
                    }
                });
                Snackbar.make(v, R.string.added_collection, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                menu.toggle(true);
            }
        });

        final FloatingActionButton wantlistFab = new FloatingActionButton(this);
        wantlistFab.setButtonSize(FloatingActionButton.SIZE_NORMAL);
        wantlistFab.setLabelText(getString(R.string.add_to_wantlist));
        wantlistFab.setImageResource(R.mipmap.ic_heart);
        wantlistFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        client.addToWantlist(mUsername, release.getId());
                    }
                });
                Snackbar.make(v, R.string.added_wantlist, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                menu.toggle(true);
            }
        });
        menu.addMenuButton(collectionFab);
        menu.addMenuButton(wantlistFab);

        menu.setOnMenuButtonClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                menu.toggle(true);
            }
        });
        menu.setClosedOnTouchOutside(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Generic Adapter for single card
     */
    public class GenericAdapter extends RecyclerView.Adapter<GenericAdapter.ViewHolder> {

        public static final int LARGE_DATA = 0;
        public static final int TRACKLIST = 1;
        public static final int LARGE_DESC = 2;
        public static final int RATING = 3;
        public static final int MEMBER_LIST = 4;
        public static final int ITEMICON = 5;
        private int[] cardTypes;
        private final Object object;

        public GenericAdapter(Object object) {
            this.object = object;
            if(object instanceof MasterRelease) {
                cardTypes = new int[]{LARGE_DATA, TRACKLIST, ITEMICON};
            }
            else if(object instanceof  Release) {
                cardTypes = new int[]{LARGE_DATA, TRACKLIST};
            }
            else if(object instanceof Artist) {
                cardTypes = new int[]{LARGE_DESC, MEMBER_LIST, ITEMICON};
            }
            else if(object instanceof Label) {
                cardTypes = new int[]{LARGE_DESC, ITEMICON};
            }
            else if(object instanceof CollectionRelease) {
                cardTypes = new int[]{LARGE_DATA, TRACKLIST, RATING};
            }
            else if(object instanceof Want) {
                cardTypes = new int[]{LARGE_DATA, TRACKLIST, RATING};
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;

            if(viewType == LARGE_DATA) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_large_data, parent, false);

                return new LargeDataViewHolder(view);
            }
            else if(viewType == LARGE_DESC) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_large_decription, parent, false);

                return new LargeDescriptionViewHolder(view);
            }
            else if(viewType == TRACKLIST) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_empty, parent, false);

                return new TracklistViewHolder(view);
            }
            else if(viewType == RATING) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_rating, parent, false);

                return new RatingViewHolder(view);
            }
            else if(viewType == MEMBER_LIST) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_empty, parent, false);

                return new MemberlistViewHolder(view);
            }
            else if(viewType == ITEMICON) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item_icon, parent, false);

                return new ItemIconViewHolder(view);
            }

            return null;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {

            if(holder.getItemViewType() == LARGE_DATA && object instanceof MasterRelease) {
                MasterRelease master = (MasterRelease)object;
                String masterTitle = "";
                String masterArtist = "";
                String masterYear = "";
                String masterStyles = "";
                String masterGenres = "";

                if(master.getTitle() != null)
                    masterTitle = master.getTitle();
                if(master.getArtists() != null && !master.getArtists().isEmpty())
                    masterArtist = master.getArtists().get(0).get("name");
                if(master.getYear() != null)
                    masterYear = master.getYear();
                if(master.getStyles() != null && !master.getStyles().isEmpty())
                    masterStyles = TextUtils.join(", ", master.getStyles());
                if(master.getGenres() != null && !master.getGenres().isEmpty())
                    masterGenres = TextUtils.join(", ", master.getGenres());

                LargeDataViewHolder viewHolder = (LargeDataViewHolder) holder;
                viewHolder.primaryTitle.setText(masterTitle);
                viewHolder.secondaryTitle.setText(masterArtist);
                viewHolder.firstItem.setText(R.string.year);
                viewHolder.firstItemValue.setText(masterYear);
                viewHolder.secondItem.setText(R.string.style);
                viewHolder.secondItemValue.setText(masterStyles);
                viewHolder.thirdItem.setText(R.string.genre);
                viewHolder.thirdItemValue.setText(masterGenres);
            }
            else if(holder.getItemViewType() == LARGE_DATA && object instanceof Release) {
                Release release = (Release)object;
                String releaseTitle = "";
                String releaseArtist = "";
                String releaseFormat = "";
                String releaseInfo = "";
                String releaseLabel = "";

                if(release.getTitle() != null)
                    releaseTitle = release.getTitle();
                if(release.getArtists() != null && !release.getArtists().isEmpty())
                    releaseArtist = release.getArtists().get(0).get("name");
                if(release.getFormats() != null && !release.getFormats().isEmpty())
                    releaseFormat = release.getFormats().get(0).getName() + " - " + TextUtils.join(", ", release.getFormats().get(0).getDescriptions());
                if(release.getYear() != null && release.getCountry() != null)
                    releaseInfo = release.getYear() + " - " + release.getCountry();
                if(release.getLabels() != null && !release.getLabels().isEmpty())
                    releaseLabel = release.getLabels().get(0).getName();

                LargeDataViewHolder viewHolder = (LargeDataViewHolder) holder;
                viewHolder.primaryTitle.setText(releaseTitle);
                viewHolder.secondaryTitle.setText(releaseArtist);
                viewHolder.firstItem.setText(R.string.format);
                viewHolder.firstItemValue.setText(releaseFormat);
                viewHolder.secondItem.setText(R.string.release);
                viewHolder.secondItemValue.setText(releaseInfo);
                viewHolder.thirdItem.setText(R.string.label);
                viewHolder.thirdItemValue.setText(releaseLabel);
            }
            else if(holder.getItemViewType() == LARGE_DATA && object instanceof CollectionRelease) {
                //Since a CollectionRelease object only contains limited information, the full Release object is loaded as a helper
                CollectionRelease collectionRelease = (CollectionRelease)object;
                BasicInformation basicInfo = collectionRelease.getBasic_information();
                String releaseTitle = "";
                String releaseArtist = "";
                String releaseFormat = "";
                String releaseInfo = "";
                String releaseLabel = "";

                if(helperRelease.getTitle() != null)
                    releaseTitle = helperRelease.getTitle();
                if(helperRelease.getArtists() != null && !helperRelease.getArtists().isEmpty())
                    releaseArtist = helperRelease.getArtists().get(0).get("name");
                if(helperRelease.getFormats() != null && !helperRelease.getFormats().isEmpty())
                    releaseFormat = helperRelease.getFormats().get(0).getName() + " - " + TextUtils.join(", ", helperRelease.getFormats().get(0).getDescriptions());
                if(helperRelease.getYear() != null && helperRelease.getCountry() != null)
                    releaseInfo = helperRelease.getYear() + " - " + helperRelease.getCountry();
                if(helperRelease.getLabels() != null && !helperRelease.getLabels().isEmpty())
                    releaseLabel = helperRelease.getLabels().get(0).getName();

                LargeDataViewHolder viewHolder = (LargeDataViewHolder) holder;
                viewHolder.primaryTitle.setText(releaseTitle);
                viewHolder.secondaryTitle.setText(releaseArtist);
                viewHolder.firstItem.setText(R.string.format);
                viewHolder.firstItemValue.setText(releaseFormat);
                viewHolder.secondItem.setText(R.string.release);
                viewHolder.secondItemValue.setText(releaseInfo);
                viewHolder.thirdItem.setText(R.string.label);
                viewHolder.thirdItemValue.setText(releaseLabel);
            }

            else if(holder.getItemViewType() == LARGE_DATA && object instanceof Want) {
                //Since a Want object only contains limited information, the full Release object is loaded as a helper
                Want want = (Want)object;
                BasicInformation basicInfo = want.getBasic_information();
                String wantTitle = "";
                String wantArtist = "";
                String wantFormat = "";
                String wantInfo = "";
                String wantLabel = "";

                if(helperRelease.getTitle() != null)
                    wantTitle = helperRelease.getTitle();
                if(helperRelease.getArtists() != null && !helperRelease.getArtists().isEmpty())
                    wantArtist = helperRelease.getArtists().get(0).get("name");
                if(helperRelease.getFormats() != null && !helperRelease.getFormats().isEmpty())
                    wantFormat = helperRelease.getFormats().get(0).getName() + " - " + TextUtils.join(", ", helperRelease.getFormats().get(0).getDescriptions());
                if(helperRelease.getYear() != null && helperRelease.getCountry() != null)
                    wantInfo = helperRelease.getYear() + " - " + helperRelease.getCountry();
                if(helperRelease.getLabels() != null && !helperRelease.getLabels().isEmpty())
                    wantLabel = helperRelease.getLabels().get(0).getName();

                LargeDataViewHolder viewHolder = (LargeDataViewHolder) holder;
                viewHolder.primaryTitle.setText(wantTitle);
                viewHolder.secondaryTitle.setText(wantArtist);
                viewHolder.firstItem.setText(R.string.format);
                viewHolder.firstItemValue.setText(wantFormat);
                viewHolder.secondItem.setText(R.string.release);
                viewHolder.secondItemValue.setText(wantInfo);
                viewHolder.thirdItem.setText(R.string.label);
                viewHolder.thirdItemValue.setText(wantLabel);
            }

            else if(holder.getItemViewType() == LARGE_DESC && object instanceof Artist) {
                final Artist artist = (Artist)object;

                LargeDescriptionViewHolder viewHolder = (LargeDescriptionViewHolder) holder;
                viewHolder.primaryTitle.setText(artist.getName());
                if(artist.getRealname() != null)
                    viewHolder.secondaryTitle.setText(artist.getRealname());
                else
                    viewHolder.secondaryTitle.setVisibility(View.GONE);
                viewHolder.description.setText(artist.getProfile());
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Timber.d(artist.getUrls().toString());
                    }
                });
            }
            else if(holder.getItemViewType() == LARGE_DESC && object instanceof Label) {
                Label label = (Label)object;

                LargeDescriptionViewHolder viewHolder = (LargeDescriptionViewHolder) holder;
                viewHolder.primaryTitle.setText(label.getName());
                viewHolder.secondaryTitle.setVisibility(View.GONE);
                viewHolder.description.setText(label.getProfile());
            }
            else if(holder.getItemViewType() == TRACKLIST) {
                List<Track> trackList = new ArrayList();

                if(object instanceof MasterRelease) {
                    MasterRelease master = (MasterRelease) object;
                    trackList = master.getTracklist();
                }
                else if(object instanceof Release) {
                    Release release = (Release) object;
                    trackList = release.getTracklist();
                }
                else if(object instanceof  CollectionRelease) {
                    trackList = helperRelease.getTracklist();
                }
                else if(object instanceof Want) {
                    trackList = helperRelease.getTracklist();
                }

                TracklistViewHolder viewHolder = (TracklistViewHolder) holder;

                for(Track track : trackList){
                    int margin = dpToPixels(getResources().getDimension(R.dimen.md_keylines_half) / 2);
                    int baseLine = View.generateViewId();

                    String trackPosition = "";
                    String trackTitle = "";
                    String trackDuration = "";

                    if(track.getPosition() != null)
                        trackPosition = track.getPosition();
                    if(track.getTitle() != null)
                        trackTitle = track.getTitle();
                    if(track.getDuration() != null)
                        trackDuration = track.getDuration();

                    //Ugly alert: Programatically creating a list inside a CardView
                    RelativeLayout relativeLayout = new RelativeLayout(getApplicationContext());

                    TextView trackPositionText = new TextView(getApplicationContext());
                    trackPositionText.setId(baseLine);
                    trackPositionText.setText(trackPosition);
                    trackPositionText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.text_secondary));
                    RelativeLayout.LayoutParams positionParams = new RelativeLayout.LayoutParams ( RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT );
                    positionParams.setMargins(margin, margin, 0, 0);
                    trackPositionText.setLayoutParams(positionParams);

                    TextView trackTitleText = new TextView(getApplicationContext());
                    trackTitleText.setText(trackTitle);
                    trackTitleText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.text_primary));
                    RelativeLayout.LayoutParams titleParams = new RelativeLayout.LayoutParams ( RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT );
                    titleParams.setMargins(margin, margin, 0, 0);

                    titleParams.addRule(RelativeLayout.RIGHT_OF, baseLine);
                    titleParams.addRule(RelativeLayout.ALIGN_BOTTOM, baseLine);
                    trackTitleText.setLayoutParams(titleParams);

                    TextView trackDurationText = new TextView(getApplicationContext());
                    trackDurationText.setText(trackDuration);
                    trackDurationText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.text_secondary));
                    RelativeLayout.LayoutParams lengthParams = new RelativeLayout.LayoutParams ( RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT );
                    lengthParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                    lengthParams.addRule(RelativeLayout.ALIGN_BOTTOM, baseLine);
                    lengthParams.setMargins(0, margin, margin, 0);
                    trackDurationText.setLayoutParams(lengthParams);

                    View divider = new View(getApplicationContext());
                    divider.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.divider));
                    RelativeLayout.LayoutParams dividerParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 2);
                    dividerParams.setMargins(0, margin, 0, 0);
                    dividerParams.addRule(RelativeLayout.BELOW, baseLine);
                    divider.setLayoutParams(dividerParams);

                    relativeLayout.addView(trackPositionText);
                    relativeLayout.addView(trackTitleText);
                    relativeLayout.addView(trackDurationText);
                    relativeLayout.addView(divider);
                    viewHolder.layout.addView(relativeLayout);
                }
            }
            else if(holder.getItemViewType() == MEMBER_LIST) {
                List<Member> memberList = new ArrayList();

                Artist artist = (Artist)object;
                if(artist.getMembers() == null){
                    Member singleMember = new Member();
                    singleMember.setName(artist.getName());
                    singleMember.setActive(true);
                    memberList.add(singleMember);
                }
                else
                    memberList.addAll(artist.getMembers());


                MemberlistViewHolder viewHolder = (MemberlistViewHolder) holder;

                for(Member member : memberList){
                    int margin = dpToPixels(getResources().getDimension(R.dimen.md_keylines_half) / 2);
                    int baseLine = View.generateViewId();
                    String memberName = "";

                    if(member.getName() != null)
                        memberName = member.getName();

                    //Ugly alert: Programatically creating a list inside a CardView
                    RelativeLayout relativeLayout = new RelativeLayout(getApplicationContext());

                    TextView memberNameText = new TextView(getApplicationContext());
                    memberNameText.setText(memberName);
                    memberNameText.setId(baseLine);
                    memberNameText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.text_primary));
                    RelativeLayout.LayoutParams positionParams = new RelativeLayout.LayoutParams ( RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT );
                    positionParams.setMargins(margin, margin, 0, 0);
                    memberNameText.setLayoutParams(positionParams);

                    TextView memberStatusText = new TextView(getApplicationContext());
                    if(member.isActive())
                        memberStatusText.setText(R.string.active);
                    else
                        memberStatusText.setText(R.string.inactive);
                    memberStatusText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.text_secondary));
                    RelativeLayout.LayoutParams lengthParams = new RelativeLayout.LayoutParams ( RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT );
                    lengthParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                    lengthParams.addRule(RelativeLayout.ALIGN_BOTTOM, baseLine);
                    lengthParams.setMargins(0, margin, margin, 0);
                    memberStatusText.setLayoutParams(lengthParams);

                    View divider = new View(getApplicationContext());
                    divider.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.divider));
                    RelativeLayout.LayoutParams dividerParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 2);
                    dividerParams.setMargins(0, margin, 0, 0);
                    dividerParams.addRule(RelativeLayout.BELOW, baseLine);
                    divider.setLayoutParams(dividerParams);

                    relativeLayout.addView(memberNameText);
                    relativeLayout.addView(memberStatusText);
                    relativeLayout.addView(divider);
                    viewHolder.layout.addView(relativeLayout);
                }
            }
            else if(holder.getItemViewType() == RATING && object instanceof CollectionRelease) {
                final CollectionRelease collectionRelease = (CollectionRelease)object;
                RatingViewHolder viewHolder = (RatingViewHolder) holder;
                viewHolder.ratingBar.setRating(Float.parseFloat(collectionRelease.getRating()));
                viewHolder.ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, final float rating, boolean fromUser) {
                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("rating", String.valueOf((int)rating));
                                Timber.d(client.updateInstanceInFolder(mUsername, "1", helperRelease.getId(), collectionRelease.getInstance_id(), params));
                            }
                        });
                        Snackbar.make(holder.itemView, R.string.rating_updated, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                });

            }
            else if(holder.getItemViewType() == RATING && object instanceof Want) {
                final Want want = (Want)object;
                RatingViewHolder viewHolder = (RatingViewHolder) holder;
                viewHolder.ratingBar.setRating(Float.parseFloat(want.getRating()));
                viewHolder.ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, final float rating, boolean fromUser) {
                        Timber.d("Rating changed" + rating);
                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("rating", String.valueOf((int) rating));
                                Timber.d(client.genericPost(want.getResource_url(), params));
                            }
                        });
                        Snackbar.make(holder.itemView, R.string.rating_updated, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                });
            }
            else if(holder.getItemViewType() == ITEMICON) {
                ItemIconViewHolder viewHolder = (ItemIconViewHolder) holder;

                if(object instanceof MasterRelease){
                    final MasterRelease master = (MasterRelease) object;
                    viewHolder.title.setText(R.string.view_release_versions);
//TODO: For Master Release, Artist, Label- send user to list of releases
//                    viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            new LoadReleaseListTask().execute(master.getVersions_url(), master.getTitle());
//                        }
//                    });

                }
                else if(object instanceof Artist){
                    Artist artist = (Artist) object;
                    viewHolder.title.setText(R.string.view_releases);
                }
                else if(object instanceof Label){
                    Label label = (Label) object;
                    viewHolder.title.setText(R.string.view_releases);
                }
            }
        }

        @Override
        public int getItemCount() {
            return cardTypes.length;
        }

        @Override
        public int getItemViewType(int position) {
            return cardTypes[position];
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public ViewHolder(View v) {
                super(v);
            }
        }

        public class LargeDataViewHolder extends ViewHolder {
            TextView primaryTitle;
            TextView secondaryTitle;
            TextView description;
            TextView firstItem;
            TextView secondItem;
            TextView thirdItem;
            TextView firstItemValue;
            TextView secondItemValue;
            TextView thirdItemValue;

            public LargeDataViewHolder(View v) {
                super(v);
                this.primaryTitle = (TextView) v.findViewById(R.id.primaryTitle);
                this.secondaryTitle = (TextView) v.findViewById(R.id.heading2);
                this.description = (TextView) v.findViewById(R.id.description);
                this.firstItem = (TextView) v.findViewById(R.id.firstItem);
                this.secondItem = (TextView) v.findViewById(R.id.secondItem);
                this.thirdItem = (TextView) v.findViewById(R.id.thirdItem);
                this.firstItemValue = (TextView) v.findViewById(R.id.firstItemValue);
                this.secondItemValue = (TextView) v.findViewById(R.id.secondItemValue);
                this.thirdItemValue = (TextView) v.findViewById(R.id.thirdItemValue);
            }
        }

        public class LargeDescriptionViewHolder extends ViewHolder {
            TextView primaryTitle;
            TextView secondaryTitle;
            TextView description;

            public LargeDescriptionViewHolder(View v) {
                super(v);
                this.primaryTitle = (TextView) v.findViewById(R.id.primaryTitle);
                this.secondaryTitle = (TextView) v.findViewById(R.id.heading2);
                this.description = (TextView) v.findViewById(R.id.description);
            }
        }

        public class TracklistViewHolder extends ViewHolder {
            LinearLayout layout;

            public TracklistViewHolder(View v) {
                super(v);
                this.layout = (LinearLayout) v.findViewById(R.id.linear_layout_list);
            }
        }

        public class MemberlistViewHolder extends ViewHolder {
            LinearLayout layout;

            public MemberlistViewHolder(View v) {
                super(v);
                this.layout = (LinearLayout) v.findViewById(R.id.linear_layout_list);
            }
        }

        public class RatingViewHolder extends ViewHolder {
            RatingBar ratingBar;

            public RatingViewHolder(View v) {
                super(v);
                this.ratingBar = (RatingBar) v.findViewById(R.id.ratingBar);
            }
        }

        public class ItemIconViewHolder extends ViewHolder {
            TextView title;
            Button button;
            public ItemIconViewHolder(View v){
                super(v);
                this.title = (TextView) v.findViewById(R.id.title);
                this.button = (Button) v.findViewById(R.id.button);
            }
        }
    }

    public class LoadReleaseListTask extends AsyncTask<String, Void, Object> {
        String heading1;
        String heading2;
        String jsonObject;
        @Override
        protected void onPreExecute() {


        }

        @Override
        protected Object doInBackground(String... params) {
            try {
                String masterURL = params[0];
                String masterTitle = params[1];

                jsonObject =  client.genericGet(masterURL);
                heading1 =  getString(R.string.versions_of);
                heading2 =  masterTitle;

            } catch (Exception e){
                e.printStackTrace();
            }
            return "Done";
        }

        @Override
        protected void onPostExecute(Object result) {
            Intent returnIntent = getIntent();
            returnIntent.putExtra("jsonObject", jsonObject);
            returnIntent.putExtra("heading1", heading1);
            returnIntent.putExtra("heading2", heading2);
            setResult(Activity.RESULT_OK, returnIntent);
            DetailActivity.this.finish();
        }
    }

    private int dpToPixels(float dp)
    {
        float density = getApplicationContext().getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private Image primaryImage(List<Image> images){
        Image currentPrimary = images.get(0);

        for(Image i : images){
            if(i.getType() == "primary") {
                return i;
            }
        }
        return currentPrimary;
    }
}
