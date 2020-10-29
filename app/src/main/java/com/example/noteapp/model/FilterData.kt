package com.example.noteapp.model
import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class FilterData (
    val name:String,
    val rule:String,
    val imageId:Int):Serializable

