package com.example.mynotes.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Notes::class], version = 2)
abstract class NotesDataBase:RoomDatabase() {
    abstract fun getDao():NotesDAO

    companion object{
        @Volatile
        private var INSTANCE:NotesDataBase?=null
        fun getdataBase(context: Context):NotesDataBase{
            if(INSTANCE==null){
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    NotesDataBase::class.java,
                    name = "NotesDB"
                ).build()
            }

            return INSTANCE!!
        }
    }

}