package com.example.mynotes.database

import android.provider.ContactsContract.CommonDataKinds.Note
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface NotesDAO {

    @Insert
    suspend fun insertNotes(notes: Notes)

    @Delete
    suspend fun deleteNotes(notes:Notes)

    @Query("SELECT * FROM notes")
    fun getallNotes():LiveData<List<Notes>>

    @Query("UPDATE notes SET title =:Title, description =:NotesDes,color =:Color WHERE id =:id")
    suspend fun updateNotes(id:Int,Title:String,NotesDes:String,Color:Int)

}