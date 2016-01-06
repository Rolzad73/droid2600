LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_ARM_MODE := arm

LOCAL_MODULE := stlport

LOCAL_CFLAGS := -I$(LOCAL_PATH)/stlport -I$(LOCAL_PATH)/src

LOCAL_CPP_EXTENSION := .cpp

LOCAL_SRC_FILES := $(addprefix src/,$(notdir $(wildcard $(LOCAL_PATH)/src/*.c $(LOCAL_PATH)/src/*.cpp )))

#LOCAL_LDLIBS := -lstdc++

include $(BUILD_STATIC_LIBRARY)

