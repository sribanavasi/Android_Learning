# Platform architecture

Android is Open-source, linux-based software stack created for wide array of devices

![Alt text](https://developer.android.com/static/guide/platform/images/android-stack_2x.png)

## Linux Kernel
- The foundation of the Android platform.
- Contains all hardware device drivers.
- Manages power, devices such as camera, Bluetooth, display, audio, Wi-Fi, USB, sensors, etc.

## Hardware Abstraction Layer (HAL)
- Provides standard interfaces that expose hardware device capabilities to the high-level Java API.
- Consists of multiple library modules for different hardware components like camera, Bluetooth, audio, and sensors.

## Android RunTime (ART)
- Executes Dalvik Executable format (DEX) files compiled from Java DEX bytecode using build tools with a low/minimum memory footprint.
- Features Ahead of Time (AOT) and Just-In-Time compilation, as well as optimized garbage collection (GC).

## Core C/C++ Libraries
- Many system layers, such as HAL and ART, are built using C and C++ native libraries.

## Java API Framework
- Entire system features are exposed as Java APIs, used for building Android apps.
- Includes APIs for the view system, resource management, notification, location, and activity.

## System Apps
- System apps such as SMS, calendar, and internet browser are included in this layer.



