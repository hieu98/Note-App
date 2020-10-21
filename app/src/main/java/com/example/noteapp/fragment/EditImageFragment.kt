package com.example.noteapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import com.example.noteapp.Interface.EditImageFragmentListener
import com.example.noteapp.R

class EditImageFragment : Fragment(), SeekBar.OnSeekBarChangeListener {
    private var listener: EditImageFragmentListener? = null
    internal lateinit var seekbar_brightness : SeekBar
    internal lateinit var seekbar_constrant : SeekBar
    internal lateinit var seekbar_saturatation : SeekBar

    fun resetControl(){
        seekbar_brightness.progress= 100
        seekbar_constrant.progress = 0
        seekbar_saturatation.progress = 10
    }

    fun setListener(listener: EditImageFragmentListener){
        this.listener = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_image, container, false)
        seekbar_brightness = view.findViewById<SeekBar>(R.id.seekbar_brightness)
        seekbar_constrant = view.findViewById<SeekBar>(R.id.seekbar_constrant)
        seekbar_saturatation = view.findViewById<SeekBar>(R.id.seekbar_saturatation)

        seekbar_brightness.max = 200
        seekbar_brightness.progress= 100

        seekbar_constrant.max = 20
        seekbar_constrant.progress = 0

        seekbar_saturatation.max = 30
        seekbar_saturatation.progress = 10

        seekbar_saturatation.setOnSeekBarChangeListener(this)
        seekbar_constrant.setOnSeekBarChangeListener(this)
        seekbar_brightness.setOnSeekBarChangeListener(this)

        return view
    }

    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
        var progress = p1
        if(listener != null){
            if (p0?.id == R.id.seekbar_brightness){
                listener!!.onBrightnessChanged(progress -100)
            }else if (p0?.id == R.id.seekbar_constrant){
                progress += 10
                val floatVal =.10f*progress
                listener!!.onConstrantChanged(floatVal)
            }else if (p0?.id == R.id.seekbar_saturatation){
                val floatVal =.10f*progress
                listener!!.onSaturationChanged(floatVal)
            }
        }
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {
        if (listener != null){
            listener!!.onEditStarted()
        }
    }

    override fun onStopTrackingTouch(p0: SeekBar?) {
        if (listener != null){
            listener!!.onEditCompleted()
        }
    }

}