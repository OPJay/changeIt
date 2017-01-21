package com.fullpower.changeit.model;

/**
 * Created by OJaiswal153939 on 6/19/2016.
 */
public class SettingsObjects {
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    String title;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    String description;
    public SettingsObjects(String t,String d)
    {
        this.title=t;
        this.description=d;
    }
}
