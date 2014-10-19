package edu.hastings.hastingscollege.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import edu.hastings.hastingscollege.R;

public class CustomEContactsAdapter extends BaseAdapter {

    private static final String TAG = CustomEContactsAdapter.class.getSimpleName();
    ArrayList<ContactsDataModel> listArray;

    public CustomEContactsAdapter(ArrayList<ContactsDataModel> contactsDataModelArrayList) {
        this.listArray = contactsDataModelArrayList;
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
    public View getView(int position, View convertView, final ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.list_item_econtacts, parent, false);
        }

        final ContactsDataModel dataModel = listArray.get(position);

        TextView contactName = (TextView) convertView.findViewById(R.id.contact_name);
        contactName.setText(dataModel.getContactName());

        TextView contactNum = (TextView) convertView.findViewById(R.id.phone_num);
        contactNum.setText(dataModel.getContactPhoneNumber());
        return convertView;
    }
}
