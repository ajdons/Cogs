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
import android.widget.ImageView;
import android.widget.TextView;

import com.adamdonegan.cogs.R;
import com.adamdonegan.cogs.models.Result;
import com.adamdonegan.cogs.models.SearchResults;
import com.adamdonegan.cogs.util.DiscogsClient;
import com.adamdonegan.cogs.util.PreferencesManager;
import com.bartoszlipinski.recyclerviewheader.RecyclerViewHeader;
import com.squareup.moshi.Moshi;
import com.squareup.picasso.Picasso;

public class SearchResultFragment extends Fragment {
    private static final String CONSUMER_KEY = "tZplWaLrLakbPmeKDnNR";
    private static final String CONSUMER_SECRET = "WlvAHSrMKkEokrhICslQndFmlwjafEwW";
    private static final String USER_AGENT = "Cogs/0.1 +https://github.com/ajdons/Cogs";
    private static final int DETAIL_ACTIVITY = 101;


    private static SearchResults mResults;
    private static String mHeading1;
    private static String mHeading2;
    private static Moshi moshi;
    private static PreferencesManager prefsManager;
    private static DiscogsClient client;

    public static SearchResultFragment newInstance(SearchResults results, String heading1, String heading2) {
        SearchResultFragment fragment = new SearchResultFragment();
        mResults = results;
        mHeading1 = heading1;
        mHeading2 = heading2;
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
        View rootView = inflater.inflate(R.layout.fragment_recycler_with_header, container, false);

        if(mResults.getResults().size() == 0)
            Snackbar.make(container, "No results found", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

        RecyclerViewHeader header = (RecyclerViewHeader) rootView.findViewById(R.id.header);
        TextView heading1 = (TextView) rootView.findViewById(R.id.heading1);
        TextView heading2 = (TextView) rootView.findViewById(R.id.heading2);
        heading1.setText(mHeading1);
        heading2.setText(mHeading2);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        ResultAdapter adapter = new ResultAdapter(getActivity().getApplicationContext(), mResults);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        header.attachTo(recyclerView, true);
        return rootView;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.card_list_item, parent, false));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Result selectedResult = mResults.getResults().get(getAdapterPosition());
                    final String type = selectedResult.getType();

                    if(type.equals("release")){

                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                                    intent.putExtra("jsonObject", client.genericGet(selectedResult.getResource_url()));
                                    intent.putExtra("type", type);
                                    getActivity().startActivityForResult(intent, DETAIL_ACTIVITY);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                    else if(type.equals("master")){
                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                                    intent.putExtra("jsonObject", client.genericGet(selectedResult.getResource_url()));
                                    intent.putExtra("type", type);
                                    getActivity().startActivityForResult(intent, DETAIL_ACTIVITY);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                    else if(type.equals("artist")){
                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                                    intent.putExtra("jsonObject", client.genericGet(selectedResult.getResource_url()));
                                    intent.putExtra("type", type);
                                    getActivity().startActivityForResult(intent, DETAIL_ACTIVITY);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                    else if(type.equals("label")){
                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                                    intent.putExtra("jsonObject", client.genericGet(selectedResult.getResource_url()));
                                    intent.putExtra("type", type);
                                    getActivity().startActivityForResult(intent, DETAIL_ACTIVITY);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    public class ResultAdapter extends RecyclerView.Adapter<ViewHolder> {

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
            TextView text = (TextView) holder.itemView.findViewById(R.id.card_text);
            TextView textSecondary = (TextView) holder.itemView.findViewById(R.id.card_text_secondary);

            if(result.getThumb() != null && !result.getThumb().isEmpty())
                Picasso.with(mContext).load(result.getThumb()).placeholder(R.drawable.bg_side_nav_bar).error(R.drawable.bg_side_nav_bar).into(image);


            if(result.getType().equals("master")) {
                String titleOnly = result.getTitle().split(" - ")[1];
                String artistOnly = result.getTitle().split(" - ")[0];
                title.setText(titleOnly);
                text.setText(artistOnly);
                textSecondary.setText(result.getType());
            }
            else if(result.getType().equals("release")) {
                String titleOnly = result.getTitle().split(" - ")[1];
                String artistOnly = result.getTitle().split(" - ")[0];
                title.setText(titleOnly);
                text.setText(TextUtils.join(", ", result.getFormat()));
                textSecondary.setText(result.getLabel().get(0) + " - " + result.getCountry());
            }
            else if(result.getType().equals("artist")) {
                title.setText(result.getTitle());
                text.setText(result.getType());
                textSecondary.setText("");
            }
            else if(result.getType().equals("label")) {
                title.setText(result.getTitle());
                text.setText(result.getType());
                textSecondary.setText("");
            }
        }

        @Override
        public int getItemCount() {
            return searchResults.getResults().size();
        }
    }
}
