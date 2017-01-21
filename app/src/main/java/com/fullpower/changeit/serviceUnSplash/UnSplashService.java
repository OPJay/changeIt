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

package com.fullpower.changeit.serviceUnSplash;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * http://instagram.com/developer/endpoints/
 */
public interface UnSplashService {
    //https://api.unsplash.com/photos?client_id=c288827579791e3fe71e35bdd6543d18fc2c594a0d94a8af055ce42efb726b0e&page=1&per_page=30
    @GET("/photos/curated")
    public void getFeed(@Query("client_id") String client_id,
                        @Query("perPageFlickr") String per_page,
                        @Query("page") String page,
                        Callback<List<PhotoUnSplash>> callback
    );

}

