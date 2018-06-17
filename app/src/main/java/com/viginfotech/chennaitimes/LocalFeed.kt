package com.viginfotech.chennaitimes

import android.os.Parcel
import android.os.Parcelable


/**
 * Created by anand on 12/25/15.
 */
class LocalFeed : Parcelable {
    var title: String? = null
    var detailedTitle: String? = null
    var summary: String? = null
    private var pubDate: Long = 0
    var guid: String? = null
    var thumbnail: String? = null
    var image: String? = null
    var detailNews: String? = null
    var categoryId: Int = 0
    var sourceId: Int = 0
    var readState: Int = 0

    constructor() {

    }

    protected constructor(`in`: Parcel) {
        title = `in`.readString()
        detailedTitle = `in`.readString()
        summary = `in`.readString()
        pubDate = `in`.readLong()
        guid = `in`.readString()
        thumbnail = `in`.readString()
        image = `in`.readString()
        detailNews = `in`.readString()
        categoryId = `in`.readInt()
        sourceId = `in`.readInt()
        readState = `in`.readInt()

    }

    fun getPubDate(): Long? {
        return pubDate
    }

    fun setPubDate(pubDate: Long?) {
        this.pubDate = pubDate!!
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(title)
        dest.writeString(detailedTitle)
        dest.writeString(summary)
        dest.writeLong(pubDate)
        dest.writeString(guid)
        dest.writeString(thumbnail)
        dest.writeString(image)
        dest.writeString(detailNews)
        dest.writeInt(categoryId)
        dest.writeInt(sourceId)
        dest.writeInt(readState)
    }

    override fun equals(obj: Any?): Boolean {
        if (obj == null) return false
        if (obj !is LocalFeed) return false
        val other = obj as LocalFeed?
        return other!!.guid == this.guid

    }

    override fun hashCode(): Int {
        return this.guid!!.hashCode()
    }

    companion object {
        val CREATOR: Parcelable.Creator<LocalFeed> = object : Parcelable.Creator<LocalFeed> {
            override fun createFromParcel(`in`: Parcel): LocalFeed {
                return LocalFeed(`in`)
            }

            override fun newArray(size: Int): Array<LocalFeed?> {
                return arrayOfNulls(size)
            }
        }
        private val TAG = LocalFeed::class.java.simpleName
        val CATEGORY_ID = "categoryId"
        val PUBLISHED_DATE = "pubDate"
    }
}
