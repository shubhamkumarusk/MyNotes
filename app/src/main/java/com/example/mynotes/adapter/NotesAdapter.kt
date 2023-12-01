package com.example.mynotes.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mynotes.R
import com.example.mynotes.database.Notes
import com.example.mynotes.databinding.NotesListBinding
import kotlin.random.Random

class NotesAdapter(private val onNotesClicked: (Notes) -> Unit):ListAdapter<Notes,NotesAdapter.NotesViewHolder>(DiffCallback) {

    private val originalList: MutableList<Notes> = mutableListOf()

    fun setOriginalList(list: List<Notes>) {
        originalList.clear()
        originalList.addAll(list)
    }


    class NotesViewHolder(private val binding:NotesListBinding):RecyclerView.ViewHolder(binding.root){

        fun bind(notes:Notes){
            binding.title.text = notes.title
            binding.description.text = notes.description
            binding.date.text = notes.date
            binding.notesCard.setCardBackgroundColor(itemView.resources.getColor(notes.color!!))
        }

//        fun randomColor():Int{
//            val list = ArrayList<Int>()
//            list.add(R.color.Color1)
//            list.add(R.color.Color2)
//            list.add(R.color.Color3)
//            list.add(R.color.Color4)
//            list.add(R.color.Color5)
//            list.add(R.color.Color6)
//            list.add(R.color.Color7)
//
//            val randomIndex = Random.nextInt(list.size)
//            return list[randomIndex]
//
//
//        }


    }

    fun setColor(color:Int):Int{
        return color
    }

    companion object{
        private val DiffCallback = object:DiffUtil.ItemCallback<Notes>(){

            override fun areItemsTheSame(oldItem: Notes, newItem: Notes): Boolean {
                return oldItem.id == newItem.id
            }


            override fun areContentsTheSame(oldItem: Notes, newItem: Notes): Boolean {

                return oldItem.description == newItem.description &&  oldItem.title ==newItem.description
            }


        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        val viewHolder = NotesViewHolder(
            NotesListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )

        return viewHolder
    }


    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        val curr = getItem(position)
        holder.itemView.setOnClickListener{
            onNotesClicked(curr)
        }
        holder.bind(curr)
    }

    fun getItemAtPosition(position: Int): Notes {
        return getItem(position)
    }


    fun filter(query: String?) {
        val filteredList = mutableListOf<Notes>()

        if (query.isNullOrBlank()) {
            filteredList.addAll(originalList)
        } else {
            val searchQuery = query.toLowerCase().trim()
            for (item in originalList) {
                if (item.title.toLowerCase().contains(searchQuery) ||
                    item.description.toLowerCase().contains(searchQuery)
                ) {
                    filteredList.add(item)
                }
            }
        }

        submitList(filteredList)
    }


    fun sameColorNotes(color:Int?){
        val sameColorNoteList = mutableListOf<Notes>()
        for(item in originalList){
            if(item.color==color){
                sameColorNoteList.add(item)
            }
        }

        submitList(sameColorNoteList)
    }
    fun clearFilter(){
        submitList(originalList)
    }

    fun isColorFilterApplied(colorFilter: Int, mImageView: Int?): Boolean {
        return colorFilter==mImageView
    }

}