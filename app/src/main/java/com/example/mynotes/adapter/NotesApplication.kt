package com.example.mynotes.adapter

import android.app.Application
import com.example.mynotes.database.NotesDataBase

class NotesApplication:Application() {
    val database:NotesDataBase by lazy { NotesDataBase.getdataBase(this) }
}