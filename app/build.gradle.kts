import java.util.Properties
import java.io.StringReader

plugins {
    alias(libs.plugins.android.application)
}

val mapsSettings = Properties().apply {
    load(StringReader(providers.fileContents(
        rootProject.layout.projectDirectory.file("maps.properties")
    ).asText.orElse("").get()))
}
val mapsApiKey = mapsSettings.getProperty("MAPS_API_KEY", "").trim()

android {
    namespace = "dev.josegaldamez.tickethn_uth"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "dev.josegaldamez.tickethn_uth"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        manifestPlaceholders["MAPS_API_KEY"] = mapsApiKey
        buildConfigField("boolean", "MAPS_CONFIGURED",
            (mapsApiKey.isNotBlank() && !mapsApiKey.startsWith("DEMO_")).toString())
    }

    buildFeatures { buildConfig = true }

    buildTypes {
        release {
            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.material)
    implementation("androidx.viewpager2:viewpager2:1.0.0")
    implementation("com.google.android.gms:play-services-maps:20.1.0")
    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
}
