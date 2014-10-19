package edu.hastings.hastingscollege.tabfragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.hastings.hastingscollege.R;
import edu.hastings.hastingscollege.model.Data;

public class DinnerFragment extends Fragment {

    final String KEY_DAY = "dayname";
    final String KEY_MEAL = "meal";
    final String KEY_ITEM_NAME = "item_name";
    final String KEY_ITEM_DESC = "item_desc";

    private String day;
    private String[] daysOfWeek;

    public DinnerFragment() {}

    public static DinnerFragment newInstance(String dayOfWeek) {
        DinnerFragment dinnerFragment = new DinnerFragment();

        Bundle arguments = new Bundle();
        arguments.putString("dayname", dayOfWeek);
        dinnerFragment.setArguments(arguments);
        return dinnerFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null)
            day = arguments.getString(KEY_DAY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_dinner, container, false);
        ListView mListView = (ListView) rootView.findViewById(R.id.dinnerList);
        TextView txtHeaderText = (TextView) rootView.findViewById(R.id.list_item_menu_header_textview);
        daysOfWeek = getResources().getStringArray(R.array.days_of_week);

        final List<HashMap<String, String>> dinnerMenuItems = getMenuItemsFromDay(day);
        String[] from = { KEY_ITEM_NAME, KEY_ITEM_DESC };
        int[] to = {R.id.item_name, R.id.item_desc};
        SimpleAdapter adapter = new SimpleAdapter(getActivity().getBaseContext(),
                dinnerMenuItems,
                R.layout.list_item_sodexo,
                from,
                to);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent in = new Intent(getActivity(), SingleMenuItemNutritionFactsActivity.class);
                String[] menuItemKeys = getResources().getStringArray(R.array.sodexo_menu_item_keys);
                for (int i = 5; i < menuItemKeys.length; i++){
                    String itemAttribute = dinnerMenuItems.get(position).get(menuItemKeys[i]);
                    in.putExtra(menuItemKeys[i], itemAttribute);
                }
                String itemName = dinnerMenuItems.get(position).get(KEY_ITEM_NAME);
                in.putExtra(KEY_ITEM_NAME, itemName);

                startActivity(in);
            }
        });

        String headerText;
        if (day.equals(daysOfWeek[5]))
            headerText = "Open from " + getResources().getString(R.string.sodexo_dinner_times_sat);
        else if (day.equals(daysOfWeek[6]))
            headerText = "Open from " + getResources().getString(R.string.sodexo_dinner_times_sun);
        else
            headerText = "Open from " + getResources().getString(R.string.sodexo_dinner_times_weekday);

        txtHeaderText.setText(headerText);
        return rootView;
    }

    private List<HashMap<String, String>> getDinnerItems(List<HashMap<String, String>> menuItems) {
        List<HashMap<String, String>> lunchItems = new ArrayList<>();

        for (HashMap<String, String> menuItem : menuItems) {
            if (menuItem.get(KEY_MEAL).equals("Dinner")) {
                lunchItems.add(menuItem);
            }
        }
        return lunchItems;
    }

    private List<HashMap<String, String>> getMenuItemsFromDay(String day) {
        if (day.equals(daysOfWeek[0]))
            return getDinnerItems(Data.mondayMenu);
        else if (day.equals(daysOfWeek[1]))
            return getDinnerItems(Data.tuesdayMenu);
        else if (day.equals(daysOfWeek[2]))
            return getDinnerItems(Data.wednesdayMenu);
        else if (day.equals(daysOfWeek[3]))
            return getDinnerItems(Data.thursdayMenu);
        else if (day.equals(daysOfWeek[4]))
            return getDinnerItems(Data.fridayMenu);
        else if (day.equals(daysOfWeek[5]))
            return getDinnerItems(Data.saturdayMenu);
        else if (day.equals(daysOfWeek[6]))
            return getDinnerItems(Data.sundayMenu);
        return new ArrayList<>();
    }
}
