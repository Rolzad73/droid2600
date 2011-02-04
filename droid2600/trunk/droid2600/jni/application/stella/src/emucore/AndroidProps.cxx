#ifdef ANDROID

#include "bspf.hxx"

#include "DefProps.hxx"
#include "Props.hxx"

#include <jni.h>
#include <android/log.h>

/* JNI-C++ wrapper stuff */

#ifdef __cplusplus
#define C_LINKAGE "C"
#else
#define C_LINKAGE
#endif

//#ifndef SDL_JAVA_PACKAGE_PATH
//#error You have to define SDL_JAVA_PACKAGE_PATH to your package path with dots replaced with underscores, for example "com_example_SanAngeles"
//#endif

#define JAVA_EXPORT_NAME2(name,package) Java_##package##_##name
#define JAVA_EXPORT_NAME1(name,package) JAVA_EXPORT_NAME2(name,package)
#define JAVA_EXPORT_NAME(name) JAVA_EXPORT_NAME1(name,com_droid2600)

extern C_LINKAGE jstring
//JNIEXPORT jstring JNICALL
JAVA_EXPORT_NAME(StellaMetadataSource_nativeLookupCartTitle) (
        JNIEnv*  env, 
        jobject thiz,
        jstring md5)
{
    jboolean isCopy;
    const char * str = env->GetStringUTFChars(md5, &isCopy);
    __android_log_print(ANDROID_LOG_INFO, "nativeLookupCartTitle", "value=%s", str);

    Properties properties;
    properties.setDefaults();

    // find the default property.
    bool found = false;
    int low = 0, high = DEF_PROPS_SIZE - 1;
    while(low <= high)
    {
      int i = (low + high) / 2;
      int cmp = strncmp(str, DefProps[i][Cartridge_MD5], 32);

      if(cmp == 0)  // found it
      {
        for(int p = 0; p < LastPropType; ++p)
          if(DefProps[i][p][0] != 0)
            properties.set((PropertyType)p, DefProps[i][p]);

        found = true;
        break;
      }
      else if(cmp < 0)
        high = i - 1; // look at lower range
      else
        low = i + 1;  // look at upper range
    }
 
    env->ReleaseStringUTFChars(md5, str);

    if (found == true) {
        const std::string cartName = properties.get(Cartridge_Name);
        __android_log_print(ANDROID_LOG_INFO, "nativeLookupCartTitle", "cartName=%s", cartName.c_str());
        jstring newString = env->NewStringUTF(cartName.c_str());
            return newString;
    }
    else {
        return NULL;
    }
}

#undef JAVA_EXPORT_NAME
#undef JAVA_EXPORT_NAME1
#undef JAVA_EXPORT_NAME2
#undef C_LINKAGE

#endif // ANDROID
