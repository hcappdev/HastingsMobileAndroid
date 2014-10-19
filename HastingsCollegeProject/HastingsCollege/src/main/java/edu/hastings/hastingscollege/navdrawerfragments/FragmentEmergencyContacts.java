package edu.hastings.hastingscollege.navdrawerfragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;

import edu.hastings.hastingscollege.R;
import edu.hastings.hastingscollege.adapter.ContactsDataModel;
import edu.hastings.hastingscollege.adapter.CustomEContactsAdapter;
import edu.hastings.hastingscollege.googleanalytics.MyApplication;

public class FragmentEmergencyContacts extends Fragment {

    public static final String TAG = "FragmentEmergencyContacts";
    public Tracker tracker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.tracker = ((MyApplication) getActivity().getApplication()).getTracker(MyApplication.TrackerName.APP_TRACKER);
        this.tracker.setScreenName("Campus Contacts");
        this.tracker.send(new HitBuilders.AppViewBuilder().build());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.emergency_contacts, container, false);
        ListView contactsList = (ListView) view.findViewById(R.id.contacts_list);

        ArrayList<ContactsDataModel> contactsDataModelArrayList = new ArrayList<ContactsDataModel>();
        String[] contactNames = getResources().getStringArray(R.array.emergency_contact_names);
        final String[] contactPhoneNums = getResources().getStringArray(R.array.emergency_contact_numbers);
        for (int i = 0; i < contactNames.length; i++) {
            contactsDataModelArrayList.add(new ContactsDataModel(contactNames[i], contactPhoneNums[i]));
        }
        CustomEContactsAdapter customEContactsAdapter = new CustomEContactsAdapter(contactsDataModelArrayList);
        contactsList.setAdapter(customEContactsAdapter);
        contactsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + contactPhoneNums[position]));
                    parent.getContext().startActivity(callIntent);
                } catch (Exception e) {
                    Log.v("Error with Intent to Call", e.toString());
                }
            }
        });
        return view;
    }
}
