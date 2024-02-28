package com.example.xold.models

import android.net.Uri

class ModelImagePicked {

    var id = ""
    var imageUri: Uri? = null
    var imageUrl: String? = null
    var fromInternet = false

    constructor()


    constructor(id: String, imageUri: Uri?, imageUrl: String?, fromInternet: Boolean) {
        this.id = id
        this.imageUri = imageUri
        this.imageUrl = imageUrl
        this.fromInternet = fromInternet
    }
}