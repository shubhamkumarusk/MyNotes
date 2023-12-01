package com.example.mynotes.database

import android.icu.text.CaseMap.Title
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "notes")
@Parcelize
data class Notes(
    @PrimaryKey(autoGenerate = true) val id:Int?,
    val title:String,
    val description:String,
    val date:String,
   @ColumnInfo(defaultValue = "0") var color:Int?
) : Parcelable {

    fun updateColor(newColor: Int) {
        color = newColor
    }
}