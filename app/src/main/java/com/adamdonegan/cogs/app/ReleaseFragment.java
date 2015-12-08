package com.adamdonegan.cogs.app;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.adamdonegan.cogs.R;
import com.adamdonegan.cogs.models.Release;
import com.adamdonegan.cogs.util.CircleTransformation;
import com.adamdonegan.cogs.util.DiscogsClient;
import com.adamdonegan.cogs.util.PreferencesManager;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

/**
 * Created by AdamDonegan on 15-11-09.
 */
public class ReleaseFragment  extends Fragment {
    private ImageView releaseImage;
    private TextView textName;
    private TextView textArtist;
    private static Release mRelease;


    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ReleaseFragment newInstance(Release release) {
        ReleaseFragment fragment = new ReleaseFragment();
        mRelease = release;
        return fragment;
    }

    public ReleaseFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_release, container, false);
        //((AppCompatActivity)getActivity()).setSupportActionBar((Toolbar) rootView.findViewById(R.id.toolbar));

        //((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#80000000")));
        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) rootView.findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(mRelease.getTitle());
        releaseImage = (ImageView) rootView.findViewById(R.id.releaseImage);
        Picasso.with(getActivity().getApplicationContext()).load(mRelease.getImages().get(0).getResource_url()).into(releaseImage);

        return rootView;
    }

}
