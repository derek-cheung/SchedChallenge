plugins {
  id("com.android.application")
  id("kotlin-android")
  id("kotlin-android-extensions")
  id("kotlin-kapt")
}

android {
  compileSdkVersion(28)

  defaultConfig {
    minSdkVersion(21)
    targetSdkVersion(28)

    applicationId = "com.derek.schedchallenge"
    versionCode = 1
    versionName = "1.0"

    buildTypes {

      getByName("debug") {
        isMinifyEnabled = false
        proguardFile(getDefaultProguardFile("proguard-android.txt"))
        proguardFile("proguard-rules.pro")
      }
    }
  }
}

dependencies {
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.31")
  implementation("androidx.appcompat:appcompat:1.0.2")
  implementation("androidx.core:core-ktx:1.0.2")
  implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.0.0")
  implementation("androidx.constraintlayout:constraintlayout:1.1.3")

  implementation("io.reactivex.rxjava2:rxandroid:2.1.1")
  implementation("io.reactivex.rxjava2:rxjava:2.2.6")
  implementation("com.jakewharton.rxbinding3:rxbinding:3.0.0-alpha2")

  implementation("com.squareup.moshi:moshi:1.8.0")
  implementation("com.squareup.moshi:moshi-kotlin:1.8.0")

  implementation("org.koin:koin-android:2.0.1")
  implementation("org.koin:koin-androidx-viewmodel:2.0.1")

  implementation("androidx.room:room-runtime:2.1.0")
  implementation("androidx.room:room-rxjava2:2.1.0")
  kapt("androidx.room:room-compiler:2.1.0")

  testImplementation("junit:junit:4.12")
  testImplementation("org.mockito:mockito-core:2.18.3")
  testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.1.0")
}
