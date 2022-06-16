plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-android-extensions")

}

android {


    compileSdk = Build.compileSdkVersion

    defaultConfig {
        applicationId = Build.applicationId
        minSdk = Build.minimumSdkVersion
        targetSdk = Build.targetSdkVersion
        versionCode = Build.versionCode
        versionName = Build.versionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        dataBinding = true
        viewBinding = true

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


}

dependencies {
    implementation(project(":dependencies"))
    implementation(project(":carver"))

    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    
    testImplementation(TestDeps.Local.junit)
    androidTestImplementation(TestDeps.Instrumentation.espresso)
    androidTestImplementation(TestDeps.Instrumentation.junitExtension)
    api(Deps.ImmersionBar.baseBag)
    api(Deps.ImmersionBar.baseKTX)
    api(Deps.ImmersionBar.fragmentDie)
    api(Deps.retrofit)
    api(Deps.retrofitGsonConverter)
    api(Deps.Lifecycle.livedata)
    api(Deps.Navigation.navigationUI)
    api(Deps.Navigation.fragment)

}