# [deprecated] HolyCycle

[![Build Status](https://travis-ci.org/restorer/holycycle.svg?branch=master)](https://travis-ci.org/restorer/holycycle) [![Javadoc](https://img.shields.io/badge/javadoc-0.1.0-blue.svg)](https://restorer.github.io/holycycle/javadoc) [![MIT License](https://img.shields.io/badge/license-MIT-blue.svg?style=flat)](LICENSE.txt)

![](docs/logo.png)

## Usage

**Step 1.** Add the JitPack repository to your build file:

```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

**Step 2.** Add the dependency:

```
dependencies {
    implementation 'com.github.restorer:holycycle:0.2.1@aar'
}
```

## About the library

This library is an abstraction over Activity or Fragment lifecycle, which makes it more straightforward and consistent.

```
↓ onControllerCreate(Bundle extras)
? onControllerRestoreInstanceState(Bundle savedInstanceState)
    ↓ onControllerGetContentLayoutId()
    ↓ onControllerContentViewCreated()
        ↓ onControllerStart()
            ↓ onControllerResume()
                ↓ onControllerFocus()
                ↓ onControllerBlur()
            ↓ onControllerPause()
            ↓ onControllerPersistUserData()
        ↓ onControllerStop()
        ? onControllerSaveInstanceState(Bundle outState)
```

This is similar to the standard lifecycle callbacks, but has several advantages:

1. Workaround for `onSaveInstanceState()` - library guarantees that `onControllerBlur()`, `onControllerPause()`, `onControllerPersistUserData()` and `onControllerStop()` will be called prior to `onControllerSaveInstanceState()`. Say goodbye to `java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState` :smiley:.
2. The same methods for Activity and Fragment (unless you use `setRetainInstance(true)`, but please don't use it).
3. `onControllerFocus()` / `onControllerBlur()` out of the box, and perfectly synced with `onControllerResume()` / `onControllerPause()` (vs `onWindowFocusChanged()`).
4. `getView()` for Activity (just like Fragment).
5. `findViewById()` for Fragment (just like Activity).
