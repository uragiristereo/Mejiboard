# Mejiboard

image (イメージ) + board = メジboard (mejiboard)

An image board client based on [Gelbooru](https://gelbooru.com) for android, made from Jetpack Compose 🚀.

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
- Version: `v1.2.1-alpha.5`
- Date: 2021-12-16
- Links: [changelogs](https://github.com/uragiristereo/Mejiboard/releases/tag/v1.2.1-alpha.5) • [direct](https://github.com/uragiristereo/Mejiboard/releases/download/v1.2.1-alpha.5/Mejiboard_v1.2.1.alpha.5.apk)

Check out [Releases](https://github.com/uragiristereo/Mejiboard/releases) section for more.

## Screenshots
<img src="https://github.com/uragiristereo/Mejiboard/raw/alpha/screenshots/Screenshots_combined.jpg">

## To-Dos
**Note: not in ordered list.**
- [ ] Use staggered grid layout
- [ ] Use definitive progress bar for image loading
- [ ] Use Storage Access Framework (SAF)
- [ ] Change app icon
- [ ] Refactor the code
- [ ] Join home screen with search screen
- [ ] Back press returns to previous browsed tags instead exiting
- [ ] Generate filename from tags
- [ ] Tweak double-tap-to-zoom behavior
- [ ] Implement search autocomplete filter
- [ ] Implement search guide
- [ ] Implement custom video player controls
- [ ] Implement favourites
- [ ] Implement search history
- [ ] Implement tags blacklisting
- [ ] Implement check for update
- [ ] Enable safe listing toggle

## Libraries used
- [OkHttp](https://github.com/square/okhttp): Square’s meticulous HTTP client for the JVM, Android, and GraalVM.
- [Retrofit2](https://github.com/square/retrofit): A type-safe HTTP client for Android and the JVM
- [Hilt](https://developer.android.com/training/dependency-injection/hilt-android): A dependency injection library for Android that reduces the boilerplate of doing manual dependency injection in your project.
- [Coil](https://github.com/coil-kt/coil): Image loading for Android backed by Kotlin Coroutines.
- [Accompanist](https://github.com/google/accompanist): A collection of extension libraries for Jetpack Compose.
- [Timber](https://github.com/JakeWharton/timber): A logger with a small, extensible API which provides utility on top of Android's normal Log class.
- [ExoPlayer](https://github.com/google/ExoPlayer): An extensible media player for Android.
- [TouchImageView](https://github.com/MikeOrtiz/TouchImageView): Adds touch functionality to Android ImageView.
- [Material Motion for Jetpack Compose](https://github.com/fornewid/material-motion-compose): Creates intuitive animation and transition based on [Material Motion](https://material.io/design/motion/the-motion-system.html) guidelines.

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
