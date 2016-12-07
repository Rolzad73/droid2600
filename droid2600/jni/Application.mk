APP_PROJECT_PATH := $(call my-dir)/..

# Available libraries: mad sdl_mixer sdl_image sdl_ttf sdl_net sdl_blitpool
# sdl_mixer depends on tremor and optionally mad
# sdl_image depends on png and jpeg
# sdl_ttf depends on freetype
APP_STL := stlport_static

APP_OPTIM := release
#APP_OPTIM := debug
APP_MODULES := application sdl sdl_main 

#APP_ABI := armeabi armeabi-v7a
#APP_ABI := armeabi-v7a
#APP_ABI := armeabi
APP_ABI := all

