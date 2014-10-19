package edu.hastings.hastingscollege;

/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.hastings.hastingscollege.connection.Connection;
import edu.hastings.hastingscollege.googleanalytics.MyApplication;
import edu.hastings.hastingscollege.model.Data;
import edu.hastings.hastingscollege.navdrawerfragments.FragmentAbout;
import edu.hastings.hastingscollege.navdrawerfragments.FragmentAthletics;
import edu.hastings.hastingscollege.navdrawerfragments.FragmentBroncoboard;
import edu.hastings.hastingscollege.navdrawerfragments.FragmentCampusEvents;
import edu.hastings.hastingscollege.navdrawerfragments.FragmentDiningHall;
import edu.hastings.hastingscollege.navdrawerfragments.FragmentEmergencyContacts;
import edu.hastings.hastingscollege.navdrawerfragments.FragmentHome;
import edu.hastings.hastingscollege.navdrawerfragments.FragmentMap;
import edu.hastings.hastingscollege.navdrawerfragments.FragmentTwitter;

public class MainActivity extends FragmentActivity {
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    // nav drawer title
    private CharSequence mDrawerTitle;
    // used to store app title
    private CharSequence mTitle;

    // slide menu items
    private String[] navMenuTitles;

    private int mFragPosition = 0;

    private boolean mConnection;
    public static boolean mShouldNotify = true;
    public Tracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Checks whether the user set the preference to include summary text
        //SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        //mShouldNotify = sharedPrefs.getBoolean("notificationsPref", false);

        mConnection = new Connection().hasConnection(MainActivity.this);

        mTitle = mDrawerTitle = getTitle();

        // load slide menu items
        navMenuTitles = getResources().getStringArray(R.array.drawer_items);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item, navMenuTitles));

        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            selectItem(0);
        }
        new DownloadXmlTask().execute(getString(R.string.sodexo_menu_url));

        tracker = ((MyApplication) getApplication()).getTracker(MyApplication.TrackerName.APP_TRACKER);
        tracker.setScreenName("App Opened");
        tracker.send(new HitBuilders.AppViewBuilder().build());
    }

    @Override
    public void finish() {
        Data.mondayMenu = null;
        Data.tuesdayMenu = null;
        Data.wednesdayMenu = null;
        Data.thursdayMenu = null;
        Data.fridayMenu = null;
        Data.saturdayMenu = null;
        Data.sundayMenu = null;
        Data.dates = null;
        super.finish();
        //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    //Fires after the OnStop() state
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            trimCache(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void trimCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {
            Log.v("Trim Cache exception", e.toString());
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                boolean success = deleteDir(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }

    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // display view for selected nav drawer item
            selectItem(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        boolean fragmentRefreshable = ((mFragPosition != 7) && (mFragPosition != 8));
        //menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        menu.findItem(R.id.itemRefresh).setVisible(!drawerOpen && fragmentRefreshable);
        menu.findItem(R.id.settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.itemRefresh:
                if (mFragPosition == 2)
                    new DownloadXmlTask().execute(getString(R.string.sodexo_menu_url));
                selectItem(mFragPosition);
                return true;
            case R.id.settings:
                Intent settingsActivity = new Intent(getBaseContext(), SettingsActivity.class);
                startActivity(settingsActivity);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void selectItem(int position) {
        // update the main content frame by replacing fragments
        Fragment fragment = null;
        mFragPosition = position;
        // Fragments requiriing connection
        switch (position) {
            case 0:
                if (mConnection) {
                    fragment = new FragmentHome();

                } else {
                    buildDialog(this).show();
                }
                break;
            case 1:
                fragment = new FragmentMap();
                break;
            case 2:
                if (mConnection) {
                    fragment = new FragmentDiningHall();
                } else {
                    buildDialog(this).show();
                }
                break;
            case 3:
                if (mConnection) {
                    fragment = new FragmentBroncoboard();
                } else {
                    buildDialog(this).show();
                }
                break;
            case 4:
                if (mConnection) {
                    fragment = new FragmentTwitter();
                } else {
                    buildDialog(this).show();
                }
                break;
            case 5:
                if (mConnection) {
                    fragment = new FragmentCampusEvents();
                } else {
                    buildDialog(this).show();
                }
                break;
            case 6:
                if (mConnection) {
                    fragment = new FragmentAthletics();
                } else {
                    buildDialog(this).show();
                }
                break;
            case 7:
                fragment = new FragmentEmergencyContacts();
                break;
            case 8:
                fragment = new FragmentAbout();
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment).commit();

            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(navMenuTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }

    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public AlertDialog.Builder buildDialog(Context c) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("No Internet connection.");
        builder.setMessage("Check your Internet Connection");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //finish();
            }
        });
        return builder;
    }

    // Given a string representation of a URL, sets up a connection and gets
    // an input stream.
    private InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        return conn.getInputStream();
    }


    //Displays an error if the app is unable to load content.
    private void showSodexoError() {
        // The specified network connection is not available. Displays error message.
        Toast.makeText(this,
                "Unable to load feed, Check your internet connection",
                Toast.LENGTH_LONG).show();
    }

    // Implementation of AsyncTask used to download XML feed
    private class DownloadXmlTask extends AsyncTask<String, Void, List<HashMap<String, String>>> {

        @Override
        protected List<HashMap<String, String>> doInBackground(String... urls) {
            InputStream stream = null;
            SodexoXmlParser sodexoXmlParser = new SodexoXmlParser(MainActivity.this);
            List<HashMap<String, String>> menuItems = new ArrayList<>();
            try {
                try {
                    stream = downloadUrl(urls[0]);
                    menuItems = sodexoXmlParser.parse(stream);
                } finally {
                    if (stream != null) {
                        stream.close();
                    }
                }
            } catch (IOException e) {
                Log.d("IOException", e.toString());
            } catch (XmlPullParserException e) {
                Log.d("XmlPullParserException", e.toString());
            }

            return menuItems;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> menuItems) {
            super.onPostExecute(menuItems);
            if (menuItems != null) {
                retrieveItems(menuItems);
            } else
                showSodexoError();
        }

        protected void retrieveItems(List<HashMap<String, String>> menuItems) {
            final String KEY_ITEM_DATE = "menudate";
            final String KEY_DAY = "dayname";

            List<HashMap<String, String>> sundayMenu = new ArrayList<>();
            List<HashMap<String, String>> mondayMenu = new ArrayList<>();
            List<HashMap<String, String>> tuesdayMenu = new ArrayList<>();
            List<HashMap<String, String>> wednesdayMenu = new ArrayList<>();
            List<HashMap<String, String>> thursdayMenu = new ArrayList<>();
            List<HashMap<String, String>> fridayMenu = new ArrayList<>();
            List<HashMap<String, String>> saturdayMenu = new ArrayList<>();
            String[] dates = new String[7];

            for (HashMap<String, String> menuItem : menuItems) {
                if (menuItem.get(KEY_DAY).equals("Monday")) {
                    mondayMenu.add(menuItem);
                    dates[0] = menuItem.get(KEY_ITEM_DATE);
                } else if (menuItem.get(KEY_DAY).equals("Tuesday")) {
                    tuesdayMenu.add(menuItem);
                    dates[1] = menuItem.get(KEY_ITEM_DATE);
                } else if (menuItem.get(KEY_DAY).equals("Wednesday")) {
                    wednesdayMenu.add(menuItem);
                    dates[2] = menuItem.get(KEY_ITEM_DATE);
                } else if (menuItem.get(KEY_DAY).equals("Thursday")) {
                    thursdayMenu.add(menuItem);
                    dates[3] = menuItem.get(KEY_ITEM_DATE);
                } else if (menuItem.get(KEY_DAY).equals("Friday")) {
                    fridayMenu.add(menuItem);
                    dates[4] = menuItem.get(KEY_ITEM_DATE);
                } else if (menuItem.get(KEY_DAY).equals("Saturday")) {
                    saturdayMenu.add(menuItem);
                    dates[5] = menuItem.get(KEY_ITEM_DATE);
                } else if (menuItem.get(KEY_DAY).equals("Sunday")) {
                    sundayMenu.add(menuItem);
                    dates[6] = menuItem.get(KEY_ITEM_DATE);
                }
            }

            Data.mondayMenu = mondayMenu;
            Data.tuesdayMenu = tuesdayMenu;
            Data.wednesdayMenu = wednesdayMenu;
            Data.thursdayMenu = thursdayMenu;
            Data.fridayMenu = fridayMenu;
            Data.saturdayMenu = saturdayMenu;
            Data.sundayMenu = sundayMenu;
            Data.dates = dates;
        }
    }
}


