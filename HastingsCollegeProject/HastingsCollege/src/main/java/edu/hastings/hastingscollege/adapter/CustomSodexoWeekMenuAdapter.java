package edu.hastings.hastingscollege.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import edu.hastings.hastingscollege.R;
import edu.hastings.hastingscollege.tabfragments.SodexoMenu;

/**
 * Created by Alex on 7/20/2014.
 */
public class CustomSodexoWeekMenuAdapter extends BaseAdapter{

    private static final String TAG = CustomEContactsAdapter.class.getSimpleName();
    List<HashMap<String, String>> daysList;

    final String KEY_ITEM_DATE = "menudate";
    final String KEY_DAY = "dayname";

    public CustomSodexoWeekMenuAdapter(List<HashMap<String, String>> days) {
        this.daysList = days;
    }

    @Override
    public int getCount() {
        return daysList.size();
    }

    @Override
    public Object getItem(int position) {
        return daysList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.list_item_sodexo_week, parent, false);
        }

        final HashMap<String, String> day = daysList.get(position);

        TextView dayOfWeek = (TextView) convertView.findViewById(R.id.day_of_week);
        dayOfWeek.setText(day.get(KEY_DAY));

        TextView date = (TextView) convertView.findViewById(R.id.date);
        date.setText(day.get(KEY_ITEM_DATE));

        Button btnMenu = (Button) convertView.findViewById(R.id.menu_button);
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dayName = day.get(KEY_DAY);
                Intent i = new Intent(parent.getContext(), SodexoMenu.class);
                i.putExtra(KEY_DAY, dayName);
                parent.getContext().startActivity(i);
            }
        });
        return convertView;
    }
}
