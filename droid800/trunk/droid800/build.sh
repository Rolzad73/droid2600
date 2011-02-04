#!/bin/bash

# build.sh
#
# Build script. Options: 
#     r build a release (clean, compile, sign)
#     cc only compile c sources
#     cj only compile java sources
#     t test build (compile, sign, install)
#     i install only
#     clean

export PATH=$PATH:~/soft/android-ndk
DROIDSDL_DIR=../droidSDL

function checkDroidSDLVersion() {
    REQUIRED_VERSION=`cat ./droidsdl.version`
    CHECK_STRING="android:versionName=\"$REQUIRED_VERSION\""
    OUTPUT=`grep "$CHECK_STRING" ../droidSDL/AndroidManifest.xml`
    if [ "$OUTPUT" == "" ]; then
        echo "Requires DroidSDL v$REQUIRED_VERSION"
        echo "Check that ./droidsdl.version and ../droidSDL/AndroidManifest.xml are set correctly"
        exit 1
    fi
}

function cleanProject() {
    rm -rf bin gen obj libs
}

function checkSdlLinks {
    # if the symlinks already exist then we are done
    if [ -h "./jni/sdl" ] \
            && [ -h "./jni/sdl_main" ] \
            && [ -h "./jni/sdl_blitpool" ] \
            && [ -h "./jni/stlport" ]; then
        echo "DroidSDL native sources already linked"
        return;
    fi 

    # test if the source files are where we expect them to be 
    # if not, we will print an error and exit
    if [ -d "$DROIDSDL_DIR/jni/sdl" ] \
            && [ -d "$DROIDSDL_DIR/jni/sdl_main" ] \
            && [ -d "$DROIDSDL_DIR/jni/sdl_blitpool" ] \
            && [ -d "$DROIDSDL_DIR/jni/stlport" ]; then
        ln -s "../$DROIDSDL_DIR/jni/sdl" ./jni/sdl
        ln -s "../$DROIDSDL_DIR/jni/sdl_main" ./jni/sdl_main
        ln -s "../$DROIDSDL_DIR/jni/sdl_blitpool" ./jni/sdl_blitpool
        ln -s "../$DROIDSDL_DIR/jni/stlport" ./jni/stlport
        echo "Created links to DroidSDL native sources"
    else
        echo "Cannot find droidSDL sources.. exit"
        exit -1
    fi
}

function ccompile {
    checkSdlLinks;
    ndk-build V=1
}

function jcompile {
    # work-around for bug in android development chain where the "libs" directory 
    # must exist in a library project folder.
    mkdir -p $DROIDSDL_DIR/libs
    ant release 
}

function signApp {
    rm bin/Droid800.apk
    jarsigner -verbose -keystore ../../droid2600.keystore bin/Droid800-unsigned.apk droid2600
    jarsigner -verify bin/Droid800-unsigned.apk
    zipalign -v 4 bin/Droid800-unsigned.apk bin/Droid800.apk
}

function installApp {
    #adb -d uninstall com.droid800
    adb -d install -r bin/Droid800.apk
}

function showHelp {
    echo "usage: build.sh <arg>, where <arg> is one of:"
    echo "    clean : clean all objects"
    echo "    r     : build a release (clean, compile, sign)"
    echo "    cc    : compile c sources"
    echo "    j     : compile java sources"
    echo "    t     : test build (clean, compile, sign, install)"
    echo "    i     : install apk"
    echo "    h     : show this message"
}

checkDroidSDLVersion;

case "$1" in
    clean)
        cleanProject;
        ;;
    r)
        cleanProject;
        ccompile;
        jcompile;
        signApp;
        ;;
    cc)
        ccompile;
        ;;
    j)
        jcompile;
        ;;
    a)
        ccompile;
        jcompile;
        signApp;
        ;;
    t)
        ccompile;
        jcompile;
        signApp;
        installApp;
        ;;
    i)
        installApp;
        ;;
    *)
        showHelp;
        ;;
esac

