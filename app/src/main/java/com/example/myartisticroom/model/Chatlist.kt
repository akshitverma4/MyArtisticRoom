package com.example.myartisticroom.model

class Chatlist {
    private var id:String=""
constructor()
    constructor(id: String) {
        this.id = id
    }
    fun id():String?{
        return id
    }
    fun setId(id: String){
        this.id = id
    }
}