<img src="docs/banner.png" width="100%">

# Mejiboard
image (イメージ) + board = メジboard (mejiboard)

An image board ~~client~~ viewer based on [Gelbooru](https://gelbooru.com) for android, made from Jetpack Compose 🚀.

## Features
- Material Design UI
- Easy one-handed operation
- Lightweight
- System-based theme with 3 themes available (Light, Dark & Black)
- Search images with tags
- Image viewer with zoom & pan support
- Download and share image
- DNS over HTTPS enabled by default

## Download
Latest **alpha** variant:
- Version: `1.4.0-alpha`
- Date: 2022-05-12
- Links: [changelogs](https://github.com/uragiristereo/Mejiboard/releases/tag/v1.4.0-alpha) • [direct](https://github.com/uragiristereo/Mejiboard/releases/download/v1.4.0-alpha/Mejiboard_v1.4.0-alpha.apk)

Check out [Releases](https://github.com/uragiristereo/Mejiboard/releases) section for more.

## Screenshots
<img src="https://github.com/uragiristereo/Mejiboard/raw/main/docs/screenshots.jpg">

## To-Dos
**Note: not in ordered list.**
- [x] Implement check for update
- [x] Implement custom video player controls
- [x] Migrate to ~~clean~~ android app architecture (in progress)
- [x] Change app icon
- [ ] Use staggered grid layout
- [ ] Use definitive progress bar for image loading
- [ ] Use Storage Access Framework (SAF)
- [ ] Join home screen with search screen
- [ ] Back press returns to previous browsed tags instead exiting
- [ ] Generate filename from tags
- [ ] Tweak double-tap-to-zoom behavior
- [ ] Implement search autocomplete filter
- [ ] Implement search guide
- [ ] Implement favourites
- [ ] Implement search history
- [ ] Implement tags filtering

## Libraries used
### Official
- [Jetpack Compose](https://developer.android.com/jetpack/compose): Android’s modern toolkit for building native UI. It simplifies and accelerates UI development on Android.
- [Hilt](https://developer.android.com/training/dependency-injection/hilt-android): A dependency injection library for Android that reduces the boilerplate of doing manual dependency injection in your project.
- [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel): The ViewModel class is designed to store and manage UI-related data in a lifecycle conscious way.
- [Room](https://developer.android.com/training/data-storage/room): A persistence library that provides an abstraction layer over SQLite to allow fluent database access while harnessing the full power of SQLite.
- [Proto DataStore](https://developer.android.com/topic/libraries/architecture/datastore#proto-datastore): A data storage solution that allows you to store typed objects with protocol buffers.
- [Kotlin Reflection](https://kotlinlang.org/docs/reflection.html): A set of language and library features that allows you to introspect the structure of your program at runtime.
- [kotlinx.serialization](https://kotlinlang.org/docs/serialization.html): Provides sets of libraries for all supported platforms – JVM, JavaScript, Native – and for various serialization formats – JSON, CBOR, protocol buffers, and others.
- [Firebase Crashlytics](https://firebase.google.com/docs/crashlytics): A lightweight, realtime crash reporter that helps you track, prioritize, and fix stability issues that erode your app quality.

### Open-source
- [OkHttp](https://github.com/square/okhttp): Square’s meticulous HTTP client for the JVM, Android, and GraalVM.
- [Retrofit](https://github.com/square/retrofit): A type-safe HTTP client for Android and the JVM.
- [Moshi](https://github.com/square/moshi): A modern JSON library for Kotlin and Java.
- [Coil](https://github.com/coil-kt/coil): Image loading for Android backed by Kotlin Coroutines.
- [Accompanist](https://github.com/google/accompanist): A collection of extension libraries for Jetpack Compose.
- [Timber](https://github.com/JakeWharton/timber): A logger with a small, extensible API which provides utility on top of Android's normal Log class.
- [ExoPlayer](https://github.com/google/ExoPlayer): An extensible media player for Android.
- [TouchImageView](https://github.com/MikeOrtiz/TouchImageView): Adds touch functionality to Android ImageView.
- [Material Motion for Jetpack Compose](https://github.com/fornewid/material-motion-compose): Jetpack Compose library for implementing [motion system](https://material.io/design/motion/the-motion-system.html) in Material Components for Android.

## License
    Copyright 2021 Agung Watanabe

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
