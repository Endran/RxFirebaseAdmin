[![Hex.pm](https://img.shields.io/hexpm/l/plug.svg)](http://www.apache.org/licenses/LICENSE-2.0)
[![Language](https://img.shields.io/badge/language-java-yellowgreen.svg)](https://www.google.nl/search?q=java)
[![Build Status](https://travis-ci.org/Endran/RxFirebaseAdmin.svg?branch=master)](https://travis-ci.org/Endran/RxFirebaseAdmin)
[![Coverage Status](https://coveralls.io/repos/github/Endran/RxFirebaseAdmin/badge.svg?branch=master)](https://coveralls.io/github/Endran/RxFirebaseAdmin?branch=master)
[![](https://jitpack.io/v/endran/RxFirebaseAdmin.svg)](https://jitpack.io/#endran/RxFirebaseAdmin)
# RxFirebaseAdmin

RxJava wrapper on Google's [Firebase for Java Admin](https://firebase.google.com/docs/admin/setup) library. Based upon the 
[RxJava wrapper](https://github.com/nmoskalenko/RxFirebase) on Google's [Firebase for Android](https://firebase.google.com/docs/android/setup) library. 
Also check out the Kotlin bindings at [RxFirebaseAdminKt](https://github.com/Endran/RxFirebaseAdminKt).

## Usage
This library provides set of methods to work with the Firebase Realtime Database using the Admin Java interface. 
Storage, Authentication or User is not supported by the Admin interface. The admin interface can always read and write, regardless of 
the Firebase rules.

### Database:
TBD

## Download

##### Gradle:

Add it in your root build.gradle at the end of repositories:
```groovy
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
Add the dependencies to the module build.gradle:
```groovy
dependencies {
    compile 'io.reactivex:rxjava:1.2.3'
    compile 'com.google.firebase:firebase-admin:4.0.3'
    compile 'com.github.endran:RxFirebaseAdmin:1.0' // Check the JitPack badge in top for the latest version info
}
```

##### Maven:
Add the JitPack repository to your build file
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```
Add the dependencies
```xml
<dependencies>
    <dependency>
        <groupId>io.reactivex</groupId>
        <artifactId>rxjava</artifactId>
        <version>1.2.3</version>
    </dependency>
    <dependency>
        <groupId>ccom.google.firebase</groupId>
        <artifactId>firebase-admin</artifactId>
        <version>4.0.3</version>
    </dependency>
    <dependency>
        <groupId>com.github.endran</groupId>
        <artifactId>RxFirebaseAdmin</artifactId>
        <version>1.0</version>
    </dependency>
</dependencies>
```

# License

    Copyright 2017 David Hardy
    Copyright 2016 Nickolay Moskalenko
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
        http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
