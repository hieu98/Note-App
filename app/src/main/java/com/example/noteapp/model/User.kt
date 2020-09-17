package com.example.noteapp.model
import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
<<<<<<< HEAD

data class User ( val userid:String, val username:String, val email:String, val phone:String):Serializable


=======
data class User (
    val userid:String,
    val username:String,
    val email:String,
    val phone:String):Serializable
>>>>>>> 3ad2be0468a0badd7a7153275b50be86c300446d
