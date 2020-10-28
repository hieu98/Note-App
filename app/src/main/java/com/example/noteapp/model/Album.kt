package com.example.noteapp.model
import com.google.firebase.database.IgnoreExtraProperties
@IgnoreExtraProperties

//data class Album (var Name:String?="", var Note: String? ="")

class Album {
    var mName: String = ""
    var mNote: String = ""
    var mCount: String = ""
    var mImage: String = ""

    constructor() {}
    constructor(name: String, note: String, count: String, image: String) {
        this.mName = name
        this.mNote = note
        this.mCount = count
        this.mImage = image
    }

}


