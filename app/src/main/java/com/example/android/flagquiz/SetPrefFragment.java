package com.example.android.flagquiz;

import android.os.Bundle;
import android.support.v14.preference.MultiSelectListPreference;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Iterator;
import java.util.Set;


/**
 * Created by 92324 on 2017/8/9.
 */

public class SetPrefFragment extends PreferenceFragmentCompat {

//    private static final String KEY_CHOICE_NUM_PREF = "choiceSelection";
 //   private static final String KEY_REGIONS_PREF = "regionSelection";
    private static final String SETTING_STR = "Setting";

    private ListPreference mButtonNumPref;
    private MultiSelectListPreference mRegionsPref;
//    private int mRegionCode;
//    private int mButtonNum;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        AppCompatActivity activity = (AppCompatActivity)getActivity();
        if(activity.getSupportActionBar() != null)
        {
            activity.getSupportActionBar().setTitle(SETTING_STR);
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        super.onResume();
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.preferences);

        mButtonNumPref = (ListPreference)findPreference(getResources().getString(R.string.pref_key_button_num));
        mRegionsPref = (MultiSelectListPreference)findPreference(getResources().getString(R.string.pref_key_region_selection));

//        updateRegionCode(mRegionsPref.getValues());
//        mButtonNum = Integer.parseInt(mButtonNumPref.getValue());

        mRegionsPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                Set<String> newValue = (Set<String>) o;
                if(!newValue.isEmpty())
                    mRegionsPref.setValues(newValue);
                else
                    Toast.makeText(getContext(),R.string.region_selection_warning,Toast.LENGTH_SHORT).show();
//              updateRegionCode(newValue);
                return false;
            }
        });

        mButtonNumPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                String newValue = (String)o;
                mButtonNumPref.setValue(newValue);
//              mButtonNum = Integer.parseInt(newValue);
                return false;
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case android.R.id.home:
                getActivity().getSupportFragmentManager().popBackStack();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

//    private void updateRegionCode(Set<String> value)
//    {
//        mRegionCode = 0;
//        Iterator<String> it = value.iterator();
//        while(it.hasNext())
//        {
//            mRegionCode |= Integer.decode(it.next());
//        }
//        return;
//    }

}
