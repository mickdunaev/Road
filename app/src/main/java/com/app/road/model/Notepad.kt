package com.app.road.model

class Notepad(val day:Int, val mode:Int, val id:Int, val text:String ) {
    var answer: String = ""
    var answerId: String = ""
    override fun toString(): String {
        return "Notepad(day=$day, mode=$mode, id=$id, text='$text')"
    }
}
