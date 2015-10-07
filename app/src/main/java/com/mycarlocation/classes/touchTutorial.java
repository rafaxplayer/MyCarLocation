package com.mycarlocation.classes;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.mycarlocation.R;

import java.util.Set;

/**
 * Created by rafaxplayer on 06/10/2015.
 */
public class touchTutorial extends DialogFragment {
    private SharedPreferences prefs;


    public touchTutorial() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tutorial_touch, container);
    }
    @Override
    public void onViewCreated(View view,Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
                SharedPreferences.Editor ed=prefs.edit();
                ed.putBoolean("FirstUse", false);
                ed.commit();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

    }
}

