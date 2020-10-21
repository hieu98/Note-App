package com.example.noteapp.model
import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties

data class Album ( val name:String,
                   val note:String,
                   val img:String,
                   val count:String):Serializable

