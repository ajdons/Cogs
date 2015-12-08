package com.adamdonegan.cogs.app;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adamdonegan.cogs.R;
import com.adamdonegan.cogs.models.Artist;
import com.adamdonegan.cogs.models.Image;
import com.adamdonegan.cogs.models.MasterRelease;
import com.adamdonegan.cogs.models.Release;
import com.adamdonegan.cogs.models.Track;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {
    private final String TYPE_MASTER = "master";
    private final String TYPE_RELEASE = "release";
    private final String TYPE_ARTIST = "artist";
    private final String TYPE_LABEL = "label";
    private RecyclerView mRecyclerView;
    private GenericAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ImageView image = (ImageView)findViewById(R.id.image);
        String jsonObject = getIntent().getStringExtra("jsonObject");
        String type = getIntent().getStringExtra("type");
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<MasterRelease> masterAdapter = moshi.adapter(MasterRelease.class);
        JsonAdapter<Release> releaseAdapter = moshi.adapter(Release.class);
        JsonAdapter<Artist> artistAdapter = moshi.adapter(Artist.class);


        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Set title of Detail page
        try {
            if (type.equals(TYPE_MASTER)) {
                MasterRelease master = masterAdapter.fromJson(jsonObject);
                mAdapter = new GenericAdapter(master);
                Picasso.with(this).load(largestImage(master.getImages()).getResource_url()).into(image);

            } else if (type.equals(TYPE_RELEASE)) {
                Release release = releaseAdapter.fromJson(jsonObject);
                mAdapter = new GenericAdapter(release);
                Picasso.with(this).load(largestImage(release.getImages()).getResource_url()).into(image);

            } else if (type.equals(TYPE_ARTIST)) {
                Artist artist = artistAdapter.fromJson(jsonObject);
                mAdapter = new GenericAdapter(artist);
                Picasso.with(this).load(largestImage(artist.getImages()).getResource_url()).into(image);
            } else if (type.equals(TYPE_LABEL)) {

            }
        } catch (Exception e){
            e.printStackTrace();
        }


        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(DetailActivity.this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

    }

    /**
     * Generic Adapter for single card
     */
    public class GenericAdapter extends RecyclerView.Adapter<GenericAdapter.ViewHolder> {

        public static final int LARGE = 0;
        public static final int TRACKLIST = 1;
        public static final int RATING = 3;
        public static final int RECYCLERVIEW_LIST = 4;
        private int[] cardTypes;
        private final Object object;

        public GenericAdapter(Object object) {
            this.object = object;
            if(object instanceof MasterRelease) {
                cardTypes = new int[]{LARGE, TRACKLIST};
            }
            else if(object instanceof  Release) {
                cardTypes = new int[]{LARGE, RECYCLERVIEW_LIST, RATING};
            }
            else if(object instanceof Artist) {
                cardTypes = new int[]{LARGE};
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;

            if(viewType == LARGE) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_large, parent, false);

                return new LargeViewHolder(view);
            }
            else if(viewType == TRACKLIST) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_empty, parent, false);

                return new TracklistViewHolder(view);
            }
            else if(viewType == RATING) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_rating, parent, false);

                return new RatingViewHolder(view);
            }
            else if(viewType == RECYCLERVIEW_LIST) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_recycler_view, parent, false);

                return new RecyclerListViewHolder(view);
            }

            return null;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            if(holder.getItemViewType() == LARGE && object instanceof MasterRelease) {
                MasterRelease master = (MasterRelease)object;

                LargeViewHolder viewHolder = (LargeViewHolder) holder;
                viewHolder.primaryTitle.setText(master.getTitle());
                viewHolder.secondaryTitle.setText(master.getArtists().get(0).get("name"));
                viewHolder.firstItem.setText("Year");
                viewHolder.firstItemValue.setText(master.getYear());
                viewHolder.secondItem.setText("Style");
                viewHolder.secondItemValue.setText(TextUtils.join(", ", master.getStyles()));
                viewHolder.thirdItem.setText("Genre");
                viewHolder.thirdItemValue.setText(TextUtils.join(", ", master.getGenres()));
            }
            else if(holder.getItemViewType() == LARGE && object instanceof Release) {
                Release release = (Release)object;

                LargeViewHolder viewHolder = (LargeViewHolder) holder;
                viewHolder.primaryTitle.setText(release.getTitle());
                viewHolder.secondaryTitle.setText(release.getArtists().get(0).get("name"));
                viewHolder.firstItem.setText("Format");
                viewHolder.firstItemValue.setText(release.getFormats().get(0).getName() + " - " + TextUtils.join(", ", release.getFormats().get(0).getDescriptions()));
                viewHolder.secondItem.setText("Release");
                viewHolder.secondItemValue.setText(release.getYear() + " - " + release.getCountry());
                viewHolder.thirdItem.setText("Label");
                viewHolder.thirdItemValue.setText(release.getLabels().get(0).getName());
            }
            else if(holder.getItemViewType() == LARGE && object instanceof Artist) {
                Artist artist = (Artist)object;

                LargeViewHolder viewHolder = (LargeViewHolder) holder;
                viewHolder.primaryTitle.setText(artist.getName());
                viewHolder.secondaryTitle.setVisibility(View.INVISIBLE);

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

                TracklistViewHolder viewHolder = (TracklistViewHolder) holder;

                for(Track track : trackList){
                    int margin = dpToPixels(getResources().getDimension(R.dimen.md_keylines_half) / 2);
                    int baseLine = View.generateViewId();

                    RelativeLayout relativeLayout = new RelativeLayout(getApplicationContext());

                    TextView trackPosition = new TextView(getApplicationContext());
                    trackPosition.setId(baseLine);
                    trackPosition.setText(track.getPosition());
                    trackPosition.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.text_secondary));
                    RelativeLayout.LayoutParams positionParams = new RelativeLayout.LayoutParams ( RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT );
                    positionParams.setMargins(margin, margin, 0, 0);
                    trackPosition.setLayoutParams(positionParams);

                    TextView trackTitle = new TextView(getApplicationContext());
                    trackTitle.setText(track.getTitle());
                    trackTitle.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.text_primary));
                    RelativeLayout.LayoutParams titleParams = new RelativeLayout.LayoutParams ( RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT );
                    titleParams.setMargins(margin, margin, 0, 0);

                    titleParams.addRule(RelativeLayout.RIGHT_OF, baseLine);
                    titleParams.addRule(RelativeLayout.ALIGN_BOTTOM, baseLine);
                    trackTitle.setLayoutParams(titleParams);

                    TextView trackLength = new TextView(getApplicationContext());
                    trackLength.setText(track.getDuration());
                    trackLength.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.text_secondary));
                    RelativeLayout.LayoutParams lengthParams = new RelativeLayout.LayoutParams ( RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT );
                    lengthParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                    lengthParams.addRule(RelativeLayout.ALIGN_BOTTOM, baseLine);
                    lengthParams.setMargins(0, margin, margin, 0);
                    trackLength.setLayoutParams(lengthParams);

                    View divider = new View(getApplicationContext());
                    divider.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.divider));
                    RelativeLayout.LayoutParams dividerParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 2);
                    dividerParams.setMargins(0, margin, 0, 0);
                    dividerParams.addRule(RelativeLayout.BELOW, baseLine);
                    divider.setLayoutParams(dividerParams);

                    relativeLayout.addView(trackPosition);
                    relativeLayout.addView(trackTitle);
                    relativeLayout.addView(trackLength);
                    relativeLayout.addView(divider);
                    viewHolder.layout.addView(relativeLayout);
                }
            }
            else if(holder.getItemViewType() == RATING) {
                RatingViewHolder viewHolder = (RatingViewHolder) holder;
                //TODO: rating action onChange
            }
            else if (holder.getItemViewType() == RECYCLERVIEW_LIST) {
                RecyclerListViewHolder viewHolder = (RecyclerListViewHolder) holder;
                Release release = (Release)object;
                CustomLinearLayoutManager customLayoutManager = new CustomLinearLayoutManager(DetailActivity.this,LinearLayoutManager.VERTICAL,false);

                viewHolder.recyclerView.setLayoutManager(customLayoutManager);
                viewHolder.recyclerView.setAdapter(new RecyclerceptionAdapter(release));
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

        public class LargeViewHolder extends ViewHolder {
            TextView primaryTitle;
            TextView secondaryTitle;
            TextView firstItem;
            TextView secondItem;
            TextView thirdItem;
            TextView firstItemValue;
            TextView secondItemValue;
            TextView thirdItemValue;

            public LargeViewHolder(View v) {
                super(v);
                this.primaryTitle = (TextView) v.findViewById(R.id.primaryTitle);
                this.secondaryTitle = (TextView) v.findViewById(R.id.secondaryTitle);
                this.firstItem = (TextView) v.findViewById(R.id.firstItem);
                this.secondItem = (TextView) v.findViewById(R.id.secondItem);
                this.thirdItem = (TextView) v.findViewById(R.id.thirdItem);
                this.firstItemValue = (TextView) v.findViewById(R.id.firstItemValue);
                this.secondItemValue = (TextView) v.findViewById(R.id.secondItemValue);
                this.thirdItemValue = (TextView) v.findViewById(R.id.thirdItemValue);
            }
        }

        public class TracklistViewHolder extends ViewHolder {
            LinearLayout layout;

            public TracklistViewHolder(View v) {
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

        public class RecyclerListViewHolder extends ViewHolder {
            RecyclerView recyclerView;

            public RecyclerListViewHolder(View v) {
                super(v);
                this.recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view_list);
            }
        }
    }

    public class CustomLinearLayoutManager extends LinearLayoutManager {
        public CustomLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);

        }

        // it will always pass false to RecyclerView when calling "canScrollVertically()" method.
        @Override
        public boolean canScrollVertically() {
            return false;
        }
    }

    public class RecyclerceptionAdapter extends RecyclerView.Adapter<RecyclerceptionAdapter.ListViewHolder> {
        // Set numbers of Card in RecyclerView.
        private Object object;

        public RecyclerceptionAdapter(Object object) {
            this.object = object;
        }

        @Override
        public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_list_item, parent, false);

            return new ListViewHolder(view);        }

        @Override
        public void onBindViewHolder(ListViewHolder holder, int position) {
            Release release = (Release) object;
            Track track = release.getTracklist().get(position);
            holder.text1.setText(track.getPosition());
            holder.text2.setText(track.getTitle());
            holder.text3.setText(track.getDuration());
        }

        @Override
        public int getItemCount() {
            Release release = (Release) object;
            return release.getTracklist().size();
        }

        public class ListViewHolder extends RecyclerView.ViewHolder{
            TextView text1;
            TextView text2;
            TextView text3;

            public ListViewHolder(View v) {
                super(v);
                text1 = (TextView) v.findViewById(R.id.text1);
                text2 = (TextView) v.findViewById(R.id.text2);
                text3 = (TextView) v.findViewById(R.id.text3);
            }
        }
    }
    private int dpToPixels(float dp)
    {
        float density = getApplicationContext().getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private Image largestImage(List<Image> images){
        Image currentLargest = images.get(0);

        for(Image i : images){
            if(Integer.parseInt(i.getWidth()) > Integer.parseInt(currentLargest.getWidth())) {
                currentLargest = i;
            }
        }
        return currentLargest;
    }
}
