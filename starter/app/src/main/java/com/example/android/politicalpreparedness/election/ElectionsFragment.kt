package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter

class ElectionsFragment : Fragment() {

    //TODO: Declare ViewModel
    private val electionsViewModel by lazy {
        ViewModelProvider(this, ElectionsViewModelFactory(requireActivity().application)).get(
                ElectionsViewModel::class.java
        )
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = FragmentElectionBinding.inflate(inflater)
        binding.lifecycleOwner = this
        electionsViewModel.initialized()
        binding.electionRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        val electionsAdapter = ElectionListAdapter(electionsViewModel)
        binding.electionRecyclerView.adapter = electionsAdapter
        electionsViewModel.elections.observe(viewLifecycleOwner, Observer { list ->
            electionsAdapter.submitList(list)
        })
        binding.savedelectionRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        val savedElections = ElectionListAdapter(electionsViewModel)
        binding.savedelectionRecyclerView.adapter = savedElections
        electionsViewModel.savedElections.observe(viewLifecycleOwner, Observer { list ->
            savedElections.submitList(list)
        })
        electionsViewModel.navigateToDetailFragment.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { election ->
                findNavController().navigate(ElectionsFragmentDirections.navigateToVoterInfoFragment(election.id, election.division))
            }
        })
        return binding.root
    }


}