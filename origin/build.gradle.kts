plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    compileSdk = Build.targetSdkVersion

    defaultConfig {
        minSdk = Build.minimumSdkVersion
        targetSdk = Build.targetSdkVersion

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Deps.Compose.compilerVersion
    }
}

dependencies {
    implementation(project(":dependencies"))
    implementation(project(":carver"))
    implementation(project(":uploader"))
    implementation(project(":okresult"))

    implementation(Deps.Coroutines.coroutinesAndroid)

    implementation(Deps.glide)
    implementation(Deps.retrofit)
    implementation(Deps.Lifecycle.livedata)
    implementation(Deps.DataStore.preferences)

    implementation(Deps.swipeRefreshLayout)

    implementation(Deps.glplayer)

    implementation(Deps.Compose.ui)
    implementation(Deps.Compose.material)
    implementation(Deps.Compose.uiToolingPreview)
    implementation(Deps.Compose.composeActivity)
    implementation(Deps.Compose.runtime)

    testImplementation(TestDeps.Local.junit)
    androidTestImplementation(TestDeps.Instrumentation.espresso)
    androidTestImplementation(TestDeps.Instrumentation.junitExtension)
}