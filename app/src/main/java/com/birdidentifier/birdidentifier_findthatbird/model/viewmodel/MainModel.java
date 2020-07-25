package com.birdidentifier.birdidentifier_findthatbird.model.viewmodel;

import android.app.Application;
import android.os.Handler;

import com.birdidentifier.birdidentifier_findthatbird.api.WikiImageDataApi;
import com.birdidentifier.birdidentifier_findthatbird.model.Bird;
import com.birdidentifier.birdidentifier_findthatbird.model.ImageDataResponse;


import java.util.ArrayList;
import java.util.Collection;

import java.util.List;
import java.util.Map;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import okhttp3.OkHttpClient;

import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public class MainModel extends ViewModel {

    public static List<Bird> birdList = new ArrayList<>();
    public static String imageFileName = "";

    public MainModel(){

    }

    public LiveData<Bird> getWikipediaImageUrls(final Bird newBird, final int imageSize, boolean useCommonsWikipedia){

        MutableLiveData<Bird> birdMutableLiveData = new MutableLiveData<>();
        WikiImageDataApi wikiImageDataApi = getCommonsWikimediaRetrofit().create(WikiImageDataApi.class);
                    Observable<ImageDataResponse> imageDataResponse;
        if (useCommonsWikipedia) {
            wikiImageDataApi = getCommonsWikimediaRetrofit().create(WikiImageDataApi.class);
            imageDataResponse = wikiImageDataApi.getWikiImageData("query", newBird.getLatinNameUnderscored(), "pageimages", "json", imageSize);
        }
        else{
            wikiImageDataApi = getEnglishWikimediaRetrofit().create(WikiImageDataApi.class);
            imageDataResponse = wikiImageDataApi.getWikiImageData("query", newBird.getEnglishNameUnderscored(), "pageimages", "json", imageSize);
        }


        imageDataResponse.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<ImageDataResponse>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(ImageDataResponse imageDataResponse) {

                            if (imageDataResponse != null){
                                ImageDataResponse.Query query = imageDataResponse.getQuery();
                                if (query!=null){

                                    Map<Object, ImageDataResponse.Query.Image> imageHashMap = query.getPages();
                                    if (imageHashMap != null){
                                        Collection<ImageDataResponse.Query.Image> values = imageHashMap.values();
                                        if (!values.isEmpty()){
                                            ImageDataResponse.Query.Image[] images = values.toArray(new ImageDataResponse.Query.Image[values.size()]);
                                            ImageDataResponse.Query.Image image = images[0];
                                            if (image != null){
                                                ImageDataResponse.Query.Image.Thumbnail thumbnail = image.getThumbnail();
                                                if (thumbnail!=null){
                                                    String sourceImageUrl = thumbnail.getSource();
                                                    int height = thumbnail.getHeight();
                                                    int width = thumbnail.getWidth();
                                                    if (sourceImageUrl!=null){
                                                        newBird.setImageUrl(sourceImageUrl);
                                                        birdMutableLiveData.setValue(newBird);
                                                    }
                                                    else{
                                                        if (useCommonsWikipedia) {
                                                            getWikipediaImageUrls(newBird, imageSize, false);
                                                        }
                                                    }
                                                }

                                                else{
                                                    if (useCommonsWikipedia) {
                                                        getWikipediaImageUrls(newBird, imageSize, false);
                                                    }
                                                }
                                            }

                                            else{
                                                if (useCommonsWikipedia) {
                                                    getWikipediaImageUrls(newBird, imageSize, false);
                                                }
                                            }
                                        }

                                        else {
                                            if (useCommonsWikipedia) {
                                                getWikipediaImageUrls(newBird, imageSize, false);
                                            }
                                        }

                                    }

                                }
                                else {
                                    if (useCommonsWikipedia) {
                                        getWikipediaImageUrls(newBird, imageSize, false);
                                    }
                                }
                            }

                        }

                        @Override
                        public void onError(Throwable e) {
                            Timber.e(e);

                        }

                        @Override
                        public void onComplete() {

                        }
                });

        return birdMutableLiveData;

    }


    private Retrofit getCommonsWikimediaRetrofit(){

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        return new Retrofit.Builder()
                .baseUrl("https://commons.wikimedia.org/w/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build();
    }

    private Retrofit getEnglishWikimediaRetrofit(){

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        return new Retrofit.Builder()
                .baseUrl("https://en.wikipedia.org/w/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build();
    }
}
