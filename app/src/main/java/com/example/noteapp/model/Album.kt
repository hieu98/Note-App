package com.example.noteapp.model
import android.graphics.Bitmap
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties

class Album(nameab:String, note:String, image: String) {
    var Nameab : String = ""
    var Note : String = ""
    var Image : String = ""

    init {
        Nameab = nameab
        Note = note
        Image = image
    }

//    constructor(nameab: String, note: String, image: String){
//        Nameab = nameab
//        Note = note
//        Image = image
//    }
//    @JvmName("setNameab1")
//    fun setNameab(name : String) {
//        Nameab = name
//    }
//
//    @JvmName("getNameab1")
//    fun getNameab() : String{
//        return Nameab
//    }
//
//    @JvmName("setNote1")
//    fun setNote(note : String){
//        Note = note
//    }
//
//    @JvmName("getNote1")
//    fun getNote() : String{
//        return Note
//    }
//
//
//    @JvmName("setImage1")
//    fun setImage(image : String){
//        Image = image
//    }
//
//    @JvmName("getImage1")
//    fun getImage() : String{
//        return Image
//    }





}

