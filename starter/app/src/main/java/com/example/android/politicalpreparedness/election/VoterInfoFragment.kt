package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase.Companion.getInstance
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import kotlinx.android.synthetic.main.fragment_voter_info.view.*

class VoterInfoFragment : Fragment() {


    private val voterInfoViewModel by lazy {
        val database = getInstance(requireActivity().application)
        ViewModelProvider(this, VoterInfoViewModelFactory(database.electionDao)).get(
                VoterInfoViewModel::class.java
        )
    }

    private val args: VoterInfoFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding = FragmentVoterInfoBinding.inflate(inflater)
        binding.lifecycleOwner = this
        voterInfoViewModel.initialize(args.argDivision, args.argElectionId)
        voterInfoViewModel.voterInfoResponse.observe(viewLifecycleOwner, Observer { voterInfoResponse ->
            if (voterInfoResponse.election.name.isEmpty()) {
                binding.electionName.visibility = View.INVISIBLE
            } else {
                binding.electionName.visibility = View.VISIBLE
            }

            if (voterInfoResponse.pollingLocations?.get(0)?.address?.toFormattedString().isNullOrEmpty()) {
                binding.stateLocations.visibility = View.INVISIBLE
            } else {
                binding.stateLocations.visibility = View.VISIBLE
            }

            if (voterInfoResponse.state?.get(0)?.electionAdministrationBody?.electionInfoUrl.isNullOrEmpty()) {
                binding.stateBallot.visibility = View.INVISIBLE
            } else {
                binding.stateBallot.visibility = View.VISIBLE
            }

            if (voterInfoResponse.state?.get(0)?.electionAdministrationBody?.correspondenceAddress?.toFormattedString().isNullOrEmpty()) {
                binding.address.visibility = View.INVISIBLE
            } else {
                binding.address.visibility = View.VISIBLE
            }

        })
        voterInfoViewModel.savedElections.observe(viewLifecycleOwner, Observer { savedElections ->
            val election = savedElections.find {
                it.id == args.argElectionId
            }
            val follow: Boolean = election == null
            voterInfoViewModel.toFollow.value = follow
        })

        voterInfoViewModel.openLink.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { link ->
                findNavController().navigate(VoterInfoFragmentDirections.actionVoterInfoFragmentToMyCustomWebViewFragment(link))
            }
        })


        voterInfoViewModel.toFollow.observe(viewLifecycleOwner, Observer { tofollow ->
            binding.follow.text = if (tofollow) {
                getString(R.string.follow)
            } else {
                getString(R.string.unfollow)
            }

        })

        voterInfoViewModel.finish.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                findNavController().popBackStack()
            }
        })

        binding.viewModel = voterInfoViewModel
        return binding.root


        //TODO: Populate voter info -- hide views without provided data.
        /**
        Hint: You will need to ensure proper data is provided from previous fragment.
         */


        //TODO: Handle loading of URLs

        //TODO: Handle save button UI state
        //TODO: cont'd Handle save button clicks

    }


    //TODO: Create method to load URL intents

}
