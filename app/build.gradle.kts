import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
}

// los datos para firmar el APK de release se leen de keystore.properties
// (en la raiz del proyecto). ese archivo y el keystore estan en .gitignore,
// asi las claves nunca quedan escritas aca ni se suben a GitHub.
// si el archivo no existe, el release igual compila pero sin firmar
val archivoFirma = rootProject.file("keystore.properties")
val propiedadesFirma = Properties().apply {
    if (archivoFirma.exists()) archivoFirma.inputStream().use { load(it) }
}

android {
    namespace = "cl.duoc.ferresenas"
    compileSdk = 37

    signingConfigs {
        if (archivoFirma.exists()) {
            create("release") {
                storeFile = rootProject.file(propiedadesFirma.getProperty("storeFile"))
                storePassword = propiedadesFirma.getProperty("storePassword")
                keyAlias = propiedadesFirma.getProperty("keyAlias")
                keyPassword = propiedadesFirma.getProperty("keyPassword")
            }
        }
    }

    defaultConfig {
        applicationId = "cl.duoc.ferresenas"
        minSdk = 24
        targetSdk = 37
        // version 2: base de datos SQLite, sesion recordada y CRUD
        versionCode = 2
        versionName = "2.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            if (archivoFirma.exists()) {
                signingConfig = signingConfigs.getByName("release")
            }
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.activity:activity-compose:1.9.3")

    implementation(platform("androidx.compose:compose-bom:2026.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    implementation("androidx.navigation:navigation-compose:2.8.5")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")

    testImplementation("junit:junit:4.13.2")
    // espresso 3.5.1 se caia en Android nuevo (usaba InputManager.getInstance
    // que ya no existe), por eso lo subi
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
    androidTestImplementation(platform("androidx.compose:compose-bom:2026.08.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
