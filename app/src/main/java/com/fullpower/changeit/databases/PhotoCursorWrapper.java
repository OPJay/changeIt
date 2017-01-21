package com.fullpower.changeit.databases;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.fullpower.changeit.databases.PhotoDbSchema.PhotoTable;
import com.fullpower.changeit.model.PhotoObject;

/**
 * Created by OJaiswal153939 on 1/9/2016.
 */
public class PhotoCursorWrapper extends CursorWrapper {
    public PhotoCursorWrapper(Cursor cursor) {
        super(cursor);
    }
    public PhotoObject getPhoto() {
        String imageID =
                getString(getColumnIndex(PhotoTable.Cols.IMAGEID));
        String title =
                getString(getColumnIndex(PhotoTable.Cols.TITLE));
        String url = getString(getColumnIndex(PhotoTable.Cols.URL));
        String profileUrl = getString(getColumnIndex(PhotoTable.Cols.PROFILE_URL));
        String userUrl = getString(getColumnIndex(PhotoTable.Cols.USER_URL));
        String tags = getString(getColumnIndex(PhotoTable.Cols.TAGS));
        String description = getString(getColumnIndex(PhotoTable.Cols.DESCRIPTION));
        String imageType= getString(getColumnIndex(PhotoTable.Cols.IMAGETYPE));
        String username=getString(getColumnIndex(PhotoTable.Cols.USERNAME));
        String smallurl=getString(getColumnIndex(PhotoTable.Cols.SMALLIMAGEURL));
        PhotoObject photoObject = new PhotoObject(imageID);
        photoObject.setTitle(title);
        photoObject.setUrl(url);
        photoObject.setProfileUrl(profileUrl);
        photoObject.setUserUrl(userUrl);
        photoObject.setTags(tags);
        photoObject.setDescription(description);
        photoObject.setImageType(imageType);
        photoObject.setUsername(username);
        photoObject.setSmallUrl(smallurl);
        return photoObject;
    }
}
