package com.iao.saydaliyati.ui.settings;

import android.os.Bundle;
import android.util.Log;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.google.firebase.messaging.FirebaseMessaging;
import com.iao.saydaliyati.R;

public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {

    private SwitchPreferenceCompat notification_weekend, notification_day;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.fragment_settings, rootKey);

        notification_weekend = findPreference("notification_weekend");
        notification_day = findPreference("notification_day");

        notification_weekend.setSummaryProvider(new Preference.SummaryProvider() {
            @Override
            public CharSequence provideSummary(Preference preference) {
                if (notification_weekend.isChecked()) {
                    return "Activé";
                } else {
                    return "Désactivé";
                }
            }
        });

        notification_day.setSummaryProvider(new Preference.SummaryProvider() {
            @Override
            public CharSequence provideSummary(Preference preference) {
                if (notification_day.isChecked()) {
                    return "Activé";
                } else {
                    return "Désactivé";
                }
            }
        });

        notification_weekend.setOnPreferenceChangeListener(this);
        notification_day.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {

        String key = preference.getKey();
        boolean isChecked = Boolean.parseBoolean(newValue.toString());

        if (key.equals("notification_weekend")) {
            if (isChecked) {
                FirebaseMessaging.getInstance().subscribeToTopic("weekend");
                Log.d("SETTINGS", key + " is checked");
            } else {
                FirebaseMessaging.getInstance().unsubscribeFromTopic("weekend");
                Log.d("SETTINGS", key + " is not checked");
            }
        }
        else if (key.equals("notification_day")) {
            if (isChecked) {
                FirebaseMessaging.getInstance().subscribeToTopic("every_day");
                Log.d("SETTINGS", key + " is checked");
            } else {
                FirebaseMessaging.getInstance().unsubscribeFromTopic("every_day");
                Log.d("SETTINGS", key + " is not checked");
            }
        }

        return true;
    }
}