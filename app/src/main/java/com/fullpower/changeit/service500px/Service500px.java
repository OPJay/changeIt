// "Therefore those skilled at the unorthodox
// are infinite as heaven and earth,
// inexhaustible as the great rivers.
// When they come to an end,
// they begin again,
// like the days and months;
// they die and are reborn,
// like the four seasons."
//
// - Sun Tsu,
// "The Art of War"

package com.fullpower.changeit.service500px;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface Service500px {
    //@GET("/users/self/feed?access_token=1657816247.5b9e1e6.172e8e989d1f4a288cf1ffa7b426f749")
    // @GET("/photos?termArray500px=popular&page=2&image_size=1,600,2048&consumer_key=B2B7Q2CvFc5AEbuGq0KsWgZZXANmsDax0se2QyRo")
    @GET("/photos")
    public void getFeed(@Query("feature")String feature,
                        @Query("rpp")Integer rpp,
                        @Query("only")String only,
                        @Query("page")Integer page,
                        @Query("image_size")String sizes,
                        @Query("consumer_key")String key,
                        @Query("exclude")String exclude,
                        @Query("tags")Integer tags,
                        Callback<Feed500px> callback);
    @GET("/photos/search")
    public void getFeedSearch(@Query("term")String term,
                        @Query("rpp")Integer rpp,
                        @Query("page")Integer page,
                        @Query("image_size")String sizes,
                        @Query("consumer_key")String key,
                        @Query("exclude")String exclude,
                        @Query("tags")Integer tags,
                        @Query("sort")String sort,
                        Callback<Feed500px> callback);

}

