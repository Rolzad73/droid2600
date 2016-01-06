
# use arm instruction set, not thumb
LOCAL_ARM_MODE := arm

# The namespace in Java file, with dots replaced with underscores
SDL_JAVA_PACKAGE_PATH := com_tvi910_android_sdl

# Path to shared libraries - Android 1.6 cannot load them properly, thus we have to specify absolute path here
# SDL_SHARED_LIBRARIES_PATH := /data/data/de.schwardtnet.alienblaster/lib

# Path to files with application data - they should be downloaded from Internet on first app run inside
# Java sources, or unpacked from resources (TODO)
# Typically /sdcard/alienblaster
# Or /data/data/de.schwardtnet.alienblaster/files if you're planning to unpack data in application private folder
# Your application will just set current directory there
SDL_CURDIR_PATH := com.tvi910.android.sdl

# Android Dev Phone G1 has trackball instead of cursor keys, and
# sends trackball movement events as rapid KeyDown/KeyUp events,
# this will make Up/Down/Left/Right key up events with X frames delay,
# so if application expects you to press and hold button it will process the event correctly.
# TODO: create a libsdl config file for that option and for key mapping/on-screen keyboard
SDL_TRACKBALL_KEYUP_DELAY := 1

# If the application designed for higher screen resolution enable this to get the screen
# resized in HW-accelerated way, however it eats a tiny bit of CPU
SDL_VIDEO_RENDER_RESIZE := 1

#COMPILED_LIBRARIES := sdl_mixer sdl_image

#APPLICATION_ADDITIONAL_CFLAGS := -finline-functions -DSOUND_SUPPORT -DANDROID -DDISPLAY_OPENGL
#APPLICATION_ADDITIONAL_CFLAGS := -finline-functions -DSOUND_SUPPORT -DANDROID -DANDROID_PROFILE
APPLICATION_ADDITIONAL_CFLAGS := -finline-functions -DANDROID -DUNIX
SDL_ADDITIONAL_CFLAGS := -DSDL_ANDROID_KEYCODE_MOUSE=UNKNOWN

# If SDL_Mixer should link to libMAD
SDL_MIXER_USE_LIBMAD :=
ifneq ($(strip $(filter mad, $(COMPILED_LIBRARIES))),)
SDL_MIXER_USE_LIBMAD := 1
endif

include $(call all-subdir-makefiles)
