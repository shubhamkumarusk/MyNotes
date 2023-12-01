package com.example.mynotes.Fragment

import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View

import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.mynotes.R
import com.example.mynotes.adapter.NotesAdapter
import com.example.mynotes.adapter.NotesApplication
import com.example.mynotes.database.Notes
import com.example.mynotes.database.NotesDataBase
import com.example.mynotes.databinding.FragmentNotesFrontBinding
import com.example.mynotes.swip.SwipToDelete
import com.example.mynotes.viewmodel.NotesViewModel
import com.example.mynotes.viewmodel.NotesViewModelFactory


class NotesFront : Fragment() {
    private lateinit var binding:FragmentNotesFrontBinding
    private lateinit var notes:Notes
    private lateinit var ListofAllNotes:LiveData<List<Notes>>
    private  var mCurrentColor: Int?=null
    private val viewModel:NotesViewModel by activityViewModels{
        NotesViewModelFactory(
            (activity?.application as NotesApplication).database.getDao()
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentNotesFrontBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // OnClickNotes
        val adapter = NotesAdapter({
            val action = NotesFrontDirections.actionNotesFrontToNotesEdit(
                it
            )
            findNavController().navigate(action)
        })


        // To keep the real icon on Iteam of drawer Layout
//        binding.drawerNavigation.setItemIconTintList(null)

        //Observer
        viewModel.allnotes.observe(this.viewLifecycleOwner){
            it.let {
                adapter.setOriginalList(it)
                adapter.submitList(it)
            }
        }

        binding.recyclerView.layoutManager = GridLayoutManager(this.context,2)
        binding.recyclerView.adapter = adapter
        val swipHandler = object :SwipToDelete(){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                notes = adapter.getItemAtPosition(position)

                viewModel.delete(notes)
            }
        }
        // Scroll to the last note (latest) when the RecyclerView is loaded
        binding.recyclerView.postDelayed(Runnable {
            val lastItemPosition = adapter.itemCount - 1
            binding.recyclerView.scrollToPosition(lastItemPosition)
        },0)


        // Swip to delete
        val itemTouchHelper = ItemTouchHelper(swipHandler)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
        binding.addNotesActionbtn.setOnClickListener{
            val action = NotesFrontDirections.actionNotesFrontToNotesEdit(Notes(null,"","","",0))
            findNavController().navigate(action)
        }

        //Searching through Search bar
        binding.searchView.setOnQueryTextListener(

            object :SearchView.OnQueryTextListener,
                androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(p0: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(p0: String?): Boolean {
                    if(p0!=null){

                        adapter.filter(p0)
                    }

                    return true
                }

            }
        )

        // For Drawer navigation
        val drawer = requireView().findViewById<DrawerLayout>(R.id.drawer_layout)
        binding.searchView.setOnClickListener{
            drawer.openDrawer(GravityCompat.START)
        }

        binding.redBtn.setOnClickListener{


        }

        val colorButtonCLicker = View.OnClickListener {
            val colorFilter: Int? = when (it.id) {
                R.id.red_btn -> R.color.red
                R.id.green_btn -> R.color.green
                R.id.blue_btn -> R.color.blue
                R.id.skin_btn -> R.color.skin
                R.id.teal_btn -> R.color.teal
                R.id.yellow_btn -> R.color.yellow
                R.id.purple_btn -> R.color.purple
                R.id.grey_btn -> R.color.grey
                else -> null
            }

            if (colorFilter != null) {
                if (adapter.isColorFilterApplied(colorFilter,mCurrentColor)) {
                    // If the filter is already applied, clear the filter
                    adapter.clearFilter()
                    mCurrentColor = null

                } else {
                    // Apply the color filter
                    mCurrentColor = colorFilter
                    adapter.sameColorNotes(colorFilter)
                }
            }
        }

        binding.redBtn.setOnClickListener(colorButtonCLicker)
        binding.greenBtn.setOnClickListener(colorButtonCLicker)
        binding.blueBtn.setOnClickListener(colorButtonCLicker)
        binding.skinBtn.setOnClickListener(colorButtonCLicker)
        binding.tealBtn.setOnClickListener(colorButtonCLicker)
        binding.yellowBtn.setOnClickListener(colorButtonCLicker)
        binding.purpleBtn.setOnClickListener(colorButtonCLicker)
        binding.greyBtn.setOnClickListener(colorButtonCLicker)
        


    }



}