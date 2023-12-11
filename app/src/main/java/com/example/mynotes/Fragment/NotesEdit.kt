package com.example.mynotes.Fragment


import android.content.Intent
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.PopupMenu
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavArgs
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.mynotes.R
import com.example.mynotes.adapter.NotesApplication
import com.example.mynotes.database.Notes
import com.example.mynotes.database.NotesDAO
import com.example.mynotes.database.NotesDataBase
import com.example.mynotes.databinding.FragmentNotesEditBinding
import com.example.mynotes.viewmodel.NotesViewModel
import com.example.mynotes.viewmodel.NotesViewModelFactory
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.Date
import java.util.Locale


class NotesEdit : Fragment() {
    private lateinit var binding:FragmentNotesEditBinding
    private val REQUEST_AUDIO_PERMISSION = 123
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var speechIntent : Intent

    private val arguments:NotesFrontArgs by navArgs()
    private var update = false
    private lateinit var notes:Notes
    private var color:Int = R.color.Color1
    private  var id:Int?=null
    private lateinit var date: String
    private val viewModel: NotesViewModel by activityViewModels{
        NotesViewModelFactory(
            (activity?.application as NotesApplication).database.getDao()
        )
    }

    private val expandAnim by lazy { AnimationUtils.loadAnimation(requireContext(), R.anim.expand_horizontal)}
    private var isListening = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        this.binding = FragmentNotesEditBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireContext())
        speechIntent = Intent (RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())

        speechRecognizer.setRecognitionListener(object: RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onError(error: Int) {}
            override fun onResults(results: Bundle?) {
                results?.let{result->
                    val data = result.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    Log.w("notes-result", "reached data $data ${binding.descriptionEdit.isFocused} ${binding.titleEdit.isFocused}")

                    if(binding.titleEdit.isFocused){
                        var updatedText = binding.titleEdit.text.toString()
                        updatedText.trim()
                        updatedText+=" ${data.toString().substring(1, data.toString().length-1)}"
                        Log.w("notes-result", "$updatedText")
                        binding.titleEdit.setText(updatedText)
                    }
                    else{
                        var updatedText = binding.descriptionEdit.text.toString()
                        updatedText.trim()
                        updatedText+=" ${data.toString().substring(1, data.toString().length-1)}"
                        binding.descriptionEdit.setText(updatedText)
                    }
                }
            }
        })

        if(arguments.notes?.id!=null){
            update = true
        }
        if(update){
            binding.titleEdit.setText(arguments.notes?.title)
            binding.descriptionEdit.setText(arguments.notes?.description)
            color = arguments.notes?.color!!
        }
        binding.colorCodeImage.setBackgroundColor(resources.getColor(color))
        val formatter = SimpleDateFormat("E, dd-M-yyyy hh:mm:ss")
        date = formatter.format(Date())

        // if we want to update then we wont change real date when note was created
        if(update) {
            id = arguments.notes?.id
            date = arguments.notes?.date!!
            color = arguments.notes?.color!!
            val title = arguments.notes?.title
            val dis = arguments.notes?.description
            notes = Notes(id,title.toString(),dis.toString(),date,color)
        }


        //Clicking on Save Button
        binding.savenotes.setOnClickListener{
            notes = Notes(id,binding.titleEdit.text.toString(),binding.descriptionEdit.text.toString(),
                date,color)
            val action =NotesEditDirections.actionNotesEditToNotesFront(
                notes
            )
            if(update){
                viewModel.update(notes)
            }
            else{
                viewModel.insert(notes)
            }

            findNavController().navigate(action)

        }



            // Setting Color For Notes
        binding.colorCodeImageView.setOnClickListener{
           item->
            showPopUpMenu(item)
        }

        expandAnim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {
                binding.record.cardText.visibility = View.VISIBLE
                binding.record.cardIcon.setImageDrawable(resources.getDrawable(R.drawable.baseline_stop_24))
                binding.record.recordBtn.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.card_red))
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        binding.record.recordBtn.setOnClickListener{
            if(isListening){
                speechRecognizer.stopListening()
                binding.record.cardText.visibility = View.GONE
                binding.record.cardIcon.setImageDrawable(resources.getDrawable(R.drawable.ic_mic))
                binding.record.recordBtn.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.purple))
            }
            else{
                if(!hasAudioPermission()){
                    askPermission()
                    isListening=!isListening
                }
                else{
                    binding.record.recordBtn.startAnimation(expandAnim)
                    speechRecognizer.startListening(speechIntent)
                }
            }
            isListening=!isListening
        }


    }

    private fun hasAudioPermission():Boolean{
        if(ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            return false
        }
        return true
    }
    private fun askPermission(){
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.RECORD_AUDIO), REQUEST_AUDIO_PERMISSION)
    }


    private fun showPopUpMenu(view: View?) {
        val popUpMenu = PopupMenu(requireContext(),view)
        popUpMenu.menuInflater.inflate(R.menu.color_code,popUpMenu.menu)

        //Updating Value of color by clicks
        popUpMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.red -> {
                    color = R.color.red
                    binding.colorCodeImage.setBackgroundColor(resources.getColor(color))
                    true
                }
                R.id.green -> {
                   color = R.color.green
                    binding.colorCodeImage.setBackgroundColor(resources.getColor(color))
                    true
                }
                R.id.blue->{
                    color = R.color.blue
                    binding.colorCodeImage.setBackgroundColor(resources.getColor(color))

                    true
                }
                R.id.skin->{
                    color = R.color.skin
                    binding.colorCodeImage.setBackgroundColor(resources.getColor(color))
                    true
                }
                R.id.teal->{
                    color = R.color.teal
                    binding.colorCodeImage.setBackgroundColor(resources.getColor(color))
                    true
                }
                R.id.yellow->{
                    color = R.color.yellow
                    binding.colorCodeImage.setBackgroundColor(resources.getColor(color))
                    true

                }
                R.id.purple->{
                    color = R.color.purple
                    binding.colorCodeImage.setBackgroundColor(resources.getColor(color))
                    true
                }
                R.id.grey->{
                    color = R.color.grey
                    binding.colorCodeImage.setBackgroundColor(resources.getColor(color))
                    true
                }


                else -> false
            }
        }
        try{
            val fieldPopup = PopupMenu::class.java.getDeclaredField("mPopup")
            fieldPopup.isAccessible = true
            val mPopup = fieldPopup.get(popUpMenu)
            mPopup.javaClass.
                    getDeclaredMethod("setForceShowIcon",Boolean::class.java).
                    invoke(mPopup,true)

        } catch (e:Exception) {
            Log.d("error", "Error showing icons")
        } finally {
            popUpMenu.show()
        }


    }


}