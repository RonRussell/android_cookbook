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

import java.util.ArrayList;

import android.content.Context;

/**
 * 
 * Defines a single preference section
 * 
 * {@more}
 * This class is used to provide layout information to {@link AutoLayoutSettingsActivity} within the scope 
 * of a call to {@link AutoLayoutSettingsActivity#onRequestSimplePreferencesConfiguration()}. A single preference 
 * section includes an optional preference header title string resource and the preference layout resource. A 
 * {@link PreferenceSection} may also specify a set of {@link String} keys which represent preference descriptions
 * that are bound at runtime. 
 */
public class PreferenceSection
{
    /** Constant to indicate that no header title shoud be displayed */
    public final static int NO_TITLE = -1;
    
    private int _titleId;
    private int _prefId;
    private ArrayList<String> _boundValues = new ArrayList<String>();
    
    /**
     * Construct a {@link PreferenceSection} with a title string resource and a preference layout resource 
     * @param title The id of a string resource to be used as the title
     * @param prefs The id of a PreferenceScreen XML resource to be used as the preference body
     */
    public PreferenceSection(int title, int prefs)
    {
        this(title, prefs, null);
    }
    
    /**
     * Construct a {@link PreferenceSection} with a title string resource, a preference layout resource, and a set of bound descriptions 
     * @param title The id of a string resource to be used as the title
     * @param prefs The id of a PreferenceScreen XML resource to be used as the preference body
     * @param boundValues A set of keys representing description values defined in <em>prefs</em> that should be bound at runtime
     */
    public PreferenceSection(int title, int prefs, String [] boundValues)
    {
        _titleId = title;
        _prefId = prefs;
        
        if (null != boundValues)
            for (String val : boundValues)
                addBoundValue(val);
    }
    
    /**
     * Adds a new entry to the set keys representing description values that should be bound to defined preferences at runtime
     * @param val A {@link String} key defined in the preference layout resource specified to {@link PreferenceSection#PreferenceSection(int, int)} 
     */
    public void addBoundValue(String val)
    {
        _boundValues.add(val);
    }

    /**
     * Get the title
     * @return the string resource id of the title
     */
    public int getTitle()
    {
        return _titleId;
    }

    /**
     * Get the layout
     * @return the id of the XML layout resource used as the preference body
     */
    public int getPref()
    {
        return _prefId;
    }

    /**
     * Get the bound description values
     * @return A set of keys representing description values that should be bound at runtime
     */
    public ArrayList<String> getBoundValues()
    {
        return _boundValues;
    }

    /**
     * Get a new {@link PreferenceSection.Builder}
     * @return
     */
    public static Builder getBuilder()
    {
        return new Builder();
    }
    
    
    /**
     * Utility class used to build a new {@link PreferenceSection} 
     *
     */
    public static class Builder
    {
        /** @hide */
        final PreferenceSection _pref = new PreferenceSection(0, 0);
        
        /**
         * Set the preference header title
         * @param title The id of a string resource to be used as the title
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setTitle(int title)
        {
            _pref._titleId = title;
            
            return this;
        }
        
        /**
         * Set the preference body layout
         * @param pref The id of a PreferenceScreen XML resource to be used as the preference body
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setPreference(int pref)
        {
            _pref._prefId = pref;
            
            return this;
        }
        
        /**
         * Add a new key to the list of values bound to preference descriptions at runtime
         * @param val A string which is the key for this bound value
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder addBoundValue(String val)
        {
            _pref.addBoundValue(val);
            
            return this;
        }
        
        /**
         * Add a new key to the list of values bound to preference descriptions at runtime 
         * @param id The id of a string resource which is the key for this bound value
         * @param context A calling context which can load the string resource specified in <em>id</em>
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder addBoundValue(int id, Context context)
        {
            _pref.addBoundValue(context.getResources().getString(id));
            
            return this;
        }
        
        /**
         * Creates a {@link PreferenceSection} with the arguments supplied to this builder 
         */
        public PreferenceSection create()
        {
            return _pref;
        }
        
    }
    
}
