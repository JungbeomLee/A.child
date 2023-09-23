package com.woojun.ai.util

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.woojun.ai.databinding.SearchItemBinding

class SearchAdapter(private val keyword: ArrayList<String>, private val listener: FragmentInteractionListener): RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val binding = SearchItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return SearchViewHolder(binding).also { handler ->
            binding.apply {
                keywordBox.setOnClickListener {
                    listener.searchAction(keyword[handler.position])
                }
                removeButton.setOnClickListener {
                    listener.removeAction(keyword[handler.position])
                }
            }
        }

    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        if (keyword[position] != "") {
            holder.bind(keyword[position])
        }
    }

    override fun getItemCount(): Int {
        return keyword.size
    }


    class SearchViewHolder(private val binding: SearchItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(keyword: String) {
            binding.apply {
                nameText.text = keyword
            }
        }
    }

}
