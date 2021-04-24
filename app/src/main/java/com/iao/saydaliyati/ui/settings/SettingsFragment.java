package com.iao.saydaliyati.ui.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.google.firebase.messaging.FirebaseMessaging;
import com.iao.saydaliyati.R;

public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {

    private SwitchPreferenceCompat notification_weekend, notification_day;
    private Preference feedback;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.fragment_settings, rootKey);

        notification_weekend = findPreference("notification_weekend");
        notification_day = findPreference("notification_day");
        feedback = findPreference("feedback");

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

        feedback.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
                intent.setData(Uri.parse("mailto:contact@saydaliyati.ma"));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return false;
            }
        });
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