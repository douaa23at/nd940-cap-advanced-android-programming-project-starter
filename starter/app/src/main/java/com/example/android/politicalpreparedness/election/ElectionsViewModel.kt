package com.example.android.politicalpreparedness.election

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDatabase.Companion.getInstance
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.election.repository.ElectionRepository
import com.example.android.politicalpreparedness.utils.Event
import kotlinx.coroutines.launch

//TODO: Construct ViewModel and provide election datasource
class ElectionsViewModel(application: Application) : ViewModel() {

    private val database = getInstance(application)
    private val repository = ElectionRepository(database.electionDao)
    val elections = repository.elections
    val savedElections = repository.savedElections
    val navigateToDetailFragment = MutableLiveData<Event<Election>>()

    fun initialized() {
        viewModelScope.launch {
            repository.refreshElections()
        }
    }

    fun navigateToDetailFragment(election: Election) {
        navigateToDetailFragment.value = Event(election)
    }

    //TODO: Create live data val for upcoming elections

    //TODO: Create live data val for saved elections

    //TODO: Create val and functions to populate live data for upcoming elections from the API and saved elections from local database

    //TODO: Create functions to navigate to saved or upcoming election voter info

}