package edu.hastings.hastingscollege.tabfragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import edu.hastings.hastingscollege.R;
import edu.hastings.hastingscollege.googleanalytics.MyApplication;

public class SingleMenuItemNutritionFactsActivity extends Activity {

    final String KEY_ITEM_NAME = "item_name";
    final String KEY_INGREDIENT = "ingredient";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ScrollView nutritionLayout = (ScrollView) inflater.inflate(R.layout.nutrition_facts_item, null);

        LinearLayout nutritionLinearLayout = (LinearLayout) nutritionLayout.findViewById(R.id.nutritionLayout);

        Intent in = getIntent();
        String[] menuItemKeys = getResources().getStringArray(R.array.sodexo_menu_item_keys);
        String[] menuItemLabels = getResources().getStringArray(R.array.sodexo_menu_item_labels);
        float scale = getResources().getDisplayMetrics().density;
        int dpAsPixels = (int) (6 * scale + 0.5f);

        String itemName = in.getStringExtra(KEY_ITEM_NAME);
        TextView txtHeader = (TextView) nutritionLinearLayout.findViewById(R.id.list_item_menu_header_textview);
        txtHeader.setText(itemName);

        for (int i = 5, j = 0; i < menuItemKeys.length; j++, i++){
            LinearLayout itemAttributeLayout = new LinearLayout(SingleMenuItemNutritionFactsActivity.this);
            itemAttributeLayout.setPadding(dpAsPixels, 0, 0, 0);
            itemAttributeLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            TextView itemAttributeLabel = new TextView(SingleMenuItemNutritionFactsActivity.this);
            itemAttributeLabel.setText(menuItemLabels[j] + " ");
            itemAttributeLabel.setTypeface(Typeface.DEFAULT_BOLD);
            itemAttributeLabel.setTextColor(Color.parseColor("#5d5d5d"));
            itemAttributeLabel.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            itemAttributeLayout.addView(itemAttributeLabel);

            String itemAttribute = in.getStringExtra(menuItemKeys[i]);
            if (menuItemKeys[i].equals(KEY_INGREDIENT)) {
                LinearLayout ingredientLayout = new LinearLayout(SingleMenuItemNutritionFactsActivity.this);
                ingredientLayout.setPadding(dpAsPixels, 0, 0, 0);
                ingredientLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                TextView ingredients = new TextView(SingleMenuItemNutritionFactsActivity.this);
                ingredients.setText(itemAttribute);
                ingredients.setTypeface(Typeface.DEFAULT_BOLD);
                ingredients.setTextColor(Color.parseColor("#acacac"));
                ingredients.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                ingredientLayout.addView(ingredients);
                nutritionLinearLayout.addView(itemAttributeLayout);
                nutritionLinearLayout.addView(ingredientLayout);
            }
            else {
                TextView itemAttributeContent = new TextView(SingleMenuItemNutritionFactsActivity.this);
                itemAttributeContent.setText(itemAttribute);
                itemAttributeContent.setTypeface(Typeface.DEFAULT_BOLD);
                itemAttributeContent.setTextColor(Color.parseColor("#acacac"));
                itemAttributeContent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                itemAttributeLayout.addView(itemAttributeContent);
                nutritionLinearLayout.addView(itemAttributeLayout);
            }
        }
        setContentView(nutritionLayout);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        Tracker t = ((MyApplication) getApplication()).getTracker(MyApplication.TrackerName.APP_TRACKER);
        t.setScreenName("Nutrition Facts");
        t.send(new HitBuilders.AppViewBuilder().build());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }
}
