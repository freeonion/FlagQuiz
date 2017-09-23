package com.example.android.flagquiz;

/**
 * Created by 92324 on 2017/8/6.
 */

public class FlagOfCountry {
    private String mCorretCountryName;
    private String mAssetPath;

    FlagOfCountry(String corretCountry,String assetPath)
    {
        this.mCorretCountryName = corretCountry;
        this.mAssetPath = assetPath;
    }


    public String getCorretCountryName() {
        return mCorretCountryName;
    }

    public String getAssetPath() {
        return mAssetPath;
    }
}
