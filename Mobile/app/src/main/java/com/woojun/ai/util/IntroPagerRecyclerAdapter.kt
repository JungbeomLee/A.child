package com.woojun.ai.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.woojun.ai.R

class IntroPagerRecyclerAdapter(private val pagerList: ArrayList<PagerItem>): RecyclerView.Adapter<IntroPagerRecyclerAdapter.PagerViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder {
        return PagerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.onboard_item, parent, false))
    }

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        holder.bindingView(pagerList[position])
    }

    override fun getItemCount(): Int {
        return pagerList.size
    }

    class PagerViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        private val itemImage = itemView.findViewById<ImageView>(R.id.pager_item_image)
        private val itemTitle = itemView.findViewById<TextView>(R.id.title_text)
        private val itemSubject = itemView.findViewById<TextView>(R.id.subject_text)

        fun bindingView(pagerItem: PagerItem) {
            itemImage.setImageResource(pagerItem.image)
            itemTitle.text = pagerItem.title
            itemSubject.text = pagerItem.subject
        }
    }
}