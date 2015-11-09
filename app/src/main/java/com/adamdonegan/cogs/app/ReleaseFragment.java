package com.adamdonegan.cogs.app;

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
import com.adamdonegan.cogs.util.CircleTransformation;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

/**
 * Created by AdamDonegan on 15-11-09.
 */
public class ReleaseFragment  extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";
    private ImageView releaseImage;
    private TextView textName;
    private TextView textArtist;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ReleaseFragment newInstance(int sectionNumber) {
        ReleaseFragment fragment = new ReleaseFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public ReleaseFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_release, container, false);
        ((AppCompatActivity) getActivity()).setSupportActionBar((Toolbar) rootView.findViewById(R.id.toolbar));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Set Collapsing Toolbar layout to the screen
        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) rootView.findViewById(R.id.collapsing_toolbar);
        // Set title of Detail page
        collapsingToolbar.setTitle("Test Release");
        return rootView;
    }
}
