package edu.hastings.hastingscollege;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SodexoXmlParser {

    private static final String ns = null;

    private Context mContext;

    public SodexoXmlParser(Context context) {
        this.mContext = context;
    }

    public List<HashMap<String, String>> parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }

    private List<HashMap<String, String>> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<HashMap<String, String>> menuItems = new ArrayList<>();
        int event;
        try {
            event = parser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                String name = parser.getName();
                switch (event) {
                    case XmlPullParser.START_TAG:
                        break;
                    case XmlPullParser.END_TAG:
                        if (name.equals("weeklymenu")) {
                            menuItems.add(readMenuItem(parser));
                        }
                        break;
                }
                event = parser.next();
            }
        } catch (Exception e) {
            Log.d("Exception", e.toString());
        }

        return menuItems;
    }

    private HashMap<String, String> readMenuItem(XmlPullParser parser) throws XmlPullParserException, IOException {
        HashMap<String, String> menuItem = new HashMap<>();
        String[] menuItemKeys = mContext.getResources().getStringArray(R.array.sodexo_menu_item_keys);
        int attributeCount = parser.getAttributeCount();
        if (attributeCount != -1) {
            for (int i = 0; i < attributeCount; i++) {
                String attributeName = parser.getAttributeName(i);
                for (String menuItemKey : menuItemKeys)
                    if(attributeName.equals(menuItemKey))
                        menuItem.put(menuItemKey, parser.getAttributeValue(i));
            }
        }
        return menuItem;
    }
}
