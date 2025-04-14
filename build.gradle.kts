plugins {
    id("com.android.application") version "8.1.2" apply false
    id("com.android.library") version "8.1.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.20" apply false
    id("com.google.dagger.hilt.android") version "2.48.1" apply false
    id("com.google.gms.google-services") version "4.4.1" apply false  // Add this line
    id("com.google.devtools.ksp") version "1.9.20-1.0.14"
}



// Add this block if not present
buildscript {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
    dependencies {
        classpath("com.google.gms:google-services:4.4.1")  // Add this line
    }
}