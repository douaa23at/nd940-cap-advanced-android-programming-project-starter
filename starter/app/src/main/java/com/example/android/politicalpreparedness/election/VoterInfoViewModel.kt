package com.example.android.politicalpreparedness.election

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.election.repository.ElectionRepository
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.utils.Event
import kotlinx.coroutines.launch

class VoterInfoViewModel(dataSource: ElectionDao) : ViewModel() {

    private val repository = ElectionRepository(dataSource)
    val voterInfoResponse = repository.voterInfoResponse
    val openLink = MutableLiveData<Event<String>>()
    val toFollow = MutableLiveData<Event<Boolean>>()
    val finish = MutableLiveData<Event<Unit>>()
    fun initialize(division: Division, electionId: Int) {
        viewModelScope.launch {
            repository.getVoterInfoResponse("1263%20Pacific%20Ave.%20Kansas%20City%20KS", electionId)
            val election = repository.savedElections.value?.find {
                it.id == electionId
            }
            toFollow.value = Event(election == null)
        }
    }

    /**
     * Hint: The saved state can be accomplished in multiple ways. It is directly related to how elections are saved/removed from the database.
     */
    fun handleElection(election: Election) {
        if (toFollow.value == Event(true)) {
            viewModelScope.launch {
                repository.saveElection(election)
            }
        } else {
            viewModelScope.launch {
                repository.deleteElection(election)
            }
        }
        finish.value = Event(Unit)
    }

    fun openLink() {
        voterInfoResponse.value?.state?.get(0)?.electionAdministrationBody?.electionInfoUrl?.let {
            openLink.value = Event(it)
        }

    }

}