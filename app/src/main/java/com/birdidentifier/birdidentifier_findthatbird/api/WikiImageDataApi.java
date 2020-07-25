package com.birdidentifier.birdidentifier_findthatbird.api;


import com.birdidentifier.birdidentifier_findthatbird.model.ImageDataResponse;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/* Example of endpoint is action=query&titles=European_goldfinch&prop=pageimages&format=json&pithumbsize=200

 */
public interface WikiImageDataApi {

    @GET("api.php")
    Observable<ImageDataResponse> getWikiImageData(@Query("action") String query, @Query("titles") String bird_name, @Query("prop") String property, @Query("format") String format, @Query("pithumbsize") int imageSize);

}
