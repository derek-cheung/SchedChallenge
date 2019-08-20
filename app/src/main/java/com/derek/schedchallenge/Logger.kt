package com.derek.schedchallenge

import android.util.Log
import com.derek.schedchallenge.models.Person
import com.derek.schedchallenge.models.Session
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import org.json.JSONArray

class Logger(
  private val moshi: Moshi
) {

  fun logSessions(
    sessions: List<Session>
  ): String {
    val sessionsJson = moshi.adapter<List<Session>>(Types.newParameterizedType(List::class.java, Session::class.java)).toJson(sessions)
    val displayJson = JSONArray(sessionsJson).toString(4)
    Log.d("Sessions", displayJson)

    return displayJson
  }

  fun logPersons(
    persons: List<Person>
  ): String {
    val personsJson = moshi.adapter<List<Person>>(Types.newParameterizedType(List::class.java, Person::class.java)).toJson(persons)
    val displayJson = JSONArray(personsJson).toString(4)
    Log.d("Persons", displayJson)

    return displayJson
  }
}