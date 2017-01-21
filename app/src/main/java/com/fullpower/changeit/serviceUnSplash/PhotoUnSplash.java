
package com.fullpower.changeit.serviceUnSplash;

import java.util.ArrayList;
import java.util.List;

public class PhotoUnSplash {

    public String id;
    public int width;
    public int height;
    public String color;
    public int likes;
    public boolean liked_by_user;
    public User user;
    public List<Object> current_user_collections = new ArrayList<Object>();
    public Urls urls;
    public List<Category> categories = new ArrayList<Category>();
    public Links_Photo links_photo;

}
