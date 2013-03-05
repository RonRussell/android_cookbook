package com.tco.examples.autolayoutsettings;

import java.util.ArrayList;
import java.util.List;

import com.tco.utils.FormFactorResolver;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;


public class MySettingsActivity extends AutoLayoutSettingsActivity
{

    @Override
    protected void onConfigureOptions(PreferenceParameters parameters)
    {
        parameters.Phone = PreferenceLayout.SIMPLE;
        parameters.MediumTablet = PreferenceLayout.SIMPLE;
        parameters.LargeTablet = PreferenceLayout.MULTIPANE;
    }

    @Override
    protected List<PreferenceSection> onRequestSimplePreferencesConfiguration()
    {

        ArrayList<PreferenceSection> prefList = new ArrayList<PreferenceSection>();

        /*
         *  If the first preference category will have a title the you must insert at least one empty 
         *  preference section without a title
         */
        
        if (FormFactorResolver.isPhoneFormFactor(this))
        {
            prefList.add(PreferenceSection.getBuilder().setTitle(PreferenceSection.NO_TITLE)
                                                       .setPreference(R.xml.pref_general)
                                                       .addBoundValue("example_text")
                                                       .addBoundValue("example_list")
                                                       .create());
        }
        else
        {
            prefList.add(PreferenceSection.getBuilder().setTitle(PreferenceSection.NO_TITLE)
                                           .setPreference(R.xml.pref_empty)
                                           .create());

            prefList.add(PreferenceSection.getBuilder().setTitle(R.string.pref_header_general)
                                                       .setPreference(R.xml.pref_general)
                                                       .addBoundValue("example_text")
                                                       .addBoundValue("example_list")
                                                       .create());
        }
        
        
        prefList.add(PreferenceSection.getBuilder().setTitle(R.string.pref_header_notifications)
                                                   .setPreference(R.xml.pref_notification)
                                                   .addBoundValue("notifications_new_message_ringtone")
                                                   .create());
        
        prefList.add(PreferenceSection.getBuilder().setTitle(R.string.pref_header_data_sync)
                                                   .setPreference(R.xml.pref_data_sync)
                                                   .addBoundValue("sync_frequency")
                                                   .create());
        
        return prefList;
        
    }

    @Override
    protected int onRequestPreferencesHeaders()
    {
        return R.xml.pref_headers;
    }

    
    
    /**
     * This fragment shows general preferences only. It is used when the activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("example_text"));
            bindPreferenceSummaryToValue(findPreference("example_list"));
        }
    }

    /**
     * This fragment shows notification preferences only. It is used when the activity is showing a two-pane settings
     * UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class NotificationPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_notification);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
        }
    }

    /**
     * This fragment shows data and sync preferences only. It is used when the activity is showing a two-pane settings
     * UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class DataSyncPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_data_sync);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("sync_frequency"));
        }
    }



}
