package com.example.noteapp.model
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties

data class Album (var Name:String?="", var Note: String? ="")

