
package com.fullpower.changeit.serviceAlphaCoders;

public class Wallpaper {

    public String id;
    public String width;
    public String height;
    public String file_type;
    public String file_size;
    public String url_image;
    public String url_thumb;
    public String url_page;
    public String category;
    public String category_id;
    public String sub_category;
    public String sub_categoryId;
    public String user_name;
    public String user_id;
    public Object collection;
    public int collection_id;
    public Object group;
    public int group_id;

    public FeedInfo getFeedInfo() {
        return feedInfo;
    }

    public void setFeedInfo(FeedInfo feedInfo) {
        this.feedInfo = feedInfo;
    }

    public FeedInfo feedInfo;
}
