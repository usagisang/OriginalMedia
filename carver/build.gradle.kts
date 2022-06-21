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
        vectorDrawables {
            useSupportLibrary = true
        }
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
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
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
    implementation(project(":okresult"))

    implementation(Deps.CameraX.camera2)
    implementation(Deps.CameraX.core)
    implementation(Deps.CameraX.lifeCycle)
    implementation(Deps.CameraX.view)
    implementation(Deps.CameraX.video)

    implementation(Deps.Coroutines.coroutinesAndroid)

    implementation(Deps.Compose.ui)
    implementation(Deps.Compose.material)
    implementation(Deps.Compose.uiToolingPreview)
    implementation(Deps.Compose.composeActivity)

    testImplementation(TestDeps.Local.junit)
    androidTestImplementation(TestDeps.Instrumentation.espresso)
    androidTestImplementation(TestDeps.Instrumentation.junitExtension)

    androidTestImplementation(Deps.Compose.junitTest)
    debugImplementation(Deps.Compose.uiDebugTool)
    debugImplementation(Deps.Compose.testManifest)
}