object Build {
    const val applicationId = "top.gochiusa.originalmedia"

    const val compileSdkVersion = 31
    const val targetSdkVersion = 31
    const val minimumSdkVersion = 21

    const val versionCode = 1
    const val versionName = "0.1.0"
}

object Deps {
    object Compose {
        const val dependentiesVersion = "1.1.1"
        const val compilerVersion = "1.1.1"

        const val composeUi = "androidx.compose.ui:ui:$dependentiesVersion"
        const val composeMaterial = "androidx.compose.material:material:$dependentiesVersion"
        const val composeUiToolingPreview =
            "androidx.compose.ui:ui-tooling-preview:$dependentiesVersion"
        const val livedataRuntime =
            "androidx.compose.runtime:runtime-livedata:$dependentiesVersion"
        const val composeActivity = "androidx.activity:activity-compose:1.4.0"
        const val composeViewBinding = "androidx.compose.ui:ui-viewbinding:$dependentiesVersion"

        const val composeRuntime = "androidx.compose.runtime:runtime:$dependentiesVersion"
        const val uiDebugTool = "androidx.compose.ui:ui-tooling:$dependentiesVersion"
        const val junitTest = "androidx.compose.ui:ui-test-junit4:$dependentiesVersion"

        const val constraintLayoutDSL =
            "androidx.constraintlayout:constraintlayout-compose:1.0.1"
    }

    object Lifecycle {
        const val version = "2.4.1"
        const val viewModelCompose = "androidx.lifecycle:lifecycle-viewmodel-compose:$version"
        const val viewModelKtx = "androidx.lifecycle:lifecycle-viewmodel-ktx:$version"
    }

    object Navigation {
        const val version = "2.4.2"
        const val fragmentKtx = "androidx.navigation:navigation-fragment-ktx:$version"
        const val navigationUiKtx = "androidx.navigation:navigation-ui-ktx:$version"
        const val compose = "androidx.navigation:navigation-compose:$version"
    }

    object Ktx {
        const val fragmentKtx = "androidx.fragment:fragment-ktx:1.4.1"
        const val coreKtx = "androidx.core:core-ktx:1.7.0"
    }

    object Coroutines {
        const val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.2"
        const val coroutinesAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.2"
    }

    object Coil {
        const val version = "2.1.0"
        const val coilCore = "io.coil-kt:coil:$version"
        const val composeExtension = "io.coil-kt:coil-compose:$version"
    }

    object ExoPlayer {
        const val version = "2.17.1"
        const val core = "com.google.android.exoplayer:exoplayer-core:$version"
        const val dash = "com.google.android.exoplayer:exoplayer-dash:$version"
        const val ui = "com.google.android.exoplayer:exoplayer-ui:$version"
    }

    object Accompanist {
        const val version = "0.23.1"
        const val accompanistInsets = "com.google.accompanist:accompanist-insets:$version"
        const val pager = "com.google.accompanist:accompanist-pager:$version"
        const val pagerIndicators = "com.google.accompanist:accompanist-pager-indicators:$version"
        const val swipeRefresh = "com.google.accompanist:accompanist-swiperefresh:$version"
    }

    object Room {
        private const val version = "2.4.2"
        const val roomKtx = "androidx.room:room-ktx:$version"
        const val roomRuntime = "androidx.room:room-runtime:$version"
        const val roomCompiler = "androidx.room:room-compiler:$version"
    }

    object ARouter {
        private const val version = "1.5.2"
        const val arouterApi = "com.alibaba:arouter-api:$version"
        const val arouterCompile = "com.alibaba:arouter-compiler:$version"
    }

    object Debug {
        const val leakCanary = "com.squareup.leakcanary:leakcanary-android:2.9.1"
    }

    object Paging {
        const val runtime = "androidx.paging:paging-runtime-ktx:3.1.1"
        const val compose = "androidx.paging:paging-compose:1.0.0-alpha14"
    }

    const val appCompat = "androidx.appcompat:appcompat:1.4.1"
    const val constraintLayout = "androidx.constraintlayout:constraintlayout:2.1.4"
    const val material = "com.google.android.material:material:1.6.0"

    const val okhttp = "com.squareup.okhttp3:okhttp:4.9.3"

    const val retrofit = "com.squareup.retrofit2:retrofit:2.9.0"

    // Retrofit的gson转换器
    const val retrofitGsonConverter = "com.squareup.retrofit2:converter-gson:2.9.0"

    const val glide = "com.github.bumptech.glide:glide:4.13.2"

    // RecyclerView - BaseRecyclerViewAdapterHelper
    const val baseRecyclerViewAdapterHelper =
        "com.github.CymChad:BaseRecyclerViewAdapterHelper:3.0.7"

    // RecyclerView - GravitySnapHelper
    const val gravitySnapHelper = "com.github.rubensousa:gravitysnaphelper:2.2.2"

    const val viewPager2 = "androidx.viewpager2:viewpager2:1.0.0"

    const val kspVersion = "1.6.21-1.0.5"

}

object TestDeps {

    object Local {

        const val junit = "junit:junit:4.13.2"
    }

    object Instrumentation {

        const val junitExtension = "androidx.test.ext:junit:1.1.3"

        const val espresso = "androidx.test.espresso:espresso-core:3.4.0"
    }
}