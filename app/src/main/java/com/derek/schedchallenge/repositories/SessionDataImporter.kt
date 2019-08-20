package com.derek.schedchallenge.repositories

import android.content.Context
import com.derek.schedchallenge.models.SessionResponse
import com.derek.schedchallenge.utils.readAssetsFile
import com.squareup.moshi.Moshi
import io.reactivex.Single
import org.json.JSONObject

class SessionDataImporter(
  private val context: Context,
  private val moshi: Moshi
) {

  fun importSessionData(
    sessionDataType: SessionsRepository.SessionDataType
  ): Single<List<SessionResponse>> =
    context.readAssetsFile(
      fileName = sessionDataType.fileName
    ).map { json ->
      val jsonObject = JSONObject(json)
      val resultArray = jsonObject.getJSONArray("result")
      val resultLength = resultArray.length()
      val sessions = mutableListOf<SessionResponse>()

      for (i in 0 until resultLength) {
        val sessionJson = resultArray[i].toString()
        val session = moshi.adapter(SessionResponse::class.java).fromJson(sessionJson)
        if (session != null) sessions.add(session)
      }

      sessions
    }
}