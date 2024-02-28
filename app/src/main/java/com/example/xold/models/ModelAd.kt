package com.example.xold.models

class ModelAd{

    var id: String = ""
    var uid: String = ""
    var brand: String = ""
    var category: String = ""
    var condition: String = ""
    var address: String = ""
    var price: String = ""
    var title: String = ""
    var description: String = ""
    var status: String = ""
    var timestamp: Long = 0
    var latitude= 0.0
    var longitude= 0.0
    var favorite= false


    constructor()


    constructor(
        id: String,
        uid: String,
        brand: String,
        category: String,
        condition: String,
        address: String,
        price: String,
        title: String,
        description: String,
        status: String,
        timestamp: Long,
        latitude: Double,
        longitude: Double,
        favorite: Boolean
    ) {
        this.id = id
        this.uid = uid
        this.brand = brand
        this.category = category
        this.condition = condition
        this.address = address
        this.price = price
        this.title = title
        this.description = description
        this.status = status
        this.timestamp = timestamp
        this.latitude = latitude
        this.longitude = longitude
        this.favorite = favorite
    }
}