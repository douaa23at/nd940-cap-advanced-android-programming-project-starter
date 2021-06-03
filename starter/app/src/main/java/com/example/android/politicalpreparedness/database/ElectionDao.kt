package com.example.android.politicalpreparedness.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.android.politicalpreparedness.network.models.Election

@Dao
interface ElectionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllElections(vararg elections: Election)

    @Query("SELECT * FROM election_table")
    fun getAllElections(): LiveData<List<Election>>

    @Query("SELECT * FROM election_table")
    fun getSavedElections(): LiveData<List<Election>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertElection(election: Election)

    @Query("SELECT * FROM election_table WHERE id = :electionId ")
    fun getElection(electionId: Int): LiveData<Election>

    @Query("DELETE FROM election_table WHERE  id = :electionId")
    fun deleteElection(electionId: Int)

    @Query("DELETE FROM election_table")
    fun clearElections()

}
