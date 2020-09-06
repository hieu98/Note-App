package com.example.noteapp.model

import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class User ( val userid:String, val username:String, val email:String, val phone:String):Serializable