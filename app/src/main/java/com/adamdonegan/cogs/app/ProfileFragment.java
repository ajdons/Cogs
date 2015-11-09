package com.adamdonegan.cogs.app;

/**
 * Created by AdamDonegan on 15-11-06.
 */
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
 * Created by adamdonegan on 24/05/2014.
 */
public class ProfileFragment  extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static JSONObject profile;
    private ImageView userImage;
    private TextView textUsername;
    private TextView textActualName;
    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ProfileFragment newInstance(int sectionNumber, JSONObject profileAsJSON) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        profile = profileAsJSON;
        return fragment;
    }

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        userImage = (ImageView) rootView.findViewById(R.id.userImage);
        textUsername = (TextView) rootView.findViewById(R.id.textUsername);
        textActualName = (TextView) rootView.findViewById(R.id.textActualName);
        try {
            textUsername.setText(profile.getString("username"));
            textActualName.setText(profile.getString("name"));
            Picasso.with(getActivity().getApplicationContext()).setIndicatorsEnabled(true);
            Picasso.with(getActivity().getApplicationContext()).load(profile.getString("avatar_url")).transform(new CircleTransformation()).into(userImage);
        } catch(Exception e){
            e.printStackTrace();
        }
        return rootView;
    }

}
