plugins {
    alias(libs.plugins.android.application)
    id("org.jetbrains.dokka") version "1.9.0"
}

android {
    namespace = "masterIoT.mdp.karma"
    compileSdk = 36

    defaultConfig {
        applicationId = "masterIoT.mdp.karma"
        minSdk = 24
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

tasks.dokkaHtml.configure {
    outputDirectory.set(file("$buildDir/docs/dokka"))
    dokkaSourceSets {
        named("main") {
            displayName.set("Karma App")
            sourceRoots.from(file("src/main/java"))
            includeNonPublic.set(false)
            skipEmptyPackages.set(true)
            reportUndocumented.set(true)
            platform.set(org.jetbrains.dokka.Platform.jvm)
            languageVersion.set("8")
        }
    }
}
