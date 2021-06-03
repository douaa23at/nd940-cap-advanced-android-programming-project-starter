package com.example.android.politicalpreparedness.election.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.databinding.ViewholderElectionBinding
import com.example.android.politicalpreparedness.election.ElectionsViewModel
import com.example.android.politicalpreparedness.network.models.Election

class ElectionListAdapter(
        val electionsViewModel: ElectionsViewModel
) : ListAdapter<Election, ElectionListAdapter.ElectionViewHolder>(ElectionDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElectionViewHolder {
        return ElectionViewHolder(
                ViewholderElectionBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                )
        )
    }

    override fun onBindViewHolder(holder: ElectionViewHolder, position: Int) {
        holder.bind(getItem(position), electionsViewModel)
    }

    //TODO: Add companion object to inflate ViewHolder (from)

    class ElectionViewHolder(private var binding: ViewholderElectionBinding) :
            RecyclerView.ViewHolder(binding.root) {

        fun bind(election: Election, electionsViewModel: ElectionsViewModel) {
            binding.election = election
            binding.viewModel = electionsViewModel
            binding.executePendingBindings()
        }
    }

    companion object ElectionDiffCallback : DiffUtil.ItemCallback<Election>() {
        override fun areItemsTheSame(oldItem: Election, newItem: Election): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Election, newItem: Election): Boolean {
            return oldItem.id == newItem.id
        }
    }
}


//TODO: Create ElectionListener