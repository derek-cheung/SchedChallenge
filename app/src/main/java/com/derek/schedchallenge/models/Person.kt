package com.derek.schedchallenge.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Person(
  @PrimaryKey
  val id: String,
  val name: String
) {

  override fun equals(other: Any?): Boolean =
    other != null && other is Person && other.id == id

  override fun hashCode(): Int = id.hashCode()

  override fun toString(): String =
    "{\n\tid: $id,\n\tname: $name}"
}