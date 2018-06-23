package com.viginfotech.chennaitimes.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.*
import com.viginfotech.chennaitimes.R
import com.viginfotech.chennaitimes.adapter.MemeHolder
import com.viginfotech.chennaitimes.model.Meme
import kotlinx.android.synthetic.main.activity_meme.*


class MemeActivity : AppCompatActivity() {
    companion object {
        val TAG = "MemeActivity"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meme)

        setupFirebase()
        setupRecyclerview()
    }

    private fun setupRecyclerview() {


        val mManager= StaggeredGridLayoutManager(3,1)
        mManager.reverseLayout = true
        meme_recycler.layoutManager = mManager
        setupadapter()
    }


    private  lateinit var mDatabase: DatabaseReference


    private  var data = mutableListOf<Meme>()

    private lateinit var memeRef: DatabaseReference

    private  var eventListener: ValueEventListener = object : ValueEventListener {

        override fun onDataChange(dataSnapshot: DataSnapshot) {
            dataSnapshot.children
                    .map { it.child("uploadUri").getValue(String::class.java) }
                    .forEach {
                        Log.d("TAG", it)
                        data.add(Meme(it.toString()))

                    }


        }

        override fun onCancelled(databaseError: DatabaseError) {

        }
    }

    private fun setupFirebase() {
        // Create a storage reference from our app


        mDatabase = FirebaseDatabase.getInstance().getReference()

        memeRef = mDatabase.child("meme")

        memeRef.addListenerForSingleValueEvent(eventListener)


    }


    override fun onStart() {
        super.onStart()
        if (mAdapter != null) {
            mAdapter!!.startListening()
        }
    }

    override fun onStop() {
        super.onStop()
        if (mAdapter != null) {
            mAdapter!!.stopListening()
        }
    }

    private  var mAdapter: FirebaseRecyclerAdapter<Meme, MemeHolder>?=null

    private fun setupadapter() {

        val options = FirebaseRecyclerOptions.Builder<Meme>()
                .setQuery(memeRef, Meme::class.java)
                .build()

        val context=this
         mAdapter = object : FirebaseRecyclerAdapter<Meme, MemeHolder>(options) {
             override fun onBindViewHolder(holder: MemeHolder, position: Int, model: Meme) {
                 holder.bindToPost(context,meme = model)
             }

             override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MemeHolder {
                val inflater = LayoutInflater.from(viewGroup.context)
                return MemeHolder(inflater.inflate(R.layout.meme_item, viewGroup, false))
            }


        }
        meme_recycler.adapter = mAdapter

    }

    override fun onDestroy() {
        super.onDestroy()
        mDatabase.removeEventListener(eventListener)
    }

}
