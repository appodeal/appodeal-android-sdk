# Appodeal Android SDK

[![](https://img.shields.io/badge/SDK%20version-Stable%202.11.1-brightgreen)](https://wiki.appodeal.com/en/android/get-started)

[Documentation](https://wiki.appodeal.com/en/android/get-started/)

### Data Protection Regulation (GDPR, CCPA)
https://wiki.appodeal.com/en/android/get-started/data-protection/gdpr-and-ccpa

[Example consent code](https://github.com/appodeal/appodeal-android-demo/blob/master/app/src/main/java/com/appodeal/test/SplashActivity.java)
android {
    compileSdkVersion 27
    defaultConfig {
        //Edit the applicationId for changing your BundeID.
        applicationId "com.yourdomainname.yourappname"
        minSdkVersion 15
        targetSdkVersion 27
        versionCode 1
        versionName "1.0"
        testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
    }
}
