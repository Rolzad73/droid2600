#ifdef ANDROID

#include <unistd.h>
#include <jni.h>
#include <android/log.h>
#include "SDL_thread.h"
#include "SDL_main.h"

/* JNI-C wrapper stuff */

#ifdef __cplusplus
#define C_LINKAGE "C"
#else
#define C_LINKAGE
#endif

#ifndef SDL_JAVA_PACKAGE_PATH
#error You have to define SDL_JAVA_PACKAGE_PATH to your package path with dots replaced with underscores, for example "com_example_SanAngeles"
#endif
#define JAVA_EXPORT_NAME2(name,package) Java_##package##_##name
#define JAVA_EXPORT_NAME1(name,package) JAVA_EXPORT_NAME2(name,package)
#define JAVA_EXPORT_NAME(name) JAVA_EXPORT_NAME1(name,SDL_JAVA_PACKAGE_PATH)

extern C_LINKAGE void
JAVA_EXPORT_NAME(SDLInterface_nativeInit) ( JNIEnv*  env, jobject thiz, jobjectArray objArray )
{
    jint length = (*env)->GetArrayLength(env, objArray);
    char * argv[length];
    jstring stringArray[length];
    int i;

    for(i = 0; i < length; i++) {
        stringArray[i] = (jstring)(*env)->GetObjectArrayElement(env, objArray, i);
        argv[i] = (*env)->GetStringUTFChars(env, stringArray[i], NULL);
    }

    main( length, (char**)argv );

    for(i = 0; i < length; i++) {
        (*env)->ReleaseStringUTFChars(env, stringArray[i], argv[i]);
    }
};

#undef JAVA_EXPORT_NAME
#undef JAVA_EXPORT_NAME1
#undef JAVA_EXPORT_NAME2
#undef C_LINKAGE

#endif
