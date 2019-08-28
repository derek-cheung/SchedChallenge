package com.derek.schedchallenge.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Session(
  @PrimaryKey
  val id: String,
  val name: String? = "",
  val description: String,
  val speakers: List<String>,
  val sponsors: List<String>,
  val moderators: List<String>,
  val artists: List<String>,
  val exhibitors: List<String>,

  @ColumnInfo(name = "start_time")
  val startTime: Long,

  @ColumnInfo(name = "end_time")
  val endTime: Long
) {

  override fun equals(other: Any?): Boolean =
    other != null && other is Session && other.id == id

  override fun hashCode(): Int = id.hashCode()
}