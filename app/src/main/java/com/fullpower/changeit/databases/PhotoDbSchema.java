package com.fullpower.changeit.databases;

/**
 * Created by OJaiswal153939 on 1/8/2016.
 */
public class PhotoDbSchema {
    public static final class PhotoTable {
        public static final String NAME = "photos";
        public static final class Cols {
            public static final String IMAGEID = "imageid";
            public static final String TITLE = "title";
            public static final String URL = "url";
            public static final String PROFILE_URL = "profile_url";
            public static final String USER_URL= "suspect";
            public static final String TAGS="tags";
            public static final String DESCRIPTION="description";
            public static final String IMAGETYPE="image_type";
            public static final String USERNAME="username";
            public static final String SMALLIMAGEURL="smallurl";

        }
    }
}
