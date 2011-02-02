LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

# The namespace in Java file, with dots replaced with underscores
#SDL_JAVA_PACKAGE_PATH := com_droid2600

LOCAL_ARM_MODE := arm

LOCAL_MODULE := application

APP_SUBDIRS := $(patsubst $(LOCAL_PATH)/%, %, $(shell find $(LOCAL_PATH)/stella/ -type d))

LOCAL_CFLAGS := $(foreach D, $(APP_SUBDIRS), -I$(LOCAL_PATH)/$(D)) \
				-I$(LOCAL_PATH)/../sdl/include \
				-I$(LOCAL_PATH)/../sdl_blitpool 

#LOCAL_CFLAGS += $(APPLICATION_ADDITIONAL_CFLAGS) -DHAVE_INTTYPES -DBSPF_UNIX -DSOUND_SUPPORT -DSDL_JAVA_PACKAGE_PATH=$(SDL_JAVA_PACKAGE_PATH)
LOCAL_CFLAGS += $(APPLICATION_ADDITIONAL_CFLAGS) -DHAVE_INTTYPES -DBSPF_UNIX -DSOUND_SUPPORT 

#Change C++ file extension as appropriate
LOCAL_CPP_EXTENSION := .cxx

LOCAL_SRC_FILES := $(foreach F, $(APP_SUBDIRS), $(addprefix $(F)/,$(notdir $(wildcard $(LOCAL_PATH)/$(F)/*.cxx))))
# Uncomment to also add C sources
LOCAL_SRC_FILES += $(foreach F, $(APP_SUBDIRS), $(addprefix $(F)/,$(notdir $(wildcard $(LOCAL_PATH)/$(F)/*.c))))

LOCAL_SHARED_LIBRARIES := sdl $(COMPILED_LIBRARIES)

#LOCAL_LDLIBS := -lGLESv1_CM -ldl -llog -lz -lstdc++
LOCAL_LDLIBS := -lGLESv1_CM -ldl -llog -lz 

LIBS_WITH_LONG_SYMBOLS := $(strip $(shell \
	for f in $(LOCAL_PATH)/../../libs/armeabi/*.so ; do \
		if echo $$f | grep "libapplication[.]so" > /dev/null ; then \
			continue ; \
		fi ; \
		if [ -e "$$f" ] ; then \
			if nm -g $$f | cut -c 12- | egrep '.{128}' > /dev/null ; then \
				echo $$f | grep -o 'lib[^/]*[.]so' ; \
			fi ; \
		fi ; \
	done \
) )

ifneq "$(LIBS_WITH_LONG_SYMBOLS)" ""
$(foreach F, $(LIBS_WITH_LONG_SYMBOLS), \
$(info Library $(F): abusing symbol names are: \
$(shell nm -g $(LOCAL_PATH)/../../libs/armeabi/$(F) | cut -c 12- | egrep '.{128}' ) ) \
$(info Library $(F) contains symbol names longer than 128 bytes, \
YOUR CODE WILL DEADLOCK WITHOUT ANY WARNING when you'll access such function - \
please make this library static to avoid problems. ) )
$(error Detected libraries with too long symbol names. Remove all files under project/libs/armeabi, make these libs static, and recompile)
endif

include $(BUILD_SHARED_LIBRARY)
