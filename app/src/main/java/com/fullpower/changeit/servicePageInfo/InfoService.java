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

package com.fullpower.changeit.servicePageInfo;

import com.fullpower.changeit.serviceFlickr.FeedFlickr;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;


public interface InfoService {
    //https://api.flickr.com/services/rest/?method=flickr.interestingness.getList&api_key=948cd04fd785ea467f2a63229f0f5cbe&date=2016-04-15&extras=description%2C+license%2C+date_upload%2C+date_taken%2C+owner_name%2C+icon_server%2C+original_format%2C+last_update%2C+geo%2C+tags%2C+machine_tags%2C+o_dims%2C+views%2C+media%2C+path_alias%2C+url_sq%2C+url_t%2C+url_s%2C+url_q%2C+url_m%2C+url_n%2C+url_z%2C+url_c%2C+url_l%2C+url_o&per_page=100&page=1&format=json&nojsoncallback=1&auth_token=72157667221220785-0196a7e6860d7c9a&api_sig=b2cb6dae1694c40882e01ead1c9ce70b
    @GET("/db/")
    public void getImageSearch(@Query("db") String key,
                               Callback<FeedPageInfo> callback
    );
}

