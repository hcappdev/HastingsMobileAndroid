package edu.hastings.hastingscollege.navdrawerfragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import edu.hastings.hastingscollege.R;
import edu.hastings.hastingscollege.googleanalytics.MyApplication;

public class FragmentMap extends Fragment {

    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;
    private ProgressDialog pDialog;
    public Tracker tracker;

    public static final String TAG = "FragmentMap";
    private static final String TAG_LOCATIONS = "Locations";
    private static final String TAG_LAT = "latitude";
    private static final String TAG_LONG = "longitude";
    private static final String TAG_TITLE = "title";
    private static final String TAG_SNIPPET = "snippet";

    public FragmentMap() {
        // Empty constructor required for fragment subclasses
    }

    public static Fragment newInstance(Context context) {
        return new FragmentMap();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Zoom Point
        final LatLng HASTINGS_CENTER = new LatLng(40.593413, -98.36964);
        mMap = mMapFragment.getMap();

        if (mMap != null) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            new loadAndAddPins().execute();
            // Move the camera instantly to Hastings with a zoom of 15
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(HASTINGS_CENTER, 15));
            // Zoom in, animating the camera.
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        }
    }

    public void onDestroyView() {
        super.onDestroyView();
        try {
            //Fragment fragment = (getChildFragmentManager().findFragmentById(R.id.map));
            getChildFragmentManager().beginTransaction().remove(mMapFragment).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mMapFragment == null) {
            mMapFragment = SupportMapFragment.newInstance();
            getChildFragmentManager().beginTransaction().replace(R.id.map, mMapFragment).commit();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.map, null);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            MapsInitializer.initialize(this.getActivity());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        this.tracker = ((MyApplication) getActivity().getApplication()).getTracker(MyApplication.TrackerName.APP_TRACKER);
        this.tracker.setScreenName("Campus Map");
        this.tracker.send(new HitBuilders.AppViewBuilder().build());
    }

    private class loadAndAddPins extends AsyncTask<String, Void, ArrayList<HashMap<String, String>>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Loading Map...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected ArrayList<HashMap<String, String>> doInBackground(String... urls) {
            String jsonStr = loadJsonFromAssets();
            ArrayList<HashMap<String, String>> mapLocations = new ArrayList<>();
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    // Getting JSON Array node
                    JSONArray locations = jsonObj.getJSONArray(TAG_LOCATIONS);

                    for (int i = 0; i < locations.length(); i++) {
                        JSONObject location = locations.getJSONObject(i);
                        HashMap<String, String> locInfo = new HashMap<>();

                        String latitude = location.getString(TAG_LAT);
                        String longitude = location.getString(TAG_LONG);
                        String title = location.getString(TAG_TITLE);
                        String snippet = location.getString(TAG_SNIPPET);
                        locInfo.put(TAG_LAT, latitude);
                        locInfo.put(TAG_LONG, longitude);
                        locInfo.put(TAG_TITLE, title);
                        locInfo.put(TAG_SNIPPET, snippet);
                        mapLocations.add(locInfo);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
                return null;
            }

            return mapLocations;
        }

        @Override
        protected void onPostExecute(ArrayList<HashMap<String, String>> mapLocations) {
            super.onPostExecute(mapLocations);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            for (HashMap<String, String> location : mapLocations) {
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(Double.parseDouble(location.get(TAG_LAT)), Double.parseDouble(location.get(TAG_LONG))))
                        .title(location.get(TAG_TITLE))
                        .snippet(location.get(TAG_SNIPPET)));
            }
        }

        protected String loadJsonFromAssets(){
            String json;
            try {
                InputStream is = getActivity().getAssets().open("map-data.json");
                int size = is.available();
                byte[] buffer = new byte[size];

                is.read(buffer);

                is.close();

                json = new String(buffer, "UTF-8");


            } catch (IOException ex) {
                ex.printStackTrace();
                return null;
            }
            return json;
        }
    }
}