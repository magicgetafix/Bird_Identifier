
package com.birdidentifier.birdidentifier_findthatbird.model;

import com.google.firebase.encoders.annotations.Encodable;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

public class ImageDataResponse {

    @SerializedName("query")
    @Expose
    private Query query;

    public Query getQuery() {
        return query;
    }

    public void setQuery(Query query) {
        this.query = query;
    }

    public static class Query {

        @SerializedName("pages")
        @Expose
        private Map<Object, Image> pages;

        public Map<Object, Image> getPages() {
            return pages;
        }

        public void setPages(Map<Object, Image> pages) {
            this.pages = pages;
        }

        public static class Image {

                @SerializedName("thumbnail")
                @Expose
                private Thumbnail thumbnail;

                public Thumbnail getThumbnail() {
                    return thumbnail;
                }

                public void setThumbnail(Thumbnail thumbnail) {
                    this.thumbnail = thumbnail;
                }

                public static class Thumbnail {

                    @SerializedName("source")
                    @Expose
                    private String source;
                    @SerializedName("width")
                    @Expose
                    private Integer width;
                    @SerializedName("height")
                    @Expose
                    private Integer height;

                    public String getSource() {
                        return source;
                    }

                    public void setSource(String source) {
                        this.source = source;
                    }

                    public Integer getWidth() {
                        return width;
                    }

                    public void setWidth(Integer width) {
                        this.width = width;
                    }

                    public Integer getHeight() {
                        return height;
                    }

                    public void setHeight(Integer height) {
                        this.height = height;
                    }
                }

        }
    }

}
