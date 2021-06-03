package com.example.android.politicalpreparedness.election.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ElectionRepository(private val electionDao: ElectionDao) {
    var elections = MutableLiveData<List<Election>>()
    val savedElections: LiveData<List<Election>> = electionDao.getSavedElections()
    var voterInfoResponse = MutableLiveData<VoterInfoResponse>()

    suspend fun refreshElections() {
        withContext(Dispatchers.IO) {
            try {
                elections.postValue(CivicsApi.retrofitService.getElections().await().elections)
            } catch (e: Exception) {
                Log.i("exception", "${e.message}")
            }
        }
    }

    suspend fun getVoterInfoResponse(address: String, electionId: Int) {
        withContext(Dispatchers.IO) {
            try {
                voterInfoResponse.postValue(CivicsApi.retrofitService.getVoterInfo(address, electionId).await())
            } catch (e: Exception) {
                Log.i("exception : voterInfoResponse ", "${e.message}")
            }
        }
    }

    suspend fun saveElection(election: Election) {
        withContext(Dispatchers.IO) {
            try {
                electionDao.insertElection(election)
            } catch (e: Exception) {
                Log.i("exception saving : election ", "${e.message}")
            }
        }
    }

    suspend fun deleteElection(election: Election) {
        withContext(Dispatchers.IO) {
            try {
                electionDao.deleteElection(election.id)
            } catch (e: Exception) {
                Log.i("exception saving : election ", "${e.message}")
            }
        }
    }
}
