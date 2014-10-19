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

public class BreakfastFragment extends Fragment {

    final String KEY_DAY = "dayname";
    final String KEY_MEAL = "meal";
    final String KEY_ITEM_NAME = "item_name";
    final String KEY_ITEM_DESC = "item_desc";

    private String day;
    private String[] daysOfWeek;

    public BreakfastFragment() {}

    public static BreakfastFragment newInstance(String dayOfWeek) {
        BreakfastFragment breakfastFragment = new BreakfastFragment();

        Bundle arguments = new Bundle();
        arguments.putString("dayname", dayOfWeek);
        breakfastFragment.setArguments(arguments);
        return breakfastFragment;
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

        View rootView = inflater.inflate(R.layout.fragment_breakfast, container, false);
        ListView mListView = (ListView) rootView.findViewById(R.id.breakfastList);
        TextView txtHeaderText = (TextView) rootView.findViewById(R.id.list_item_menu_header_textview);
        daysOfWeek = getResources().getStringArray(R.array.days_of_week);

        final List<HashMap<String, String>> menuItems = getMenuItemsFromDay(day);
        String[] from = { KEY_ITEM_NAME, KEY_ITEM_DESC };
        int[] to = {R.id.item_name, R.id.item_desc};
        SimpleAdapter adapter = new SimpleAdapter(getActivity().getBaseContext(),
                menuItems,
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
                    String itemAttribute = menuItems.get(position).get(menuItemKeys[i]);
                    in.putExtra(menuItemKeys[i], itemAttribute);
                }
                String itemName = menuItems.get(position).get(KEY_ITEM_NAME);
                in.putExtra(KEY_ITEM_NAME, itemName);

                startActivity(in);
            }
        });

        String headerText;
        if (day.equals(daysOfWeek[5]) || day.equals(daysOfWeek[6]))
            headerText = "Open from " + getResources().getString(R.string.sodexo_breakfast_times_weekend);
        else
            headerText = "Open from " + getResources().getString(R.string.sodexo_breakfast_times_weekday);

        txtHeaderText.setText(headerText);
        return rootView;
    }

    private List<HashMap<String, String>> getBreakfastItems(List<HashMap<String, String>> menuItems, boolean isWeekday) {
        List<HashMap<String, String>> breakfastItems = new ArrayList<>();

        for (HashMap<String, String> menuItem : menuItems) {
            if (isWeekday && menuItem.get(KEY_MEAL).equals("Breakfast")) {
                breakfastItems.add(menuItem);
            }
            else if (!isWeekday && menuItem.get(KEY_MEAL).equals("Lunch"))
                breakfastItems.add(menuItem);
        }
        return breakfastItems;
    }

    private List<HashMap<String, String>> getMenuItemsFromDay(String day) {
        if (day.equals(daysOfWeek[0]))
            return getBreakfastItems(Data.mondayMenu, true);
        else if (day.equals(daysOfWeek[1]))
            return getBreakfastItems(Data.tuesdayMenu, true);
        else if (day.equals(daysOfWeek[2]))
            return getBreakfastItems(Data.wednesdayMenu, true);
        else if (day.equals(daysOfWeek[3]))
            return getBreakfastItems(Data.thursdayMenu, true);
        else if (day.equals(daysOfWeek[4]))
            return getBreakfastItems(Data.fridayMenu, true);
        else if (day.equals(daysOfWeek[5]))
            return getBreakfastItems(Data.saturdayMenu, false);
        else if (day.equals(daysOfWeek[6]))
            return getBreakfastItems(Data.sundayMenu, false);
        return new ArrayList<>();
    }
}
