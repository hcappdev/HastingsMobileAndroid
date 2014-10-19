package edu.hastings.hastingscollege.adapter;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

public class HelperExpandListView {
    public static void getListViewSize(ListView myListView) {
        ListAdapter adapter = myListView.getAdapter();
        if (adapter == null)
            return;

        int totalHeight = 0;
        for (int size = 0; size < adapter.getCount(); size++) {
            View listItem = adapter.getView(size, null, myListView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = myListView.getLayoutParams();
        params.height = totalHeight + (myListView.getDividerHeight() * (adapter.getCount() - 1));
        myListView.setLayoutParams(params);
        Log.i("height of listItem:", String.valueOf(totalHeight));
    }
}
