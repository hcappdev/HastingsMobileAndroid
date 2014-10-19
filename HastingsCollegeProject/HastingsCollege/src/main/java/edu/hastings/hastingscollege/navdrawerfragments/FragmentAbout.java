package edu.hastings.hastingscollege.navdrawerfragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import edu.hastings.hastingscollege.R;
import edu.hastings.hastingscollege.googleanalytics.MyApplication;

public class FragmentAbout extends Fragment {

    public static final String TAG = "FragmentAbout";
    public Tracker tracker;

    public static Fragment newInstance(Context context) { return new FragmentAbout(); }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.tracker = ((MyApplication) getActivity().getApplication()).getTracker(MyApplication.TrackerName.APP_TRACKER);
        this.tracker.setScreenName("About");
        this.tracker.send(new HitBuilders.AppViewBuilder().build());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = (ViewGroup) inflater.inflate(R.layout.about, null);

        return rootView;
    }
}
