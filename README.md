# Android Authentication using Sign in with Google, Credential Manager and Firebase Authentication in Kotlin
In case [original guide](https://developer.android.com/identity/sign-in/credential-manager-siwg) was quite hard for you to master, feel free to use the following template.

1. In order to sign-in and sign-out, you have to create corresponding buttons.

2. Dependencies from build.gradle I used in my app:
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
3. minSdk, targetSdk, compileSdk = 34
4. Add this to your activity for proper use of "is" statements in handleSignIn method:
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
