package edu.hastings.hastingscollege.navdrawerfragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.hastings.hastingscollege.R;
import edu.hastings.hastingscollege.adapter.CustomSodexoWeekMenuAdapter;
import edu.hastings.hastingscollege.googleanalytics.MyApplication;
import edu.hastings.hastingscollege.model.Data;

public class FragmentDiningHall extends Fragment {


    public static final String TAG = "FragmentDiningHall";
    public Tracker tracker;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        this.tracker = ((MyApplication) getActivity().getApplication()).getTracker(MyApplication.TrackerName.APP_TRACKER);
        this.tracker.setScreenName("Dining Hall");
        this.tracker.send(new HitBuilders.AppViewBuilder().build());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sodexo, container, false);
        ListView mDaysOfWeekList = (ListView) view.findViewById(R.id.days_of_week_list);

        if (Data.mondayMenu.size() > 0) {
            String[] mDaysOfWeek = getResources().getStringArray(R.array.days_of_week);

            final String KEY_ITEM_DATE = "menudate";
            final String KEY_DAY = "dayname";
            final String[] dates = formatDates(Data.dates);
            final List<HashMap<String, String>> daysList = new ArrayList<>();
            for (int i = 0; i < mDaysOfWeek.length; i++) {
                HashMap<String, String> dayOfWeek = new HashMap<>();
                dayOfWeek.put(KEY_DAY, mDaysOfWeek[i]);
                dayOfWeek.put(KEY_ITEM_DATE, dates[i]);
                daysList.add(dayOfWeek);
            }

            mDaysOfWeekList.addHeaderView(new View(getActivity()));
            mDaysOfWeekList.addFooterView(new View(getActivity()));
            CustomSodexoWeekMenuAdapter adapter = new CustomSodexoWeekMenuAdapter(daysList);
            mDaysOfWeekList.setAdapter(adapter);

            TextView txtHeaderText = (TextView) view.findViewById(R.id.list_item_menu_header_textview);
            String headerDate = dates[0];
            String headerText = "Menu for the week of: " + headerDate;
            txtHeaderText.setText(headerText);
        }
        else {
            mDaysOfWeekList.setVisibility(View.GONE);
            view.findViewById(R.id.header_layout).setVisibility(View.GONE);
            view.findViewById(R.id.error_text).setVisibility(View.VISIBLE);
        }

        return view;
    }

    private String[] formatDates(String[] dates) {
        SimpleDateFormat fromDate = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat toDate = new SimpleDateFormat("MMMM dd, yyyy");
        String[] formattedDates = new String[7];

        for (int i=0; i< dates.length; i++) {
            try {
                String reformattedDate = toDate.format(fromDate.parse(dates[i]));
                formattedDates[i] = reformattedDate;
            } catch (ParseException e) {
                Log.v("Sodexo Fragment", e.toString());
            }
        }
        return formattedDates;
    }
}
