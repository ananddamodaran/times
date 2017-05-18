package com.viginfotech.chennaitimes;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by anand on 12/25/15.
 */
public class LocalFeed  implements Parcelable {
    public static final Creator<LocalFeed> CREATOR = new Creator<LocalFeed>() {
        @Override
        public LocalFeed createFromParcel(Parcel in) {
            return new LocalFeed(in);
        }

        @Override
        public LocalFeed[] newArray(int size) {
            return new LocalFeed[size];
        }
    };
    private static final String TAG = LocalFeed.class.getSimpleName();
    public static final String CATEGORY_ID = "categoryId";
    public static final String PUBLISHED_DATE = "pubDate";
    private String title;
    private String detailedTitle;
    private String summary;
    private long pubDate;
    private String guid;
    private String thumbnail;
    private String image;
    private String detailNews;
    private int categoryId;
    private int sourceId;
    private int readState;

    public LocalFeed() {

    }

    protected LocalFeed(Parcel in) {
        title = in.readString();
        detailedTitle = in.readString();
        summary = in.readString();
        pubDate = in.readLong();
        guid = in.readString();
        thumbnail = in.readString();
        image = in.readString();
        detailNews = in.readString();
        categoryId = in.readInt();
        sourceId = in.readInt();
        readState = in.readInt();

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetailedTitle() {
        return detailedTitle;
    }

    public void setDetailedTitle(String detailedTitle) {
        this.detailedTitle = detailedTitle;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Long getPubDate() {
        return pubDate;
    }

    public void setPubDate(Long pubDate) {
        this.pubDate = pubDate;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDetailNews() {
        return detailNews;
    }

    public void setDetailNews(String detailNews) {
        this.detailNews = detailNews;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getSourceId() {
        return sourceId;
    }

    public void setSourceId(int sourceId) {
        this.sourceId = sourceId;
    }

    public int getReadState() {
        return readState;
    }

    public void setReadState(int readState) {
        this.readState = readState;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(detailedTitle);
        dest.writeString(summary);
        dest.writeLong(pubDate);
        dest.writeString(guid);
        dest.writeString(thumbnail);
        dest.writeString(image);
        dest.writeString(detailNews);
        dest.writeInt(categoryId);
        dest.writeInt(sourceId);
        dest.writeInt(readState);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null)                return false;
        if(!(obj instanceof LocalFeed)) return false;
        LocalFeed other=(LocalFeed)obj;
       return other.guid.equals(this.guid);

    }

    @Override
    public int hashCode() {
        return  this.guid.hashCode();
    }
}
