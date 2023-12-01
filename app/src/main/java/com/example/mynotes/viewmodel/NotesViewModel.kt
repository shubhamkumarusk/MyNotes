package com.example.mynotes.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.mynotes.database.Notes
import com.example.mynotes.database.NotesDAO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotesViewModel(val dao:NotesDAO):ViewModel() {
    val allnotes:LiveData<List<Notes>> = dao.getallNotes().asFlow().asLiveData()

    fun update(notes:Notes) = viewModelScope.launch(Dispatchers.IO){dao.updateNotes(notes.id!!,notes.title,notes.description,notes.color!!)}
    fun insert(notes:Notes) = viewModelScope.launch(Dispatchers.IO){dao.insertNotes(notes)}
    fun delete(notes:Notes) = viewModelScope.launch(Dispatchers.IO){dao.deleteNotes(notes)}


}

class NotesViewModelFactory(private val dao:NotesDAO):ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NotesViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}