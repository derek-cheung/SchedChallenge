package com.derek.schedchallenge.models

import androidx.room.ColumnInfo

data class SessionResponse(
  val id: String,
  val name: String? = "",
  val description: String = "",
  val speakers: List<Person> = listOf(),
  val sponsors: List<Person> = listOf(),
  val moderators: List<Person> = listOf(),
  val artists: List<Person> = listOf(),
  val exhibitors: List<Person> = listOf(),

  @ColumnInfo(name = "start")
  val startTime: Long = 0,

  @ColumnInfo(name = "end")
  val endTime: Long = 0
)