package com.tco.examples.autolayoutsettings;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.view.Display;

import java.util.List;

import com.tco.utils.FormFactorResolver;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On handset devices, settings are presented
 * as a single list. On tablets, settings are split by category, with category headers shown to the left of the list of
 * settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html"> Android Design: Settings</a> for design
 * guidelines and the <a href="http://developer.android.com/guide/topics/ui/settings.html">Settings API Guide</a> for
 * more information on developing a Settings UI.
 */
@SuppressWarnings("deprecation")
public abstract class AutoLayoutSettingsActivity extends PreferenceActivity
{
    
    enum PreferenceLayout
    {
        SIMPLE,
        MULTIPANE
    };
    
    
    /**
     * Determines whether to always show the simplified settings UI, where settings are presented in a single list. When
     * false, settings are shown as a master/detail two-pane view on tablets. When true, a single pane is shown on
     * tablets.
     */
    private PreferenceParameters _parameters = new PreferenceParameters();
    
    /**
     * <p>Called to gather configuration options</p>
     * 
     * <p>This method is called to provide derived classes with the opportunity to provide any 
     * configuration options that are unique to their specific requirements.</p>
     *  
     * @see AutoLayoutSettingsActivity#setAlwaysUseSimplePreferences(boolean)
     * @see AutoLayoutSettingsActivity#setUseSimplePreferencesForPhone(boolean)
     * @see AutoLayoutSettingsActivity#setUseSimplePreferencesForMediumTablet(boolean)
     * @see AutoLayoutSettingsActivity#setUseSimplePreferencesForLargeTablet(boolean)
     *  
     */
    abstract protected void onConfigureOptions(PreferenceParameters parameters);
    abstract protected List<PreferenceSection> onRequestSimplePreferencesConfiguration();
    abstract protected int onRequestPreferencesHeaders();
    
    

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        onConfigureOptions(_parameters);

        super.onCreate(savedInstanceState);
        
    }
    
    
    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        
        setupSimplePreferencesScreen();
    }

    /**
     * Shows the simplified settings UI if the device configuration if the device configuration dictates that a
     * simplified, single-pane UI should be shown.
     */
    private void setupSimplePreferencesScreen()
    {
        if (!useSimplePreferences(this))
        {
            return;
        }

        // In the simplified UI, fragments are not used at all and we instead
        // use the older PreferenceActivity APIs.
        List<PreferenceSection> prefs = onRequestSimplePreferencesConfiguration();
        
        for (PreferenceSection pref : prefs)
        {
            if (PreferenceSection.NO_TITLE != pref.getTitle())
            {
                PreferenceCategory category = new PreferenceCategory(this);
                category.setTitle(pref.getTitle());
                getPreferenceScreen().addPreference(category);
            }
            
            addPreferencesFromResource(pref.getPref());
            for (String bindKey : pref.getBoundValues())
                bindPreferenceSummaryToValue(findPreference(bindKey));
            
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean onIsMultiPane()
    {
        boolean useSimple = useSimplePreferences(this); 
        return !useSimple;
    }


    /**
     * Determines whether the simplified settings UI should be shown. This is true if this is forced via
     * {@link #ALWAYS_SIMPLE_PREFS}, or the device doesn't have newer APIs like {@link PreferenceFragment}, or the
     * device doesn't have an extra-large screen. In these cases, a single-pane "simplified" settings UI should be
     * shown.
     */
    private boolean useSimplePreferences(Context context)
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
            return true;

        boolean isLandscape = isLandscape();
        
        if (FormFactorResolver.isPhoneFormFactor(context))
        {
            return _parameters.Phone == PreferenceLayout.SIMPLE;
        }
        else if (FormFactorResolver.isMediumTabletFormFactor(context))
        {
            return _parameters.MediumTablet == PreferenceLayout.SIMPLE;
        }
        else if (FormFactorResolver.isLargeTabletFormFactor(context))
        {
            return _parameters.LargeTablet == PreferenceLayout.SIMPLE;
        }
        else
        {
            return true;
        }
        
    }

    
    private boolean isLandscape()
    {
        /* We are intentionally ignoring Configuration.ORIENTATION_SQUARE and Configuration.ORIENTATION_UNDEFINED. 
         * They are to be considered like Configuration.ORIENTATION_PORTRAIT for our purposes 
         */
        
        Display display = getWindowManager().getDefaultDisplay();
        
        Point size = new Point();
        display.getSize(size);
        
        if (size.x > size.y)
            return true;
        else
            return false;
        
    }
    
    
    /** {@inheritDoc} */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target)
    {
        if (!useSimplePreferences(this))
        {
            loadHeadersFromResource(onRequestPreferencesHeaders(), target);
        }
    }

    /**
     * A preference value change listener that updates the preference's summary to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener _bindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object value)
            {
                String stringValue = value.toString();

                if (preference instanceof ListPreference)
                {
                    /* For list preferences, look up the correct display value in the preference's 'entries' list. */
                    ListPreference listPreference = (ListPreference) preference;
                    int index = listPreference.findIndexOfValue(stringValue);

                    /* Set the summary to reflect the new value. */
                    preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);

                }
                
                else if (preference instanceof RingtonePreference)
                {
                    /* For ringtone preferences, look up the correct display value using RingtoneManager. */
                    if (TextUtils.isEmpty(stringValue))
                    {
                        /* Empty values correspond to 'silent' (no ringtone). */
                        preference.setSummary(R.string.pref_ringtone_silent);

                    }
                    else
                    {
                        Ringtone ringtone = RingtoneManager.getRingtone(preference.getContext(), Uri.parse(stringValue));

                        if (ringtone == null)
                        {
                            /* Clear the summary if there was a lookup error. */
                            preference.setSummary(null);
                        }
                        else
                        {
                            /* Set the summary to reflect the new ringtone display name. */
                            String name = ringtone.getTitle(preference.getContext());
                            preference.setSummary(name);
                        }
                    }

                }
                
                else
                {
                    /* For all other preferences, set the summary to the value's simple string representation. */
                    preference.setSummary(stringValue);
                }
                return true;
            }
        };

    /**
     * Binds a preference's summary to its value. More specifically, when the preference's value is changed, its summary
     * (line of text below the preference title) is updated to reflect the value. The summary is also immediately
     * updated upon calling this method. The exact display format is dependent on the type of preference.
     * 
     * @see #_bindPreferenceSummaryToValueListener
     */
    protected static void bindPreferenceSummaryToValue(Preference preference)
    {
        /* Set the listener to watch for value changes. */
        preference.setOnPreferenceChangeListener(_bindPreferenceSummaryToValueListener);

        /* Trigger the listener immediately with the preference's current value. */
        _bindPreferenceSummaryToValueListener.onPreferenceChange(preference, PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(), ""));
    }
    

    public class PreferenceParameters
    {

        public PreferenceLayout Phone = PreferenceLayout.SIMPLE;
        public PreferenceLayout MediumTablet = PreferenceLayout.SIMPLE;
        public PreferenceLayout LargeTablet = PreferenceLayout.MULTIPANE;
        
        public PreferenceParameters()
        {
        }
        
        public PreferenceParameters(PreferenceParameters other)
        {
            Phone = other.Phone;
            MediumTablet = other.MediumTablet;
            LargeTablet = other.LargeTablet;
        }
    }
}









