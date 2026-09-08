<div align="center">

# Minecraft Stronghold Calculator

_An Android app that assists users in finding a stronghold in Minecraft._

[Download APK](#try-it) • [Demo Video](#demo-video) • [Version History](#version-history)

Locating a stronghold requires throwing Eyes of Ender to determine the direction of the stronghold. Eyes are difficult to obtain and have a chance of breaking each throw, making inefficient triangulation costly. Many players also find difficulty in pin-pointing the exact location of a stronghold without using coordinates. 

![preview](./assets/AppScreenshots.png)

</div>

## Features

- Add/Remove eye throws individually
- Calculates stronghold location from multiple Eye throws
- Interactive map displaying throw vectors and calculated intersection
- Guided tutorial for new users
- Customizable app themes

## How It Works

Users log each eye throw with the position of the in game character and the angle of their crosshair. App visualizes each eye throw in the interactive map.

When multiple throws are entered, the app calculates where the
throws intersect. Predicted stronghold location is visualized in the interactive map and prediction card. 

Users can remove eye throws if mis-inputted or they wish to calculate the location if a new stronghold. 

[Mathematical Approach](#mathematical-approach)

## Demo Video

[![Demo Video](https://img.youtube.com/vi/A3bWUoZF3jc/maxresdefault.jpg)](https://www.youtube.com/watch?v=A3bWUoZF3jc)

## Tech Stack

- Kotlin
- Jetpack Compose
- Android SDK
- Material 3

## Mathematical Approach

WIP fam

## Future Improvements

- Add error/uncertainty visualization
- Support for 3+ throws using linear regression
- Publish to Google Play

## Try it

[Download APK]

## Building From Source

### Requirements

- Android Studio
- Android SDK
- Kotlin

### Installation

1. Clone the repository
2. Open the project in Android Studio
3. Sync Gradle
4. Build and run on an emulator or Android device

## Version History

Evolution of the app starting from a blank Android Studio Project

### Initial Prototype

<img src="./assets/SH_Calc_V1.gif" width="300">

### Added Interactive Map

<img src="./assets/SH_Calc_V2.gif" width="300">

### Added Pages, Cleaner Jetpack Compose UI

<img src="./assets/SH_Calc_V3.png" width="300">

### Updated Color Scheme, Polished UI

<img src="./assets/SH_Calculator_V4.gif" width="300">

