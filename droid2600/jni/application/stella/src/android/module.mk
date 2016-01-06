MODULE := src/android

MODULE_OBJS := \
	src/android/OSystemUNIX.o 

MODULE_DIRS += \
	src/android

# Include common rules 
include $(srcdir)/common.rules
