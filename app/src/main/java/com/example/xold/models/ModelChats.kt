package com.example.xold.models

class ModelChats {

    var profileImageUrl = ""
    var name = ""
    var chatKey = ""
    var receiptUid = ""
    var messageId = ""
    var messageType = ""
    var message = ""
    var fromUid = ""
    var toUid = ""
    var timestamp: Long = 0

    constructor()
    constructor(
        profileImageUrl: String,
        name: String,
        chatKey: String,
        receiptUid: String,
        messageId: String,
        messageType: String,
        message: String,
        fromUid: String,
        toUid: String,
        timestamp: Long
    ) {
        this.profileImageUrl = profileImageUrl
        this.name = name
        this.chatKey = chatKey
        this.receiptUid = receiptUid
        this.messageId = messageId
        this.messageType = messageType
        this.message = message
        this.fromUid = fromUid
        this.toUid = toUid
        this.timestamp = timestamp
    }

}