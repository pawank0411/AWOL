package com.codex_iter.www.awol;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.widget.Toast;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.MODE_PRIVATE;

public class SettingsFragment extends PreferenceFragment {
    private static final String PREFS_NAME = "prefs";
    private static final String PREF_DARK_THEME = "dark_theme";
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        SharedPreferences preferences = getActivity().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        final boolean useDarkTheme = preferences.getBoolean(PREF_DARK_THEME, false);

        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preference);

        final SwitchPreference darkmode = (SwitchPreference) findPreference("pref_dark_mode");
        final SwitchPreference notifications = (SwitchPreference) findPreference("pref_notification");
        if (darkmode != null) {
            darkmode.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    boolean checked = (Boolean) newValue;

                    toggleTheme(checked);

                    return true;
                }
            });
        }
        /*Setting notification*/
        if (notifications != null){
            notifications.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    boolean checked = (Boolean) newValue;

                    if (checked){
                        Toast.makeText(getActivity(), "Checked", Toast.LENGTH_SHORT).show();
                        Calendar calendar = Calendar.getInstance();

                        calendar.set(Calendar.HOUR_OF_DAY, 14);
                        calendar.set(Calendar.MINUTE, 34);
                        calendar.set(Calendar.SECOND, 0);

                        Intent intent = new Intent(getActivity(), AlramReceiver.class);

                        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 100, intent,PendingIntent.FLAG_UPDATE_CURRENT );

                        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
                    } else if (!checked){
                        Intent intent = new Intent(getActivity(), AlramReceiver.class);

                        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 1, intent,0 );

                        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
                        alarmManager.cancel(pendingIntent);
                    }


                    return true;
                }
            });
        }
        Toast.makeText(getActivity(), String.valueOf(useDarkTheme), Toast.LENGTH_SHORT).show();
        SharedPreferences theme = getActivity().getSharedPreferences("theme",0);
        SharedPreferences.Editor editor = theme.edit();
        editor.putBoolean("dark_theme", useDarkTheme);
        editor.apply();
    }
    private void toggleTheme(boolean darkTheme) {
        Toast.makeText(getActivity(), "Dark", Toast.LENGTH_SHORT).show();
        SharedPreferences.Editor editor = getActivity().getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putBoolean(PREF_DARK_THEME, darkTheme);
        editor.apply();

        Intent intent = getActivity().getIntent();
        getActivity().finish();

        startActivity(intent);
    }
}