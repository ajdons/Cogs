package com.adamdonegan.cogs.app;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.adamdonegan.cogs.R;
import com.adamdonegan.cogs.models.BasicInformation;
import com.adamdonegan.cogs.models.Collection;
import com.adamdonegan.cogs.models.CollectionRelease;
import com.adamdonegan.cogs.models.Profile;
import com.adamdonegan.cogs.util.DiscogsClient;
import com.adamdonegan.cogs.util.PreferencesManager;
import com.bartoszlipinski.recyclerviewheader.RecyclerViewHeader;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.picasso.Picasso;

public class CollectionFragment extends Fragment{
    private static final String CONSUMER_KEY = "tZplWaLrLakbPmeKDnNR";
    private static final String CONSUMER_SECRET = "WlvAHSrMKkEokrhICslQndFmlwjafEwW";
    private static final String USER_AGENT = "Cogs/0.1 +https://github.com/ajdons/Cogs";

    private static Collection mCollection;
    private static String mUsername;
    private static Moshi moshi;
    private static PreferencesManager prefsManager;
    private static DiscogsClient client;

    public static CollectionFragment newInstance(Collection collection, Profile profile) {
        CollectionFragment fragment = new CollectionFragment();
        mCollection = collection;
        moshi = new Moshi.Builder().build();
        prefsManager = PreferencesManager.getInstance();
        mUsername = prefsManager.getValue("username");
        client = new DiscogsClient(CONSUMER_KEY, CONSUMER_SECRET, USER_AGENT, prefsManager.getValue("oauth_token"), prefsManager.getValue("oauth_token_secret"));

        return fragment;
    }

    public CollectionFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recycler_with_header, container, false);

        RecyclerViewHeader header = (RecyclerViewHeader) rootView.findViewById(R.id.header);
        TextView heading1 = (TextView) rootView.findViewById(R.id.heading1);
        TextView heading2 = (TextView) rootView.findViewById(R.id.heading2);
        heading1.setText(R.string.collection);
        heading2.setText(mCollection.getReleases().size() + getString(R.string.num_items));
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        CollectionAdapter adapter = new CollectionAdapter(getActivity().getApplicationContext(), mCollection);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        header.attachTo(recyclerView, true);

        return rootView;
    }

    public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.ViewHolder>
    {
        private Collection mCollection;
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

        public CollectionAdapter (Context context, Collection c) {
            mCollection = c;
            mContext = context;
        }
        @Override
        public CollectionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_list_item, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final CollectionAdapter.ViewHolder holder, int position) {
            final CollectionRelease collectionRelease = mCollection.getReleases().get(position);
            final int pos = position;
            BasicInformation basicInfo = collectionRelease.getBasic_information();
            holder.title.setText(basicInfo.getTitle());
            Picasso.with(mContext).load(basicInfo.getThumb()).placeholder(R.drawable.bg_side_nav_bar).error(R.drawable.bg_side_nav_bar).into(holder.image);
            holder.text.setText(TextUtils.join(", ", basicInfo.getFormats().get(0).getDescriptions()));
            holder.textSecondary.setText(basicInfo.getLabels().get(0).getName());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final JsonAdapter<CollectionRelease> cReleaseAdapter = moshi.adapter(CollectionRelease.class);
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Intent intent = new Intent(getActivity(), DetailActivity.class);
                                intent.putExtra("jsonObject", cReleaseAdapter.toJson(collectionRelease));
                                intent.putExtra("type", "collectionrelease");
                                intent.putExtra("helperRelease", client.genericGet(collectionRelease.getBasic_information().getResource_url()));
                                startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });

            holder.button1.setVisibility(View.VISIBLE);
            holder.button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            client.deleteInstanceFromFolder(mUsername, "0", collectionRelease.getId(), collectionRelease.getInstance_id());
                        }
                    });
                    mCollection.getReleases().remove(pos);
                    notifyItemRemoved(pos);
                    notifyItemRangeChanged(0, pos);
                    Snackbar.make(holder.itemView, "Item deleted", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mCollection.getReleases().size();
        }
    }
}
