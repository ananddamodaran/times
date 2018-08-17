package com.viginfotech.chennaitimes.adapter

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.viginfotech.chennaitimes.R
import com.viginfotech.chennaitimes.model.Meme

/**
 * Created by anand on 6/22/18.
 */

class MemeAdapter(var context: Activity, var data:MutableList<Meme>) : RecyclerView.Adapter<MemeHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemeHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.meme_item,parent,false)
        return MemeHolder(view)
    }

    override fun onBindViewHolder(holder: MemeHolder, position: Int) {
        Glide.with(context).load(data[position].uploadUri).into(holder.thumbnail)

    }

    override fun getItemCount(): Int {
        return data.size
    }


}
