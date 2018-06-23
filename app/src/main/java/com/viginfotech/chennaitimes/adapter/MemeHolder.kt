package com.viginfotech.chennaitimes.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.viginfotech.chennaitimes.R
import com.viginfotech.chennaitimes.model.Meme

/**
 * Created by anand on 6/22/18.
 */
class MemeHolder(view: View) : RecyclerView.ViewHolder(view) {
    fun bindToPost(context: Context, meme: Meme) {
        Glide.with(context).load(meme.uploadUri).into(thumbnail)

    }

    val thumbnail: ImageView = view.findViewById<ImageView>(R.id.meme_thumbnail)
}
