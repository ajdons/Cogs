package com.adamdonegan.cogs.app;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.adamdonegan.cogs.R;
import com.adamdonegan.cogs.models.Release;
import com.adamdonegan.cogs.models.Version;
import com.adamdonegan.cogs.util.DiscogsClient;
import com.adamdonegan.cogs.util.PreferencesManager;
import com.bartoszlipinski.recyclerviewheader.RecyclerViewHeader;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by AdamDonegan.
 */
public class ListReleasesFragment extends Fragment {
    private static final String CONSUMER_KEY = "tZplWaLrLakbPmeKDnNR";
    private static final String CONSUMER_SECRET = "WlvAHSrMKkEokrhICslQndFmlwjafEwW";
    private static final String USER_AGENT = "Cogs/0.1 +https://github.com/ajdons/Cogs";

    private static List<Version> mReleases;
    private static String mHeading1;
    private static String mHeading2;
    private static Moshi moshi;
    private static PreferencesManager prefsManager;
    private static DiscogsClient client;

    public static ListReleasesFragment newInstance(List<Version> releases, String header1, String header2) {
        ListReleasesFragment fragment = new ListReleasesFragment();
        mReleases = releases;
        mHeading1 = header1;
        mHeading2 = header2;
        moshi = new Moshi.Builder().build();
        prefsManager = PreferencesManager.getInstance();
        client = new DiscogsClient(CONSUMER_KEY, CONSUMER_SECRET, USER_AGENT, prefsManager.getValue("oauth_token"), prefsManager.getValue("oauth_token_secret"));

        return fragment;
    }

    public ListReleasesFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recycler_with_header, container, false);

        RecyclerViewHeader header = (RecyclerViewHeader) rootView.findViewById(R.id.header);
        TextView heading1 = (TextView) rootView.findViewById(R.id.heading1);
        TextView heading2 = (TextView) rootView.findViewById(R.id.heading2);
        heading1.setText(mHeading1);
        heading2.setText(mHeading2);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        ListReleasesAdapter adapter = new ListReleasesAdapter(getActivity().getApplicationContext(), mReleases);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        header.attachTo(recyclerView, true);

        return rootView;
    }

    public class ListReleasesAdapter extends RecyclerView.Adapter<ListReleasesAdapter.ViewHolder>
    {
        private List<Version> mReleases;
        private Context mContext;

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView image;
            TextView title;
            TextView text;
            TextView textSecondary;
            Button button1;
            Button button2;

            public ViewHolder(View v) {
                super(v);
                image = (ImageView) v.findViewById(R.id.card_image);
                title = (TextView) v.findViewById(R.id.card_title);
                text = (TextView) v.findViewById(R.id.card_text);
                textSecondary = (TextView) v.findViewById(R.id.card_text_secondary);
                button1 = (Button) v.findViewById(R.id.button1);
                button2 = (Button) v.findViewById(R.id.button2);
            }

        }

        public ListReleasesAdapter(Context context, List<Version> releases) {
            mReleases = releases;
            mContext = context;
        }
        @Override
        public ListReleasesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_list_item, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final ListReleasesAdapter.ViewHolder holder, int position) {
            final Version version = mReleases.get(position);
            final int pos = position;

            if(version.getThumb() != null && !version.getThumb().isEmpty())
                Picasso.with(mContext).load(version.getThumb()).placeholder(R.drawable.bg_side_nav_bar).error(R.drawable.bg_side_nav_bar).into(holder.image);
            holder.title.setText(version.getTitle());
            holder.text.setText(version.getFormat());
            holder.textSecondary.setText(version.getLabel());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final JsonAdapter<Release> releaseJsonAdapter = moshi.adapter(Release.class);
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Intent intent = new Intent(getActivity(), DetailActivity.class);
                                intent.putExtra("jsonObject", client.genericGet(version.getResource_url()));
                                intent.putExtra("type", "release");
                                startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
        }

        @Override
        public int getItemCount() {
            return mReleases.size();
        }
    }
}
