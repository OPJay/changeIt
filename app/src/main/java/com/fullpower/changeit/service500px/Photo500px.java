
package com.fullpower.changeit.service500px;

import java.util.ArrayList;
import java.util.List;

public class Photo500px {
    public int id;
    public int user_id;
    public String name;
    public String camera;
    public Object lens;
    public String focal_length;
    public String iso;
    public String shutter_speed;
    public String aperture;
    public int times_viewed;
    public float rating;
    public int status;
    public String created_at;
    public int category;
    public Object location;
    public Object latitude;
    public Object longitude;
    public String taken_at;
    public int hi_res_uploaded;
    public boolean for_sale;
    public int width;
    public int height;
    public int votes_count;
    public int favorites_count;
    public int comments_count;
    public boolean nsfw;
    public int sales_count;
    public Object for_sale_date;
    public float highest_rating;
    public String highest_rating_date;
    public int license_type;
    public int converted;
    public int collections_count;
    public int crop_version;
    public boolean privacy;
    public boolean profile;
    public String image_url;
    public List<Image> images = new ArrayList<Image>();
    public String url;
    public int positive_votes_count;
    public int converted_bits;
    public List<String> tags=new ArrayList<String>();
    public boolean watermark;
    public String image_format;
    public User user;
    public boolean licensing_requested;
    public String description;
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

}
