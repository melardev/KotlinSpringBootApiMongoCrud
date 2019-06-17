package com.melardev.spring.mongo.dtos.responses

import com.melardev.spring.mongo.dtos.responses.AppResponse

class ErrorResponse(errorMessage: String) : AppResponse(false) {

    init {
        addFullMessage(errorMessage)
    }

}
