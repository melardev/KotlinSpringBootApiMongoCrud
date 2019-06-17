package com.melardev.spring.mongo.dtos.responses

class SuccessResponse @JvmOverloads constructor(message: String) : AppResponse(true) {

    init {
        addFullMessage(message)
    }
}
