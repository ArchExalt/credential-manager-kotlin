# Android Authentication using Sign in with Google, Credential Manager and Firebase Authentication in Kotlin
In case [original guide](https://developer.android.com/identity/sign-in/credential-manager-siwg) was quite hard for you to master, feel free to use the following [template](https://github.com/ArchExalt/CredentialManager/blob/main/AuthActivity.kt).

1. In order to sign-in and sign-out, you have to create corresponding buttons.

2. Dependencies from build.gradle used in my app:
```
dependencies {
    implementation 'androidx.core:core-ktx:1.13.1'
    implementation 'androidx.viewpager:viewpager:1.0.0'
    implementation 'androidx.appcompat:appcompat:1.7.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    implementation 'androidx.navigation:navigation-fragment-ktx:2.7.7'
    implementation 'androidx.navigation:navigation-ui-ktx:2.7.7'
    implementation 'androidx.preference:preference-ktx:1.2.1'
    implementation 'androidx.fragment:fragment-ktx:1.8.1'
    implementation 'androidx.credentials:credentials:1.2.2'
    implementation 'androidx.credentials:credentials-play-services-auth:1.2.2'
    implementation 'com.google.gms:google-services:4.4.2'
    implementation 'com.google.android.material:material:1.12.0'
    implementation 'com.google.android.gms:play-services-appset:16.1.0'
    implementation 'com.google.android.gms:play-services-auth:21.2.0'
    implementation 'com.google.android.gms:play-services-base:18.5.0'
    implementation 'com.google.android.gms:play-services-cronet:18.1.0'
    implementation 'com.google.android.libraries.identity.googleid:googleid:1.1.1'
    implementation 'com.google.firebase:firebase-auth:23.0.0'
    implementation 'com.google.firebase:firebase-database:21.0.0'
    implementation 'com.github.bumptech.glide:glide:4.12.0'
    testImplementation 'junit:junit:4.13.2'
    androidTestImplementation 'androidx.test.ext:junit:1.2.1'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.6.1'
}
```
3. minSdk = 26 because it's the lowest one required for creating a nonce
4. Add this to your activity for proper use of is-statements in handleSignIn method:
```
    <TextView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:isCredential="true"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />
```
# A proper signing config for a RELEASE version
1. In case Firebase Authentication works with DEBUG version but not with RELEASE one:
- check in project settings whether RELEASE version also uses signingConfig;
- check for correct SHA1 and SHA256 fingeprints added into Firebase project configuration.
2. build.gradle file (pay attention, debuggable = false, keyAlias = key2)
```
android {
    signingConfigs {
        release {
            storeFile file('E:\\path\\to\\keystore.jks')
            storePassword 'yourpassword'
            keyPassword 'yourpassword'
            keyAlias 'key2'
        }
    }
buildTypes {
        release {
            minifyEnabled true
            shrinkResources true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
            debuggable false
            signingConfig signingConfigs.release
        }
    }
```
# What to do in case you forgot your key/keystore passwords?
1. Go to Google Console, select "change signing key" and then "from Java storage". Be careful, you gotta use two different key aliases from your keystore, key1 and key2.
2. Install newest [JDK](https://jdk.java.net/22/) and open the folder with your JDK in PowerShell. For example:
```
cd E:\Java\jdk-22.0.1\bin
```
3. Download the public key and PEPK tool under "from Java storage" option (steps 1 and 2).
4. Use the following command to encrypt your private key (step 3). ATTENTION! YOU'LL USE KEY #1 (key1) TO ENCRYPT THE PUBLIC KEY.
```
.\java -jar "E:\path\to\pepk.jar" --keystore="E:\path\to\keystore.jks" --alias=key1 --output="E:\path\to\output.zip" --include-cert --rsa-aes-encryption --encryption-key-path="E:\path\to\encryption_public_key.pem"
```
5. Create a new key alias during RELEASE bundle build. Create a new upload key, key2. ATTENTION! YOU'LL USE KEY #2 HERE.
```
.\keytool -export -rfc -keystore "E:\path\to\keystore.jks" -alias key2 -file "E:\path\to\encryption_public_key.pem"
```
MAJOR WARNING, ONCE AGAIN: YOU'LL USE KEY #1 TO ENCRYPT PRIVATE KEY AND KEY #2 AS AN UPLOAD KEY. YOU'LL ALSO USE KEY #2 IN SIGNINGCONFIG. ONCE YOU CHANGE KEY ALIAS, SHA1 AND SHA256 WILL ALSO CHANGE, SO ADD FINGERPRINTS TO FIREBASE CONFIGURATION ONLY AFTER COMPLETING ALL THE PREVIOUS STEPS.
