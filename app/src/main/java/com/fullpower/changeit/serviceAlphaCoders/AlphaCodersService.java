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

package com.fullpower.changeit.serviceAlphaCoders;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface AlphaCodersService {
    //https://wall.alphacoders.com/api2.0/get.php?auth=496a2ee50b1fffc11ce9b4d3e0238827&method=featured&info_level=3&page=1&check_last=1
    @GET("/get.php")
    public void getFeed(@Query("auth") String key,
                        @Query("method") String method,
                        @Query("info_level") String info_level,
                        @Query("page") Integer page,
                        @Query("check_last") String check_last,
                        Callback<FeedAlphaCoders> callback
    );
    @GET("/get.php")
    public void getFeedInfo(@Query("auth") String key,
                            @Query("method") String method,
                            @Query("id") String id,
                            Callback<FeedInfo> callback
    );
}