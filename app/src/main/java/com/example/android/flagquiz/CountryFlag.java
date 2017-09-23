package com.example.android.flagquiz;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 92324 on 2017/8/6.
 */

public class CountryFlag {

    private static final String TAG = "CountryFlag";
    private static final String ASIA_FLAG_FOLDER = "Asia";
    private static final String AFRICA_FLAG_FOLDER = "Africa";
    private static final String EUROPE_FLAG_FOLDER = "Europe";
    private static final String NORTH_AMERICA_FLAG_FOLDER = "North_America";
    private static final String OCEANIA_FLAG_FOLDER = "Oceania";
    private static final String SOUTH_AMERICA_FLAG_FOLDER = "South_America";
    private static final int AFRICA_CODE = 0x01;
    private static final int ASIA_CODE = 0x02;
    private static final int EUROPE_CODE = 0x04;
    private static final int NORTH_AMERICA_CODE = 0x08;
    private static final int OCEANIA_CODE = 0x10;
    private static final int SOUTH_AMERICA_CODE = 0x20;
    private List<FlagOfCountry> mCountryFlag = new ArrayList<>();
    private List<String> mCountryName = new ArrayList<>();
    private static CountryFlag countryFlag;
    private AssetManager mAssets;

    public static CountryFlag getInstance(Context context)
    {
        if(countryFlag == null)
        {
            countryFlag = new CountryFlag(context);
        }
        return countryFlag;
    }


    private CountryFlag(Context context){
        mAssets = context.getAssets();
 //       loadFlag(AFRICA_CODE | ASIA_CODE | EUROPE_CODE | NORTH_AMERICA_CODE | OCEANIA_CODE | SOUTH_AMERICA_CODE);
    }


    private void addCountryFlag(String continentFolder)
    {
        String[] countryNames;
        String[] components;
        String CountryName;
        try {
            countryNames = mAssets.list(continentFolder);
            Log.i(TAG, "Found " + countryNames.length + " Countries");
        } catch (IOException ioe) {
            Log.e(TAG, "Could not list assets", ioe);
            return;
        }
        for (String filename : countryNames) {
            String assetPath = continentFolder + "/" + filename;
            components = assetPath.split("/");
            filename = components[components.length - 1];
            StringBuilder strBuilder = new StringBuilder(continentFolder);
            strBuilder.append("-");
            CountryName = filename.replace(".png", "").replace(strBuilder.toString(),"");
            FlagOfCountry flag = new FlagOfCountry(CountryName,assetPath);
            mCountryName.add(CountryName);
            mCountryFlag.add(flag);
        }
    }

    public List<FlagOfCountry>  getFlags(int continentCode)
    {
        mCountryFlag.clear();
        if((continentCode & ASIA_CODE) != 0)
            addCountryFlag(ASIA_FLAG_FOLDER);
        if((continentCode & AFRICA_CODE) != 0)
            addCountryFlag(AFRICA_FLAG_FOLDER);
        if((continentCode & EUROPE_CODE) != 0)
            addCountryFlag(EUROPE_FLAG_FOLDER);
        if((continentCode & NORTH_AMERICA_CODE) != 0)
            addCountryFlag(NORTH_AMERICA_FLAG_FOLDER);
        if((continentCode & OCEANIA_CODE) != 0)
            addCountryFlag(OCEANIA_FLAG_FOLDER);
        if((continentCode & SOUTH_AMERICA_CODE) != 0)
            addCountryFlag(SOUTH_AMERICA_FLAG_FOLDER);
        return mCountryFlag;
    }

    public List<String> getAsianName()
    {
        return mCountryName;
    }
}
