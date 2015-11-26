package com.adamdonegan.cogs.app;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.adamdonegan.cogs.R;
import com.adamdonegan.cogs.models.Release;
import com.adamdonegan.cogs.models.Result;
import com.adamdonegan.cogs.models.SearchResults;
import com.adamdonegan.cogs.util.DiscogsClient;
import com.adamdonegan.cogs.util.PreferencesManager;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.picasso.Picasso;
import timber.log.Timber;

/**
 * Created by AdamDonegan on 15-11-16.
 */
public class SearchResultFragment extends Fragment {
    private static final String CONSUMER_KEY = "tZplWaLrLakbPmeKDnNR";
    private static final String CONSUMER_SECRET = "WlvAHSrMKkEokrhICslQndFmlwjafEwW";
    private static final String USER_AGENT = "Cogs/0.1 +https://github.com/ajdons/Cogs";

    private static SearchResults mResults;
    private static Moshi moshi;
    private static PreferencesManager prefsManager;
    private static DiscogsClient client;

    public static SearchResultFragment newInstance(SearchResults results) {
        SearchResultFragment fragment = new SearchResultFragment();
        mResults = results;
        moshi = new Moshi.Builder().build();
        prefsManager = PreferencesManager.getInstance();
        client = new DiscogsClient(CONSUMER_KEY, CONSUMER_SECRET, USER_AGENT, prefsManager.getValue("oauth_token"), prefsManager.getValue("oauth_token_secret"));

        return fragment;
    }

    public SearchResultFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(
                R.layout.recycler_view, container, false);
        ResultAdapter adapter = new ResultAdapter(getActivity().getApplicationContext(), mResults);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return recyclerView;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_card, parent, false));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Result selectedResult = mResults.getResults().get(getAdapterPosition());
                    String type = selectedResult.getType();

                    if(type.equals("release")){

                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JsonAdapter<Release> releaseAdapter = moshi.adapter(Release.class);
                                    Release release = releaseAdapter.fromJson(client.genericGet(selectedResult.getResource_url()));
                                    getActivity().getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.container, ReleaseFragment.newInstance(release))
                                            .commit();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                    else if(type.equals("master")){

                    }
                    else if(type.equals("artist")){

                    }
                }
            });
        }
    }

    /**
     * Adapter to display recycler view.
     */
    public class ResultAdapter extends RecyclerView.Adapter<ViewHolder> {
        // Set numbers of Card in RecyclerView.
        private SearchResults searchResults;
        private Context mContext;

        public ResultAdapter(Context context, SearchResults results) {
            this.searchResults = results;
            this.mContext = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Result result = searchResults.getResults().get(position);
            ImageView image = (ImageView) holder.itemView.findViewById(R.id.card_image);
            TextView title = (TextView) holder.itemView.findViewById(R.id.card_title);
            title.setSelected(true);
            TextView text = (TextView) holder.itemView.findViewById(R.id.card_text);

            if(result.getThumb() != null && !result.getThumb().isEmpty())
                Picasso.with(mContext).load(result.getThumb()).into(image);
            title.setText(result.getTitle());
            text.setText(result.getType());
        }

        @Override
        public int getItemCount() {
            return searchResults.getResults().size();
        }
    }
}
