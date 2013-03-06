/*
 * Copyright (c) 2013 Two Chips Off, LLC
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 */


package com.tco.preference;

import android.annotation.TargetApi;
import android.content.Context;
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
import java.util.List;

import com.tco.utils.FormFactorResolver;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. 
 * <p>
 * By default, on handset devices and medium (approximately 7") tablets, settings are presented as a single list. 
 * On larger tablets, settings are split by category, with category headers shown to the left of the list of settings.
 * The application may override the default setting for layout on handset, and medium and large tablets.
 * <p>
 * An application uses this class by providing a derived class that also defines the fragments to be used for a multi-pane layout.
 * The following sample code overrides the required abstract methods to provide configuration and preference layout information as 
 * well as defining the fragments associated with the layouts.
 * 
 * {@sample \Dev\source\android_cookbook\AutoLayoutSettingsActivity\assets\MySettingsActivity.java.txt }
 *
 * <p>The pref_headers resource describes the preferences to be displayed for multi-pane layouts and the fragments associated with them
 * 
 * {@sample \Dev\source\android_cookbook\AutoLayoutSettingsActivity\assets\pref_headers.xml.txt }
 *
 * <p>The first header is shown by GeneralPreferenceFragment, which populates itself from the following XML resource
 * 
 * {@sample \Dev\source\android_cookbook\AutoLayoutSettingsActivity\assets\pref_general.xml.txt }
 *
 * <p>See {@link PreferenceFragment} for information on implementing the fragments themselves.
 * 
 * <p> See <a href="http://developer.android.com/design/patterns/settings.html"> Android Design: Settings</a> for design
 * guidelines and the <a href="http://developer.android.com/guide/topics/ui/settings.html">Settings API Guide</a> for
 * more information on developing a Settings UI.
 */
@SuppressWarnings("deprecation")
public abstract class AutoLayoutSettingsActivity extends PreferenceActivity
{
    
    /**
     * Indicates whether to show the simplified settings UI, where settings are presented in a single list, or whether
     * settings are shown as a master/detail two-pane view (generally on tablets). 
     */
    public enum PreferenceLayout
    {
        SIMPLE,
        MULTIPANE
    };
    

    /**
     * Identifies a string resource id to be used for a {@link RintonePreference} summary when set to Silent.
     */
    private static int _idStringRingtoneSilent = -1;
    
    /**
     * Contains preference configuration information specified by the derived class. 
     */
    private PreferenceParameters _parameters = new PreferenceParameters();
    
    /**
     * <p>Called to gather configuration options</p>
     * 
     * {@more}  
     * <p>This method is called to provide derived classes with the opportunity to provide any 
     * configuration options that are unique to their specific requirements.</p>
     *  
     * @see AutoLayoutSettingsActivity.PreferenceParameters
     *  
     */
    abstract protected void onConfigureOptions(PreferenceParameters parameters);

    /**
     * <p>Called to gather preference layout information</p>
     * 
     * {@more}  
     * <p>THis method is called to allow derived classes to provide information about the layout of their 
     * preferences by sections. The returned list of {@link PreferenceSection} is used for laying out 
     * both simple and master/detail style preferences</p>
     * 
     * @return A list of {@link PreferenceSection} generally in the form of an {@link ArrayList}
     * 
     * @see PreferenceSection
     * @see PreferenceSection.Builder
     */
    abstract protected List<PreferenceSection> onRequestSimplePreferencesConfiguration();
    
    /**
     * <p>Called to request the resource id of the preference header</p>
     * 
     * {@more}  
     * <p>For a multi-pane preference layout (master/detail), the derived class will receive this 
     * call to specify the XML resource which defines the preference-headers</p>
     * 
     * @return The XML resource id specifying the preference-headers definition
     */
    abstract protected int onRequestPreferencesHeaders();
    
    /**
     * <p>Called to provide a resource id of a string to be used for {@link RingtonePreference} when "Silent" is selected.</p>
     * 
     * {@more}  
     * <p>This method provides special case handling for the {@link RingtonePreference} to allow the 
     * application to specify what string resource should be used for a summary description when the 
     * user has selected "Silent" or "No Ringtone"</p>
     * 
     * @param resId
     */
    public static void setRingtoneSilentId(int resId)
    {
        _idStringRingtoneSilent = resId;
    }
    
    /** @hide */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        onConfigureOptions(_parameters);

        super.onCreate(savedInstanceState);
        
    }
    
    
    /** @hide */
    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        
        setupSimplePreferencesScreen();
    }

    /**
     * Shows the simplified settings UI if the device configuration dictates that a simplified, 
     * single-pane UI should be shown.
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

    /**
     * Called to determine if the activity should run in multi-pane mode.
     * The default implementation returns true if the screen is large
     * enough.
     */
    @Override
    public boolean onIsMultiPane()
    {
        boolean useSimple = useSimplePreferences(this); 
        return !useSimple;
    }


    /**
     * Determines whether the simplified settings UI should be shown. 
     * 
     * {@more}
     * This is true by default for handset and medium 
     * tablet (approximately 7 inches) devices, or if the device doesn't have newer APIs like {@link PreferenceFragment}.
     * In these cases, a single-pane "simplified" settings UI should be shown. This default behavior can be changed in
     * {@link #onConfigureOptions}. 
     */
    private boolean useSimplePreferences(Context context)
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
            return true;

        if (FormFactorResolver.isHandsetFormFactor(context))
        {
            return _parameters.Handset == PreferenceLayout.SIMPLE;
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

    
    /**
     * Called when the activity needs its list of headers build.  
     * 
     * {@more}
     * By implementing this and adding at least one item to the list, you will cause the activity to run in its 
     * modern fragment mode.  Note that this function may not always be called; for example, if the activity has 
     * been asked to display a particular fragment without the header list, there is no need to build the headers.
     *
     * <p>Typical implementations will use {@link #loadHeadersFromResource} to fill in the list from a resource.
     *
     * @param target The list in which to place the headers.
     */
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
            /** {@inheritDoc} */
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
                        preference.setSummary(_idStringRingtoneSilent);

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
     * Binds a preference's summary to its value. 
     * 
     * {@more}  
     * More specifically, when the preference's value is changed, its summary
     * (line of text below the preference title) is updated to reflect the value. The summary is also immediately
     * updated upon calling this method. The exact display format is dependent on the type of preference.
     */
    protected static void bindPreferenceSummaryToValue(Preference preference)
    {
        /* Set the listener to watch for value changes. */
        preference.setOnPreferenceChangeListener(_bindPreferenceSummaryToValueListener);

        /* Trigger the listener immediately with the preference's current value. */
        _bindPreferenceSummaryToValueListener.onPreferenceChange(preference, PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(), ""));
    }
    

    /**
     *  Define the parameters that are used to configure an instance of {@link AutoLayoutSettingsActivity} 
     */
    public class PreferenceParameters
    {

        /** Contains the current {@link PreferenceLayout} value for a handset */
        public PreferenceLayout Handset = PreferenceLayout.SIMPLE;
        
        /** Contains the current {@link PreferenceLayout} value for a medium tablet */
        public PreferenceLayout MediumTablet = PreferenceLayout.SIMPLE;
        
        /** Contains the current {@link PreferenceLayout} value for a large tablet */
        public PreferenceLayout LargeTablet = PreferenceLayout.MULTIPANE;
        
        /**
         * Constructs a PreferenceParameters with the default values
         */
        public PreferenceParameters()
        {
        }
        
        /**
         * Copy constructor.
         * 
         */
        public PreferenceParameters(PreferenceParameters other)
        {
            Handset = other.Handset;
            MediumTablet = other.MediumTablet;
            LargeTablet = other.LargeTablet;
        }
    }
}









