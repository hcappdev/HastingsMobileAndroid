package edu.hastings.hastingscollege.adapter;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import edu.hastings.hastingscollege.R;
import edu.hastings.hastingscollege.model.EventModel;

public class CustomEventsAdapter extends BaseAdapter {
    private static final String TAG = CustomEventsAdapter.class.getSimpleName();

    ArrayList<EventModel> listArray;

    private static final String TAG_TITLE = "event_title";
    private static final String TAG_LOCATION = "event_location";
    private static final String TAG_TIME = "event_time";

    public CustomEventsAdapter(ArrayList<EventModel> eventsArrayList) {
        this.listArray = eventsArrayList;
    }

    @Override
    public int getCount() {
        return listArray.size();
    }

    @Override
    public Object getItem(int position) {
        return listArray.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent){
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.list_item_event, parent, false);
        }

        final EventModel eventModel = listArray.get(position);

        TextView eventName = (TextView) convertView.findViewById(R.id.event_name);
        eventName.setText(eventModel.eventName);

        TextView eventWeek = (TextView) convertView.findViewById(R.id.event_week);
        eventWeek.setText(eventModel.eventWeek);

        final Button eventInfoBtn = (Button) convertView.findViewById(R.id.more_info_btn);
        eventInfoBtn.setOnClickListener(new View.OnClickListener() {
            final LayoutInflater layoutInflater = (LayoutInflater)parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            @Override
            public void onClick(View v) {
                View eventDialogView = layoutInflater.inflate(R.layout.event_dialog, null);
                LinearLayout eventDialogLayout = (LinearLayout) eventDialogView.findViewById(R.id.event_info_dialog);
                for(int i = 0; i < eventModel.eventsOfDay.size(); i++) {
                    LinearLayout eventDaySection = (LinearLayout) layoutInflater.inflate(R.layout.event_information_template, eventDialogLayout, false);
                    TextView eventDayHeaderText = (TextView) eventDaySection.findViewById(R.id.day_label);
                    eventDayHeaderText.setText(eventModel.eventDays[i]);
                    ListView eventInfoList = (ListView) eventDaySection.findViewById(R.id.event_info_list);
                    eventInfoList.addHeaderView(new View(parent.getContext()));
                    eventInfoList.addFooterView(new View(parent.getContext()));
                    String[] from = {TAG_TITLE, TAG_LOCATION, TAG_TIME};
                    int[] to = {R.id.event_title, R.id.event_location, R.id.event_time};
                    SimpleAdapter adapter = new SimpleAdapter(parent.getContext(), eventModel.eventsOfDay.get(i), R.layout.list_item_event_info, from, to);
                    eventInfoList.setAdapter(adapter);
                    HelperExpandListView.getListViewSize(eventInfoList);
                    eventInfoList.setClickable(false);
                    eventDialogLayout.addView(eventDaySection);
                }

                final Dialog dialog = new Dialog(parent.getContext());
                dialog.setContentView(eventDialogView);
                dialog.setTitle(eventModel.eventName);
                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
            }
        });
        return convertView;
    }
}
