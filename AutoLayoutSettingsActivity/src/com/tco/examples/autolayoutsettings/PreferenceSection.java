package com.tco.examples.autolayoutsettings;

import java.util.ArrayList;

import android.content.Context;

public class PreferenceSection
{
    public final static int NO_TITLE = -1;
    
    private int _titleId;
    private int _prefId;
    private ArrayList<String> _boundValues = new ArrayList<String>();
    
    public PreferenceSection(int title, int prefs)
    {
        this(title, prefs, null);
    }
    
    public PreferenceSection(int title, int prefs, String [] boundValues)
    {
        _titleId = title;
        _prefId = prefs;
        
        if (null != boundValues)
            for (String val : boundValues)
                addBoundValue(val);
    }
    
    public void addBoundValue(String val)
    {
        _boundValues.add(val);
    }

    public int getTitle()
    {
        return _titleId;
    }

    public int getPref()
    {
        return _prefId;
    }

    public ArrayList<String> getBoundValues()
    {
        return _boundValues;
    }

    public static Builder getBuilder()
    {
        return new Builder();
    }
    
    
    public static class Builder
    {
        final PreferenceSection _pref = new PreferenceSection(0, 0);
        
        public Builder setTitle(int title)
        {
            _pref._titleId = title;
            
            return this;
        }
        
        public Builder setPreference(int pref)
        {
            _pref._prefId = pref;
            
            return this;
        }
        
        public Builder addBoundValue(String val)
        {
            _pref.addBoundValue(val);
            
            return this;
        }
        
        public Builder addBoundValue(int id, Context context)
        {
            _pref.addBoundValue(context.getResources().getString(id));
            
            return this;
        }
        
        public PreferenceSection create()
        {
            return _pref;
        }
        
    }
    
}
