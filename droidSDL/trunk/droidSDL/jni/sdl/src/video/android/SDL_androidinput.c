/*
    SDL - Simple DirectMedia Layer
    Copyright (C) 1997-2009 Sam Lantinga

    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

    Sam Lantinga
    slouken@libsdl.org
*/
#include <jni.h>
#include <android/log.h>
#include <sys/time.h>
#include <time.h>
#include <stdint.h>
#include <math.h>
#include <string.h> // for memset()

#include "SDL_config.h"

#include "SDL_version.h"
#include "SDL_video.h"
#include "SDL_mouse.h"
#include "SDL_mutex.h"
#include "SDL_thread.h"
#include "../SDL_sysvideo.h"
#include "../SDL_pixels_c.h"
#include "SDL_events.h"
#if (SDL_VERSION_ATLEAST(1,3,0))
#include "../../events/SDL_events_c.h"
#include "../../events/SDL_keyboard_c.h"
#include "../../events/SDL_mouse_c.h"
#include "SDL_scancode.h"
#include "SDL_compat.h"
#else
#include "SDL_keysym.h"
#include "../../events/SDL_events_c.h"
#endif
#include "SDL_joystick.h"
#include "../../joystick/SDL_sysjoystick.h"
#include "../../joystick/SDL_joystick_c.h"

#include "SDL_androidvideo.h"

static SDLKey keymap[KEYCODE_LAST+1];

/* JNI-C++ wrapper stuff */

#ifndef SDL_JAVA_PACKAGE_PATH
#error You have to define SDL_JAVA_PACKAGE_PATH to your package path with dots replaced with underscores, for example "com_example_SanAngeles"
#endif
#define JAVA_EXPORT_NAME2(name,package) Java_##package##_##name
#define JAVA_EXPORT_NAME1(name,package) JAVA_EXPORT_NAME2(name,package)
#define JAVA_EXPORT_NAME(name) JAVA_EXPORT_NAME1(name,SDL_JAVA_PACKAGE_PATH)

#if SDL_VERSION_ATLEAST(1,3,0)

#define SDL_KEY2(X) SDL_SCANCODE_ ## X
#define SDL_KEY(X) SDL_KEY2(X)

static SDL_scancode TranslateKey(int scancode, SDL_keysym *keysym)
{
	if ( scancode >= SDL_arraysize(keymap) )
		scancode = KEYCODE_UNKNOWN;
//	return keymap[scancode];
//    SDL_KEY(scancode);
    return scancode;
}

static SDL_scancode GetKeysym(SDL_scancode scancode, SDL_keysym *keysym)
{
	return scancode;
}

#define SDL_SendKeyboardKey(X, Y) SDL_SendKeyboardKey(X, Y, SDL_FALSE)

#else

#define SDL_KEY2(X) SDLK_ ## X
#define SDL_KEY(X) SDL_KEY2(X)

#define SDL_SendKeyboardKey SDL_PrivateKeyboard

// Randomly redefining SDL 1.3 scancodes to SDL 1.2 keycodes
#define KP_0 KP0
#define KP_1 KP1
#define KP_2 KP2
#define KP_3 KP3
#define KP_4 KP4
#define KP_5 KP5
#define KP_6 KP6
#define KP_7 KP7
#define KP_8 KP8
#define KP_9 KP9
#define NUMLOCKCLEAR NUMLOCK
#define GRAVE DOLLAR
#define APOSTROPHE QUOTE
#define LGUI LMETA
// Overkill haha
#define A a
#define B b
#define C c
#define D d
#define E e
#define F f
#define G g
#define H h
#define I i
#define J j
#define K k
#define L l
#define M m
#define N n
#define O o
#define P p
#define Q q
#define R r
#define S s
#define T t
#define U u
#define V v
#define W w
#define X x
#define Y y
#define Z z

#define SDL_scancode SDLKey

static SDL_keysym *TranslateKey(int scancode, SDL_keysym *keysym)
{
//__android_log_print(ANDROID_LOG_INFO, "TranslateKey", "scancode=%i", scancode);
	/* Sanity check */
	if ( scancode >= SDL_arraysize(keymap) )
		scancode = KEYCODE_UNKNOWN;

	/* Set the keysym information */
	keysym->scancode = scancode;

    keysym->sym = /*keymap[scancode];*/scancode;

	keysym->mod = KMOD_NONE;

	/* If UNICODE is on, get the UNICODE value for the key */
	keysym->unicode = 0;
	if ( SDL_TranslateUNICODE ) {
		/* Populate the unicode field with the ASCII value */
		keysym->unicode = scancode;
	}
	return(keysym);
}

static SDL_keysym *GetKeysym(SDLKey scancode, SDL_keysym *keysym)
{
	/* Sanity check */

	/* Set the keysym information */
	keysym->scancode = scancode;
	keysym->sym = scancode;
	keysym->mod = KMOD_NONE;

	/* If UNICODE is on, get the UNICODE value for the key */
	keysym->unicode = 0;
	if ( SDL_TranslateUNICODE ) {
		/* Populate the unicode field with the ASCII value */
		keysym->unicode = scancode;
	}
	return(keysym);
}

#endif

#define SDL_KEY_VAL(X) X

static int isTrackballUsed = 0;
static int isMouseUsed = 0;
static int isJoystickUsed = 0;
static int isMultitouchUsed = 0;
static SDL_Joystick *CurrentJoysticks[4] = {NULL, NULL, NULL, NULL};

enum MOUSE_ACTION { MOUSE_DOWN = 0, MOUSE_UP=1, MOUSE_MOVE=2 };

JNIEXPORT void JNICALL
JAVA_EXPORT_NAME(SDLInterface_nativeMouse) ( JNIEnv*  env, jobject  thiz, jint x, jint y, jint action, jint pointerId, jint force, jint radius )
{
#ifdef ANDROID_OLD_MOUSE
#if SDL_VIDEO_RENDER_RESIZE
	// Translate mouse coordinates

#if SDL_VERSION_ATLEAST(1,3,0)
	SDL_Window * window = SDL_GetFocusWindow();
	if( window && window->renderer->window ) {
		x = x * window->w / window->display->desktop_mode.w;
		y = y * window->h / window->display->desktop_mode.h;
	}
#else
	x = x * SDL_ANDROID_sFakeWindowWidth / SDL_ANDROID_sWindowWidth;
	y = y * SDL_ANDROID_sFakeWindowHeight / SDL_ANDROID_sWindowHeight;
#endif

#endif

	if( isMultitouchUsed )
	{
		if(pointerId < 0)
			pointerId = 0;
		if(pointerId > 2)
			pointerId = 2;
		pointerId++;
		if( CurrentJoysticks[pointerId] )
		{
			SDL_PrivateJoystickAxis(CurrentJoysticks[0], 0, x);
			SDL_PrivateJoystickAxis(CurrentJoysticks[0], 1, y);
			SDL_PrivateJoystickAxis(CurrentJoysticks[0], 2, force);
			SDL_PrivateJoystickAxis(CurrentJoysticks[0], 3, radius);
		}
	}
	if( !isMouseUsed )
	{
		#ifndef SDL_ANDROID_KEYCODE_MOUSE
		#define SDL_ANDROID_KEYCODE_MOUSE RETURN
		#endif
		SDL_keysym keysym;
		if( action != MOUSE_MOVE && SDL_KEY(SDL_KEY_VAL(SDL_ANDROID_KEYCODE_MOUSE)) != SDL_KEY(UNKNOWN) )
			SDL_SendKeyboardKey( action == MOUSE_DOWN ? SDL_PRESSED : SDL_RELEASED, GetKeysym(SDL_KEY(SDL_KEY_VAL(SDL_ANDROID_KEYCODE_MOUSE)) ,&keysym) );
		return;
	}

	if( action == MOUSE_DOWN || action == MOUSE_UP )
	{
#if SDL_VERSION_ATLEAST(1,3,0)
		SDL_SendMouseMotion(NULL, 0, x, y);
		SDL_SendMouseButton(NULL, (action == MOUSE_DOWN) ? SDL_PRESSED : SDL_RELEASED, 1 );
#else
		SDL_PrivateMouseMotion(0, 0, x, y);
		SDL_PrivateMouseButton( (action == MOUSE_DOWN) ? SDL_PRESSED : SDL_RELEASED, 1, x, y );
#endif
	}
	if( action == MOUSE_MOVE )
#if SDL_VERSION_ATLEAST(1,3,0)
		SDL_SendMouseMotion(NULL, 0, x, y);
#else
		SDL_PrivateMouseMotion(0, 0, x, y);
#endif
#else // ANDROID_OLD_MOUSE        
        x = x * SDL_ANDROID_sFakeWindowWidth / SDL_ANDROID_sWindowWidth;
        y = y * SDL_ANDROID_sFakeWindowHeight / SDL_ANDROID_sWindowHeight;
        SDL_PrivateMouseMotion(0,0,x,y);
#endif // ANDROID_OLD_MOUSE
}

static int processAndroidTrackball(int key, int action);

JNIEXPORT void JNICALL
JAVA_EXPORT_NAME(SDLInterface_nativeKey) ( JNIEnv*  env, jobject thiz, jint key, jint action )
{
//__android_log_print(ANDROID_LOG_INFO, "nativeKey", "key=%i", key);
    SDL_keysym keysym;
    keysym.scancode = key;
    keysym.sym = key;
    keysym.mod = KMOD_NONE;
//    keysym.unicode = 0;
//    if ( SDL_TranslateUNICODE ) {
//        /* Populate the unicode field with the ASCII value */
        keysym.unicode = key;
//    }
    SDL_PrivateKeyboard(action ? SDL_PRESSED : SDL_RELEASED, &keysym);
}

JNIEXPORT void JNICALL
JAVA_EXPORT_NAME(SDLSurfaceView_nativeKey) ( JNIEnv*  env, jobject thiz, jint key, jint action )
{
	if( isTrackballUsed )
		if( processAndroidTrackball(key, action) )
			return;
	SDL_keysym keysym;
	SDL_SendKeyboardKey( action ? SDL_PRESSED : SDL_RELEASED, TranslateKey(key ,&keysym) );
}

JNIEXPORT void JNICALL
JAVA_EXPORT_NAME(Settings_nativeSetTrackballUsed) ( JNIEnv*  env, jobject thiz)
{
	isTrackballUsed = 1;
}

JNIEXPORT void JNICALL
JAVA_EXPORT_NAME(Settings_nativeSetMouseUsed) ( JNIEnv*  env, jobject thiz)
{
	isMouseUsed = 1;
}

JNIEXPORT void JNICALL
JAVA_EXPORT_NAME(Settings_nativeSetJoystickUsed) ( JNIEnv*  env, jobject thiz)
{
	isJoystickUsed = 1;
}

JNIEXPORT void JNICALL
JAVA_EXPORT_NAME(Settings_nativeSetMultitouchUsed) ( JNIEnv*  env, jobject thiz)
{
	isMultitouchUsed = 1;
}

void ANDROID_InitOSKeymap()
{
  int i;

#if (SDL_VERSION_ATLEAST(1,3,0))
  SDLKey defaultKeymap[SDL_NUM_SCANCODES];
  SDL_GetDefaultKeymap(defaultKeymap);
  SDL_SetKeymap(0, defaultKeymap, SDL_NUM_SCANCODES);
#endif

  // start by mapping all android keys to SDL_KEY(UNKNOWN)
  for (i=0; i<SDL_arraysize(keymap); ++i) {
      keymap[i] = SDL_KEY(UNKNOWN);
  }

    keymap[KEYCODE_DPAD_DOWN] = SDL_KEY(KP_2); // android dpad down to SDL keypad 2
    keymap[KEYCODE_DPAD_LEFT] = SDL_KEY(KP_4); // android dpad left to SDL keypad 4
    keymap[KEYCODE_DPAD_RIGHT] = SDL_KEY(KP_6); // android dpad right to SDL keypad 6
    keymap[KEYCODE_DPAD_UP] = SDL_KEY(KP_8); // android dpad up to SDL keypad 8
    keymap[KEYCODE_ALT_LEFT] = SDL_KEY(RCTRL); // android left alt to SDL right ctrl
    keymap[KEYCODE_SEARCH] = SDL_KEY(F4); // android 'SEARCH' to SDL F4
    keymap[KEYCODE_FOCUS] = SDL_KEY(F5); // android 'FOCUS' to SDL F5
    keymap[KEYCODE_BACK] = SDL_KEY(F9); // android 'BACK' to SDL F9
    keymap[KEYCODE_SPACE] = SDL_KEY(SPACE); // android 'SPACE' to SDL SPACE
}

int processAndroidTrackball(int key, int action)
{
	static int leftPressed = 0, rightPressed = 0, upPressed = 0, downPressed = 0;
	SDL_keysym keysym;

	if( ! action && (
		key == KEYCODE_DPAD_UP ||
		key == KEYCODE_DPAD_DOWN ||
		key == KEYCODE_DPAD_LEFT ||
		key == KEYCODE_DPAD_RIGHT ) )
		return 1;

	if( key == KEYCODE_DPAD_UP )
	{
		if( downPressed )
		{
			downPressed = 0;
			SDL_SendKeyboardKey( SDL_RELEASED, TranslateKey(KEYCODE_DPAD_DOWN ,&keysym) );
			return 1;
		}
		if( !upPressed )
		{
			upPressed = 1;
			SDL_SendKeyboardKey( SDL_PRESSED, TranslateKey(key ,&keysym) );
		}
		else
		{
			SDL_SendKeyboardKey( SDL_RELEASED, TranslateKey(key ,&keysym) );
			SDL_SendKeyboardKey( SDL_PRESSED, TranslateKey(key ,&keysym) );
		}
		return 1;
	}

	if( key == KEYCODE_DPAD_DOWN )
	{
		if( upPressed )
		{
			upPressed = 0;
			SDL_SendKeyboardKey( SDL_RELEASED, TranslateKey(KEYCODE_DPAD_UP ,&keysym) );
			return 1;
		}
		if( !upPressed )
		{
			downPressed = 1;
			SDL_SendKeyboardKey( SDL_PRESSED, TranslateKey(key ,&keysym) );
		}
		else
		{
			SDL_SendKeyboardKey( SDL_RELEASED, TranslateKey(key ,&keysym) );
			SDL_SendKeyboardKey( SDL_PRESSED, TranslateKey(key ,&keysym) );
		}
		return 1;
	}

	if( key == KEYCODE_DPAD_LEFT )
	{
		if( rightPressed )
		{
			rightPressed = 0;
			SDL_SendKeyboardKey( SDL_RELEASED, TranslateKey(KEYCODE_DPAD_RIGHT ,&keysym) );
			return 1;
		}
		if( !leftPressed )
		{
			leftPressed = 1;
			SDL_SendKeyboardKey( SDL_PRESSED, TranslateKey(key ,&keysym) );
		}
		else
		{
			SDL_SendKeyboardKey( SDL_RELEASED, TranslateKey(key ,&keysym) );
			SDL_SendKeyboardKey( SDL_PRESSED, TranslateKey(key ,&keysym) );
		}
		return 1;
	}

	if( key == KEYCODE_DPAD_RIGHT )
	{
		if( leftPressed )
		{
			leftPressed = 0;
			SDL_SendKeyboardKey( SDL_RELEASED, TranslateKey(KEYCODE_DPAD_LEFT ,&keysym) );
			return 1;
		}
		if( !rightPressed )
		{
			rightPressed = 1;
			SDL_SendKeyboardKey( SDL_PRESSED, TranslateKey(key ,&keysym) );
		}
		else
		{
			SDL_SendKeyboardKey( SDL_RELEASED, TranslateKey(key ,&keysym) );
			SDL_SendKeyboardKey( SDL_PRESSED, TranslateKey(key ,&keysym) );
		}
		return 1;
	}

	return 0;
}

int SDL_SYS_JoystickInit(void)
{
	SDL_numjoysticks = 4;
	return(0);
}

/* Function to get the device-dependent name of a joystick */
const char *SDL_SYS_JoystickName(int index)
{
	if(index)
		return("Android multitouch");
	return("Android accelerometer/orientation sensor");
}

/* Function to open a joystick for use.
   The joystick to open is specified by the index field of the joystick.
   This should fill the nbuttons and naxes fields of the joystick structure.
   It returns 0, or -1 if there is an error.
 */
int SDL_SYS_JoystickOpen(SDL_Joystick *joystick)
{
	joystick->nbuttons = 0; // Ignored
	joystick->nhats = 0;
	joystick->nballs = 0;
	if( joystick->index == 0 )
		joystick->naxes = 3;
	else
		joystick->naxes = 4;
	CurrentJoysticks[joystick->index] = joystick;
	return(0);
}

/* Function to update the state of a joystick - called as a device poll.
 * This function shouldn't update the joystick structure directly,
 * but instead should call SDL_PrivateJoystick*() to deliver events
 * and update joystick device state.
 */
void SDL_SYS_JoystickUpdate(SDL_Joystick *joystick)
{
	return;
}

/* Function to close a joystick after use */
void SDL_SYS_JoystickClose(SDL_Joystick *joystick)
{
	CurrentJoysticks[joystick->index] = NULL;
	return;
}

/* Function to perform any system-specific joystick related cleanup */
void SDL_SYS_JoystickQuit(void)
{
	int i;
	for(i=0; i<4; i++)
		CurrentJoysticks[i] = NULL;
	return;
}
