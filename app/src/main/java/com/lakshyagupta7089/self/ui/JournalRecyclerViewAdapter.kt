package com.lakshyagupta7089.self.ui

import android.content.Context
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lakshyagupta7089.self.R
import com.lakshyagupta7089.self.databinding.JournalRowBinding
import com.lakshyagupta7089.self.model.Journal
import com.squareup.picasso.Picasso

class JournalRecyclerViewAdapter(
    private val context: Context,
    private val journalList: ArrayList<Journal>
) : RecyclerView.Adapter<JournalRecyclerViewAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = JournalRowBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false
        )

        return ViewHolder(
            binding,
            context
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val journal = journalList[position]
        val imageUrl = journal.imageUrl

        holder.userId = journal.userId
        holder.binding.journalRowUserName.text = journal.userName

        holder.binding.journalTitleList.text = journal.title
        holder.binding.journalThoughtsList.text = journal.thoughts


        Picasso.get()
            .load(imageUrl)
            .placeholder(R.drawable.image_three)
            .fit()
            .into(holder.binding.journalImageList)


        //1 hour ago...
        //Source: https://medium.com/@shaktisinh/time-a-go-in-android-8bad8b171f87
        val milliSeconds = journal.timeAdded!!.seconds * 1000
        val timeAgo = DateUtils.getRelativeTimeSpanString(milliSeconds) as String

        holder.binding.journalTimeStamp.text = timeAgo
    }

    override fun getItemCount(): Int = journalList.size

    inner class ViewHolder(val binding: JournalRowBinding, private val content: Context) :
        RecyclerView.ViewHolder(binding.root) {
        var userName: String? = null
        var userId: String? = null

        init {
            binding.journalRowShareButton.setOnClickListener {
                // context.startActivity()
            }
        }
    }
}