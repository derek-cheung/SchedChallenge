package com.derek.schedchallenge.utils

import android.content.Context
import io.reactivex.Single
import java.nio.charset.Charset

fun Context.readAssetsFile(fileName: String): Single<String> =
  Single.fromCallable {
    val input = assets.open(fileName)
    val size = input.available()
    val buffer = ByteArray(size)
    input.read(buffer)
    input.close()

    buffer.toString(Charset.forName("UTF-8"))
  }