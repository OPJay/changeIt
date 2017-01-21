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

package com.fullpower.changeit.serviceFlickr;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;


public interface FlickrService {
    //https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=4f42d84e2501ec69284614036f9fa39a&text=nature&extras=url_m&&format=json&per_page=30&page=1&sort=relevance&nojsoncallback=1
    //https://api.flickr.com/services/rest/?method=flickr.interestingness.getList&api_key=948cd04fd785ea467f2a63229f0f5cbe&date=2016-04-15&extras=description%2C+license%2C+date_upload%2C+date_taken%2C+owner_name%2C+icon_server%2C+original_format%2C+last_update%2C+geo%2C+tags%2C+machine_tags%2C+o_dims%2C+views%2C+media%2C+path_alias%2C+url_sq%2C+url_t%2C+url_s%2C+url_q%2C+url_m%2C+url_n%2C+url_z%2C+url_c%2C+url_l%2C+url_o&per_page=100&page=1&format=json&nojsoncallback=1&auth_token=72157667221220785-0196a7e6860d7c9a&api_sig=b2cb6dae1694c40882e01ead1c9ce70b
    @GET("/rest/")
    public void getFeed(@Query("method") String method,
                        @Query("api_key") String key,
                        @Query("date") String date,
                        @Query("extras") String extras,
                        @Query("format") String format,
                        @Query("per_page") String per_page,
                        @Query("page") String page,
                        @Query("nojsoncallback") String nonjsoncallback,
                        Callback<FeedFlickr> callback
    );
    @GET("/rest/")
    public void getFeedSearch(@Query("method") String method,
                        @Query("api_key") String key,
                              @Query("text") String term,
                        @Query("extras") String extras,
                        @Query("format") String format,
                        @Query("per_page") String per_page,
                        @Query("page") String page,
                              @Query("sort")String sort,
                        @Query("nojsoncallback") String nonjsoncallback,
                        Callback<FeedFlickr> callback
    );

}

