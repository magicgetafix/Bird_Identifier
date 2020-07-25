package com.birdidentifier.birdidentifier_findthatbird.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.birdidentifier.birdidentifier_findthatbird.Constants;
import com.birdidentifier.birdidentifier_findthatbird.Tools;

public class Bird implements Parcelable {

    private String englishName = "";
    private String latinName = "";
    private String englishNameUnderscored = "";
    private String latinNameUnderscored = "";
    private int id = 0;
    private int percentageAccuracy = 0;
    private String wikipediaUrl = "";
    private String imageUrl = "";
    private int imageHeight = 0;
    private int imageWidth = 0;

    public Bird(String modelResponseStr, int id, float percentageAccuracy){
        String[] strResponseArray = modelResponseStr.split("\\(");
        if (strResponseArray.length == 2){
            String latinName = strResponseArray[0];
            latinName = latinName.trim();
            this.latinNameUnderscored = latinName.replaceAll("\\s", "_");
            this.latinName = latinName;
            String englishName = strResponseArray[1];
            englishName = englishName.replaceAll("\\)", "");
            englishName = englishName.trim();
            this.englishNameUnderscored = englishName.replaceAll("\\s", "_");
            this.englishName = englishName;
        }

        if (strResponseArray.length == 1){
            String latinName = strResponseArray[0];
            latinName = latinName.trim();
            this.latinNameUnderscored = latinName.replaceAll("\\s", "_");
            this.latinName = latinName;
        }

        if (strResponseArray.length > 2){
            return;
        }
        this.id = id;
        this.percentageAccuracy = (int) (percentageAccuracy * 100);
        if (!this.englishNameUnderscored.isEmpty()){
            this.englishNameUnderscored = Tools.toProperCase(this.englishNameUnderscored);
            this.wikipediaUrl = Constants.WIKIPEDIA_URL + this.englishNameUnderscored;
        }
        else{
            this.wikipediaUrl = Constants.WIKIPEDIA_URL + this.latinNameUnderscored;
        }
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getEnglishName() {
        return englishName;
    }

    public String getLatinName() {
        return latinName;
    }

    public int getId() {
        return id;
    }

    public int getPercentageAccuracy() {
        return this.percentageAccuracy;
    }

    public String getWikipediaUrl() {
        return wikipediaUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getEnglishNameUnderscored() {
        return englishNameUnderscored;
    }

    public String getLatinNameUnderscored() {
        return latinNameUnderscored;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    protected Bird(Parcel in) {
        englishName = in.readString();
        latinName = in.readString();
        englishNameUnderscored = in.readString();
        latinNameUnderscored = in.readString();
        id = in.readInt();
        percentageAccuracy = in.readInt();
        wikipediaUrl = in.readString();
        imageUrl = in.readString();
        imageHeight = in.readInt();
        imageWidth = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(englishName);
        dest.writeString(latinName);
        dest.writeString(englishNameUnderscored);
        dest.writeString(latinNameUnderscored);
        dest.writeInt(id);
        dest.writeInt(percentageAccuracy);
        dest.writeString(wikipediaUrl);
        dest.writeString(imageUrl);
        dest.writeInt(imageHeight);
        dest.writeInt(imageWidth);
    }

    @SuppressWarnings("unused")
    public static final Creator<Bird> CREATOR = new Creator<Bird>() {
        @Override
        public Bird createFromParcel(Parcel in) {
            return new Bird(in);
        }

        @Override
        public Bird[] newArray(int size) {
            return new Bird[size];
        }
    };
}
