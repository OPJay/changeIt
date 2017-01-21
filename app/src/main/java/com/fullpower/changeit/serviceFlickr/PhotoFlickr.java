
package com.fullpower.changeit.serviceFlickr;

public class PhotoFlickr {
    public String id;
    public String owner;
    public String secret;
    public String server;
    public Integer farm;
    public String title;
    public Integer ispublic;
    public Integer isfriend;
    public Integer isfamily;
    public Integer license;
    public Description description;
    public String dateupload;
    public String lastupdate;
    public String datetaken;
    public Integer datetakengranularity;
    public Integer datetakenunknown;
    public String ownername;
    public String iconserver;
    public Integer iconfarm;
    public String views;
    public String count_faves;
    public String tags;
    public String machine_tags;
    public String latitude;
    public String longitude;
    public Integer accuracy;
    public Integer context;
    public String media;
    public String media_status;
    public String url_sq;
    public Integer height_sq;
    public Integer width_sq;
    public String url_t;
    public Integer height_t;
    public Integer width_t;
    public String url_s;
    public String height_s;
    public String width_s;
    public String url_q;
    public String height_q;
    public String width_q;
    public String url_m;
    public String height_m;
    public String width_m;
    public String url_n;
    public String height_n;
    public String width_n;
    public String url_z;
    public String height_z;
    public String width_z;
    public String url_c;
    public String height_c;
    public String width_c;
    public String url_l;
    public String height_l;
    public String width_l;
    public String url_o;
    public String pathalias;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserProfileUrl() {
        String result = "http://farm" + iconfarm + ".staticflickr.com/" + iconserver + "/buddyicons/" + owner + ".jpg";
        return (result);
    }

}
