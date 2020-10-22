package com.example.noteapp.model
import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties

data class Image ( var name : String = "",
                   var uri : String = "",
                   var note : String = "")

