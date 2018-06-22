package com.viginfotech.chennaitimes.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.google.firebase.database.*
import com.viginfotech.chennaitimes.R
import com.viginfotech.chennaitimes.adapter.MemeAdapter
import com.viginfotech.chennaitimes.model.Meme
import kotlinx.android.synthetic.main.activity_meme.*


class MemeActivity : AppCompatActivity() {
    companion object {
        val TAG = "MemeActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meme)
        setupRecyclerview()
        setupFirebase()
    }

    private fun setupRecyclerview() {
        meme_recycler.layoutManager = LinearLayoutManager(this)
    }

    private lateinit var mDatabase: DatabaseReference

    private  var data = mutableListOf<Meme>()

    private lateinit var memeRef: DatabaseReference

    private fun setupFirebase() {
        // Create a storage reference from our app


        mDatabase = FirebaseDatabase.getInstance().getReference()
        memeRef = mDatabase.child("meme")

        val eventListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.children
                        .map { it.child("url").getValue(String::class.java) }
                        .forEach {
                            Log.d("TAG", it)
                            data.add(Meme(it.toString()))

                        }


            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
        memeRef.addListenerForSingleValueEvent(eventListener)


    }

    /* override fun onStart() {
         super.onStart()
         attachRecyclerViewAdapter()
     }*/

    /*private fun attachRecyclerViewAdapter() {
        val adapter = newAdapter()

        // Scroll to bottom on new messages
        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                meme_recycler.smoothScrollToPosition(adapter.itemCount)
            }
        })

        meme_recycler.setAdapter(adapter)
    }*/
    /*protected fun newAdapter(): RecyclerView.Adapter<*> {
        val options = FirebaseRecyclerOptions.Builder<Meme>()
                .setQuery(memeRef, Meme::class.java)
                .setLifecycleOwner(this)
                .build()

        return object : FirebaseRecyclerAdapter<Meme, MemeHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemeHolder {
                return MemeHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.meme_item, parent, false))
            }

            protected override fun onBindViewHolder(holder: MemeHolder, position: Int, model: Meme) {
                //holder.bind(model)
            }

            override fun onDataChanged() {
                // If there are no chat messages, show a view that invites the user to add a message.
               // mEmptyListMessage.setVisibility(if (itemCount == 0) View.VISIBLE else View.GONE)
            }
        }
    }*/
    override fun onResume() {
        super.onResume()
        meme_recycler.adapter = MemeAdapter(this, data as ArrayList<Meme>)

    }

}
