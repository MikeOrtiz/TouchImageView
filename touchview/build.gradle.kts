import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.library")
    id("maven-publish")
}

android {
    namespace = "com.ortiz.touchview"
    defaultConfig {
        minSdk = 16
        aarMetadata {
            minCompileSdk = 28
        }
        testFixtures {
            enable = true
        }

        compileSdk = 36
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            consumerProguardFile("proguard-sdk.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_17
        }
    }
    publishing {
        singleVariant("release") {}
    }
}

dependencies {
    api("androidx.appcompat:appcompat:1.7.1")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:2.3.20")
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["release"])
                pom {
                    licenses {
                        license {
                            name = "Apache License Version 2.0"
                            url = "https://github.com/MikeOrtiz/TouchImageView/blob/master/LICENSE"
                        }
                    }
                }
            }
        }
    }
}

