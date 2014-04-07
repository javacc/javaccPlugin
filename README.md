# JavaCC Compiler Plugin for Gradle 

Provides the ability to use [JavaCC](http://javacc.java.net/) via [Gradle](http://www.gradle.org/). If the 'java' plugin is also applied, JavaCompile tasks will depend upon the compileJavacc task.

## Installation

For now, you must build the plugin yourself and install it to your local maven repo. To build, simply run:
`gradle clean build install`

Then, add the following lines to your `build.gradle` script:

```groovy
buildscript {
    repositories {
        mavenLocal()
    }
    dependencies {
        classpath group: 'ca.coglinc', name: 'javaccPlugin', version: '1.0.0'
    }
}
apply plugin: 'javacc'
```

## Usage

Place your JavaCC code into `src/main/javacc`.
The generated Java code will be  put under `./build/generated/javacc` and will be compiled as part of the Java compile.

## Changelog

### 1.0.0

Initial version with limited features. Simply generates JavaCC files to Java from a non-configurable location into a non-configurable location.
