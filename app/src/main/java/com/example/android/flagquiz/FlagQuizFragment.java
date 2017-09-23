package com.example.android.flagquiz;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Created by 92324 on 2017/8/6.
 */

public class FlagQuizFragment extends Fragment {

    private CountryFlag mCountryFlag;
    private FlagOfCountry mFlagOfCountry;
    private String mCountryName;
    private List<FlagOfCountry> mFlagImages;
    private ArrayList<Button> mButtonList;
    private Button optionButtonRight;
    private ImageView mImageView;
    private TextView mResultView;
    private ArrayList<Integer> indexOfCountries;
    private ArrayList<Integer> indexOfButtons;
    private GridLayout mGridLayout;
    private TextView mTitleView;
    private LinearLayout mQuizLinearLayout;
    private static final String TAG = "FlagQuizFragment";
    private static final Integer defaultButtonNum = 4;
    private static final int questionNum = 10;
    private Set<String> mRegionValue;
    private int mOptionsNum;
    private static int index = 1;
    private static int guessTimes = 0;
    private static int rightTimes = 0;



    private View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String strText;
            if(view == optionButtonRight)
            {
                strText = mCountryName;
                for(Button button:mButtonList)
                    button.setEnabled(false);
                rightTimes++;
                guessTimes++;
                if(index != questionNum)
                {
                    animate(true);
                    index++;
                }
                else
                {
                    float rightPer = (float)rightTimes / (float)guessTimes * 100;
                    String rightPerStr = String.format("%.2f",rightPer);
                    String titleStr = new StringBuilder().append(guessTimes).append(" guesses, ").append(rightPerStr).append("%").append(" correct").toString();
                    new AlertDialog.Builder(getActivity()).setTitle(titleStr).setPositiveButton(R.string.reset_question, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            index = 1;
                            guessTimes = 0;
                            rightTimes = 0;
                            newQuestion();
                        }
                    }).create().show();
                }
            }
            else
            {
                guessTimes++;
                strText = "incorrect";
                view.setEnabled(false);
                imageAnimation();
            }
            mResultView.setVisibility(View.VISIBLE);
            mResultView.setText(strText);


        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_flag_quiz,container,false);

        mGridLayout = v.findViewById(R.id.gridLayout);
        mImageView = v.findViewById(R.id.flagImageView);
        mResultView = v.findViewById(R.id.resultTextView);
        mTitleView = v.findViewById(R.id.titleTextView);
        mQuizLinearLayout = v.findViewById(R.id.quizLinearLayout);
        AppCompatActivity activity = (AppCompatActivity)getActivity();
        if(activity.getSupportActionBar() != null)
        {
            activity.getSupportActionBar().setTitle(R.string.app_name);
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
        StringBuilder strBuilder = new StringBuilder().append("Question ").append(index).append(" of ").append(questionNum);
        mTitleView.setText(strBuilder.toString());

        //设置默认值，只在程序第一次安装时有效
        PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences, false);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mOptionsNum = Integer.decode(settings.getString(getResources().getString(R.string.pref_key_button_num),defaultButtonNum.toString()));
        if(mButtonList == null)
        {
            mButtonList = new ArrayList<>();
        }
        mButtonList.clear();
        mGridLayout.removeAllViews();
        for(int i = 0; i < mOptionsNum; i++)
        {
            Button button = new Button(getContext());
            mGridLayout.addView(button);
            mButtonList.add(button);
            button.setOnClickListener(buttonClickListener);
            // 目的是让GridLayout一排中两个button占据同样的宽度，相对于设置weight
            ((GridLayout.LayoutParams) button.getLayoutParams()).columnSpec =
                    GridLayout.spec(GridLayout.UNDEFINED, 1f);
            button.setWidth(0);
            //不加这一句的话，同排button，一个字数达到两行后，两个button对不齐
            ((GridLayout.LayoutParams) button.getLayoutParams()).rowSpec =
                    GridLayout.spec(GridLayout.UNDEFINED, 1f);
            button.setHeight(0);
            button.setMaxLines(2);
        }

        mRegionValue =  settings.getStringSet(getResources().getString(R.string.pref_key_region_selection),null);
        newQuestion();
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_flag_quiz, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_setting:
                FragmentManager fm = getActivity().getSupportFragmentManager();
                Fragment fragment = new SetPrefFragment();

                fm.beginTransaction().hide(this).add(R.id.fragment_container,fragment).addToBackStack(null).commit();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private ArrayList<Integer> generateRandomIndex(int bound)
    {
        Random randomGenerator = new Random();
        ArrayList<Integer> arrayList = new ArrayList<>();

        while(arrayList.size() < mOptionsNum)
        {
            int random = randomGenerator.nextInt(bound);
            if(!arrayList.contains(random))
            {
                arrayList.add(random);
            }
        }
        return arrayList;
    }

    private Button getCorrespondingButton(int index)
    {
        Button buttonRet = null;


        if(index < mOptionsNum && mButtonList != null)
            buttonRet = mButtonList.get(index);
        return buttonRet;
    }

//    @Override
//    public void onResume() {
//        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
//        int optionsNum = Integer.decode(settings.getString(getResources().getString(R.string.pref_key_button_num),defaultButtonNum.toString()));
//        Set<String> regionValue = settings.getStringSet(getResources().getString(R.string.pref_key_region_selection),null);
//        if(optionsNum != mOptionsNum || !regionValue.equals(mRegionValue))
//        {
//            FragmentManager fm = getActivity().getSupportFragmentManager();
//            Fragment fragment = new FlagQuizFragment();
//            fm.beginTransaction().replace(R.id.fragment_container, fragment).commit();
//        }
//        super.onResume();
//    }

    //使用Fragment的hide方法时，发现OnResume不会被调用，使用replace后返回OnResume是会被调用的
    @Override
    public void onHiddenChanged(boolean hidden) {

        if(!hidden)
        {
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
            int optionsNum = Integer.decode(settings.getString(getResources().getString(R.string.pref_key_button_num),defaultButtonNum.toString()));
            Set<String> regionValue = settings.getStringSet(getResources().getString(R.string.pref_key_region_selection),null);
            if(optionsNum != mOptionsNum || !regionValue.equals(mRegionValue))
            {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                Fragment fragment = new FlagQuizFragment();
                fm.beginTransaction().replace(R.id.fragment_container, fragment).commit();
            }
        }
        super.onHiddenChanged(hidden);
    }

    private void imageAnimation()
    {

        float animatorLeft = getResources().getDimension(R.dimen.imageview_animator_left);
        float animatorRight = getResources().getDimension(R.dimen.imageview_animator_right);
        //More than two values imply a starting value, values to animate through along the way, and an ending value
        //https://developer.android.com/reference/android/animation/ObjectAnimator.html#ofFloat(T, android.util.Property<T, java.lang.Float>, float...)
        ObjectAnimator imageAnimator = ObjectAnimator.ofFloat(mImageView,"translationX",0,animatorLeft,animatorRight,0);
        imageAnimator.setDuration(500);
        imageAnimator.start();
    }

    private void newQuestion()
    {
        mCountryFlag = CountryFlag.getInstance(getContext());

        int mRegionCode = 0;
        Iterator<String> it = mRegionValue.iterator();
        while(it.hasNext())
        {
            mRegionCode |= Integer.decode(it.next());
        }
        mFlagImages = mCountryFlag.getFlags(mRegionCode);
        indexOfCountries = generateRandomIndex(mFlagImages.size());
        indexOfButtons = generateRandomIndex(mOptionsNum);
        mFlagOfCountry = mFlagImages.get(indexOfCountries.get(0));
        mCountryName = mFlagOfCountry.getCorretCountryName();

        optionButtonRight = getCorrespondingButton(indexOfButtons.get(0));
        Button buttonTemp;
        for(int i = 0; i < mOptionsNum; i++)
        {
            buttonTemp = getCorrespondingButton(indexOfButtons.get(i));
            buttonTemp.setText(mFlagImages.get(indexOfCountries.get(i)).getCorretCountryName());
            buttonTemp.setEnabled(true);
        }

        StringBuilder strBuilder = new StringBuilder().append("Question ").append(index).append(" of ").append(questionNum);
        mTitleView.setText(strBuilder.toString());

        mResultView.setVisibility(View.INVISIBLE);
        // load image
        try {
            // get input stream
            InputStream ims = getActivity().getAssets().open(mFlagOfCountry.getAssetPath());
            Bitmap b = BitmapFactory.decodeStream(ims);
            b.setDensity(Bitmap.DENSITY_NONE);
            mImageView.setImageBitmap(b);

            animate(false);
        }
        catch(IOException ex) {
            Log.d("TAG","failed to load picture");
        }
    }

    private void animate(boolean animateOut) {
        // prevent animation into the the UI for the first flag
        if (rightTimes == 0)
            return;

        // calculate center x and center y
        int centerX = (mQuizLinearLayout.getLeft() +
                mQuizLinearLayout.getRight()) / 2; // calculate center x
        int centerY = (mQuizLinearLayout.getTop() +
                mQuizLinearLayout.getBottom()) / 2; // calculate center y

        // calculate animation radius
        int radius = Math.max(mQuizLinearLayout.getWidth(),
                mQuizLinearLayout.getHeight());

        Animator animator;

        // if the quizLinearLayout should animate out rather than in
        if (animateOut) {
            // create circular reveal animation
            animator = ViewAnimationUtils.createCircularReveal(
                    mQuizLinearLayout, centerX, centerY, radius, 0);
            animator.addListener(
                    new AnimatorListenerAdapter() {
                        // called when the animation finishes
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            newQuestion();
                        }
                    }
            );
        }
        else { // if the quizLinearLayout should animate in
            animator = ViewAnimationUtils.createCircularReveal(
                    mQuizLinearLayout, centerX, centerY, 0, radius);
        }

        animator.setDuration(500); // set animation duration to 500 ms
        animator.start(); // start the animation
    }
}
