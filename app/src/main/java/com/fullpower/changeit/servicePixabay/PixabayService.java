package com.fullpower.changeit.servicePixabay;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface PixabayService {
    //https://api.unsplash.com/photos?client_id=c288827579791e3fe71e35bdd6543d18fc2c594a0d94a8af055ce42efb726b0e&page=1&per_page=30
    //https://pixabay.com/api/?key=2389471-fe22bb338c8cc4f593426a26d&image_type=photo&editors_choice=true&order=popular&safesearch=true&page=2&per_page=200
    @GET("/api/")
    public void getFeed(@Query("key") String key,
                        @Query("image_type") String image_type,
                        @Query("editors_choice") Boolean editors_choice,
                        @Query("order")String order,
                        @Query("safesearch")Boolean safesearch,
                        @Query("page")Integer page,
                        @Query("perPageFlickr")Integer per_page,
                        Callback<FeedPixabay> callback
    );

}
