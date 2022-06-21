package com.kokomi.uploader.handler

import com.kokomi.uploader.network.releaseService
import org.json.JSONObject

class TokenRequestHandler : RequestHandler() {

    override suspend fun request(any: Any?): String {
        val json = releaseService.token().string()
        return JSONObject(json).getString("token")
    }

}