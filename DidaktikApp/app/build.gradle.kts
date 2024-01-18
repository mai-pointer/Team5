plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    //Room
    id("kotlin-kapt")
}

android {
    namespace = "com.example.didaktikapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.didaktikapp"
        minSdk = 26
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}
    
dependencies {
    implementation ("com.github.bumptech.glide:glide:4.12.0")

    implementation ("com.android.volley:volley:1.2.1")
    implementation ("io.socket:socket.io-client:2.0.0")
    implementation("androidx.room:room-runtime:2.2.5")
    kapt("androidx.room:room-compiler:2.2.5")
    implementation ("com.google.android.exoplayer:exoplayer-core:2.16.1")
    implementation ("com.google.android.exoplayer:exoplayer-ui:2.16.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.9")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.0")
    implementation("com.google.android.material:material:1.6.0")
    implementation("com.makeramen:roundedimageview:2.3.0")
    implementation ("androidx.gridlayout:gridlayout:1.0.0")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.6")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.6")
    implementation ("androidx.databinding:databinding-runtime:8.2.1")
    implementation("androidx.gridlayout:gridlayout:1.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.2")
    implementation("io.socket:socket.io-client:2.0.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}