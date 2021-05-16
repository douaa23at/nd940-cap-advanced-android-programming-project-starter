package com.example.android.politicalpreparedness.election.adapter

import android.transition.TransitionInflater.from
import android.view.LayoutInflater.from
import android.view.View

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.databinding.ViewholderElectionBinding
import com.example.android.politicalpreparedness.election.ElectionsViewModel
import com.example.android.politicalpreparedness.network.models.Election

class ElectionListAdapter(private val clickListener: ElectionListener): ListAdapter<Election, ElectionViewHolder>(ElectionDiffCallback) {

   var items = emptyList<Election>()
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
       holder.bind(items[position])
    }

    override fun getItemCount() = items.count()
    //TODO: Bind ViewHolder

    //TODO: Add companion object to inflate ViewHolder (from)

companion object

}

class ElectionViewHolder(private var binding: ViewholderElectionBinding) :
        RecyclerView.ViewHolder(binding.root) {

    fun bind(asteroid: Election) {
      //  binding.mainViewModel = mainViewModel
        //binding.asteroid = asteroid
        binding.executePendingBindings()
    }
}
object ElectionDiffCallback : DiffUtil.ItemCallback<Election>() {
    override fun areItemsTheSame(oldItem: Election, newItem: Election): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Election, newItem: Election): Boolean {
        return oldItem.id == newItem.id
    }
}

object ElectionListener : View.OnClickListener{
    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }

}

//TODO: Create ElectionViewHolder

//TODO: Create ElectionDiffCallback

//TODO: Create ElectionListener