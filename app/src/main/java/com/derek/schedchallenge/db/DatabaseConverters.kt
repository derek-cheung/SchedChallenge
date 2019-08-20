package com.derek.schedchallenge.db

import androidx.room.TypeConverter

class DatabaseConverters {

  @TypeConverter
  fun fromStringList(string: String): List<String> =
    if (string.isBlank()) {
      listOf()
    } else {
      string.split(",")
    }

  @TypeConverter
  fun toStringList(strings: List<String>): String =
    if (strings.isEmpty()) {
      ""
    } else {
      strings.joinToString(",")
    }
}