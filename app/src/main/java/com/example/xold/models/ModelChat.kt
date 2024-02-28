package com.example.xold.models

class ModelChat {

    var messageId: String = ""
    var messageType: String = ""
    var message: String = ""
    var fromUid: String = ""
    var toUid: String = ""
    var timestamp: Long = 0

    constructor()
    constructor(
        messageId: String,
        messageType: String,
        message: String,
        fromUid: String,
        toUid: String,
        timestamp: Long
    ) {
        this.messageId = messageId
        this.messageType = messageType
        this.message = message
        this.fromUid = fromUid
        this.toUid = toUid
        this.timestamp = timestamp
    }
}
