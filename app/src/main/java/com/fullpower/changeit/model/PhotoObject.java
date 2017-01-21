package com.fullpower.changeit.model;

import android.text.Html;
import android.util.Log;

import com.fullpower.changeit.service500px.Photo500px;
import com.fullpower.changeit.serviceAlphaCoders.Wallpaper;
import com.fullpower.changeit.serviceFlickr.PhotoFlickr;
import com.fullpower.changeit.servicePixabay.Hit;
import com.fullpower.changeit.serviceUnSplash.PhotoUnSplash;
import com.fullpower.changeit.utils.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by OJaiswal153939 on 6/21/2016.
 */
public class PhotoObject {
    public String  imageID;
    public String url;
    public String title;
    public String profileUrl;
    public String userUrl;
    public String tags;
    public String description;
    public String imageType;
    public String username;

    public String getSmallUrl() {
        return smallUrl;
    }

    public void setSmallUrl(String smallUrl) {
        this.smallUrl = smallUrl;
    }

    public String smallUrl;
    public String getUsername() {
        return username;
    }

    public void setUsername(String uName) {
        this.username = uName;
    }

    public PhotoObject(String id)
    {
        imageID=id;
    }

    public PhotoObject()
    {
    }
    public void setPhotoObject(PhotoObject photoObject,Object ob)
    {
        String tag="";
        String tagArray[]={};
        String htmlTextStr;
        int isInstanceOf= Utils.isInstanceOf(ob);
        switch (isInstanceOf){
            case 0:
                Photo500px mPhoto500px = (Photo500px) ob;
                photoObject.setImageID(Integer.toString(mPhoto500px.id));
                photoObject.setTitle(mPhoto500px.name);
                photoObject.setUsername(mPhoto500px.user.username);
                htmlTextStr="";
                if (mPhoto500px.description != null)
                    htmlTextStr = Html.fromHtml(mPhoto500px.description).toString();
                photoObject.setImageType("500px");
                photoObject.setProfileUrl(mPhoto500px.user.avatars.large.https);
                tag="";
                for(int i=0;i<mPhoto500px.tags.size();i++)
                    tag=tag+mPhoto500px.tags.get(i)+",";
                photoObject.setTags(tag);
                photoObject.setUrl(mPhoto500px.images.get(3).url);
                photoObject.setUserUrl("https://500px.com" + mPhoto500px.url);
                photoObject.setSmallUrl(mPhoto500px.images.get(1).url);
                break;
            case 1:
                PhotoFlickr mPhotoFlickr = (PhotoFlickr) ob;
                photoObject.setImageID(mPhotoFlickr.id);
                photoObject.setTitle(mPhotoFlickr.title);
                photoObject.setUsername(mPhotoFlickr.ownername);
                htmlTextStr="";
                if (mPhotoFlickr.description._content != null)
                    htmlTextStr = Html.fromHtml(mPhotoFlickr.description._content).toString();
                photoObject.setDescription(htmlTextStr);
                photoObject.setImageType("flickr");
                photoObject.setProfileUrl(mPhotoFlickr.getUserProfileUrl());
                photoObject.setTags(mPhotoFlickr.tags);
                photoObject.setUrl(mPhotoFlickr.url_l);
                photoObject.setUserUrl("https://flickr.com/photos/" + mPhotoFlickr.pathalias);
                photoObject.setSmallUrl(mPhotoFlickr.url_m);
                break;
            case 2:
                PhotoUnSplash mPhotoUnsplash = (PhotoUnSplash) ob;
                photoObject.setImageID(mPhotoUnsplash.id);
                photoObject.setUsername(mPhotoUnsplash.user.name);
                photoObject.setImageType("unsplash");
                photoObject.setProfileUrl(mPhotoUnsplash.user.profile_image.small);
                photoObject.setUrl(mPhotoUnsplash.urls.regular);
                photoObject.setUserUrl("https://unsplash.com/@" + mPhotoUnsplash.user.username);
                photoObject.setSmallUrl(mPhotoUnsplash.urls.small);
                break;
            case 3:
                Hit mPhotoPixabay = (Hit) ob;
                photoObject.setImageID(Integer.toString(mPhotoPixabay.id));
                tagArray = mPhotoPixabay.tags.split(",");
                if (tagArray.length > 0)
                    photoObject.setTitle(tagArray[0]);
                photoObject.setTitle(mPhotoPixabay.user);
                photoObject.setUsername(mPhotoPixabay.user);
                if (tagArray.length > 0)
                    photoObject.setTitle(tagArray[0]);
                htmlTextStr="";
                if (tagArray.length > 1) {
                    htmlTextStr = Html.fromHtml(tagArray[1]).toString();
                }
                photoObject.setImageType("pixabay");
                String x=mPhotoPixabay.webformatURL;
                x.replaceAll("(_640)","_960");
                photoObject.setProfileUrl(mPhotoPixabay.userImageURL);
                photoObject.setTags(mPhotoPixabay.tags);
                photoObject.setUrl(x);
                photoObject.setUserUrl("https://500px.com" + mPhotoPixabay.pageURL);
                photoObject.setSmallUrl(mPhotoPixabay.webformatURL);
                break;
            case 4:
                Wallpaper mWallpaper = (Wallpaper) ob;
                photoObject.setImageID(mWallpaper.id);
                photoObject.setUsername(mWallpaper.user_name);
                photoObject.setTitle(mWallpaper.feedInfo.wallpaper.name);
                htmlTextStr="";
                if (mWallpaper.feedInfo != null && mWallpaper.feedInfo.tags.size() > 1)
                    htmlTextStr = Html.fromHtml(mWallpaper.feedInfo.tags.get(1).name).toString();
                photoObject.setDescription(htmlTextStr);
                photoObject.setImageType("alphacoders");
                photoObject.setProfileUrl("http://static.alphacoders.com/avatars/" + mWallpaper.user_id + ".jpg");
                tag="";
                for(int i=0;i<mWallpaper.feedInfo.tags.size();i++)
                    tag=tag+mWallpaper.feedInfo.tags.get(i)+",";
                photoObject.setTags(tag);
                photoObject.setUrl(mWallpaper.url_image);
                photoObject.setUserUrl("http://photos.alphacoders.com/users/profile/" + mWallpaper.user_id);
                photoObject.setSmallUrl(mWallpaper.url_thumb.replaceAll("15.1","15.2"));
                break;
        }
    }
    public String getImageID() {
        return imageID;
    }

    public void setImageID(String imageID) {
        this.imageID = imageID;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getUserUrl() {
        return userUrl;
    }

    public void setUserUrl(String userUrl) {
        this.userUrl = userUrl;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }


}
