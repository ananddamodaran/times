package com.viginfotech.chennaitimes.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import com.viginfotech.chennaitimes.R

/**
 * Created by anand on 6/22/18.
 */
class MemeHolder(view: View) : RecyclerView.ViewHolder(view) {
    val thumbnail: ImageView = view.findViewById<ImageView>(R.id.meme_thumbnail)
}
