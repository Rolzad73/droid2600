# Introduction #

This is the online manual for the Droid2600 Atari 2600 VCS emulator. Droid2600 is an Android port and custom front-end, for the popular Stella Atari VCS emulator.

# What is an Emulator? #

In this case, an emulator is an application that allows you to run games written for the Atari 2600 on your Android device. Your Android phone doesn't have a cartridge slot (duh!), instead it plays copies of original Atari cartridges called _roms_.

For a more general discussion of emulators and roms, I refer you to the following wikipedia articles on <a href='http://en.wikipedia.org/wiki/Emulator'>emulation</a> and <a href='http://en.wikipedia.org/wiki/ROM_image'>rom images</a>.

# Getting Started #

Droid2600 is an _emulator_; before you can play games you need to transfer _game roms_ to your Android device. It is recommended, but not required, that game roms be placed in the "/sdcard/Droid2600" directory on your SD card. For directions on how to download game roms, skip to the Loading\_Roms section below.

# Home Screen #

The application opens to the home screen on startup:

![http://droid2600.googlecode.com/files/homescreen.png](http://droid2600.googlecode.com/files/homescreen.png)

The home screen contains the following elements:

  * A marquee showing the currently selected rom file
  * A "Select ROM" button
  * A "PLAY!" button

The marquee the home screen shows some information about currently selected rom file - typically the original game title and the year the game was published. The field is not editable - to select a different rom file, click the "Select ROM" button. Once you have selected the game you want to play, press the "PLAY!" button to start the game.

# Console Controls #

All switches on the atari console are emulated with the exception of the TV Type switch.


| Atari Switch	| Android Device |
|:-------------|:---------------|
| Reset	       | Camera Button  |
| Select	      | Search Button  |
| Left Player Difficulty A | Y              |
| Left Player Difficulty B | U              |
| Right Player Difficulty A | I              |
| Right Player Difficulty B | O              |
| Color / BW   | N/A            |

The Reset and Select switches are also available as on-screen controls during game play. See the "On Screen Controls" section.

# Game Controls #

There are several methods of directional control available:

  * On screen controls: Directional control is performed by using the touch screen - guides are printed on screen.
  * Hardware DPad: If the device has a hardware DPad (example: Original Motorola Droid), it can be used for directional control
  * Accelerometer: Directional control is performed by tilting the device. Note: the accelerometer joystick is at rest when the device is tilted slightly before you
  * Mapped keys: Hardware keys can be mappped for directional control.

## On Screen Controls ##

The screen is divided into 3 areas:

![http://droid2600.googlecode.com/files/onscreencontrols.1.png](http://droid2600.googlecode.com/files/onscreencontrols.1.png)

  * Area 1 contains the on-screen d-pad. A guide is displayed in this area to show the location of the d-pad axis. Touching this section of the screen causes the virtual joystick to be moved in a direction relative to the d-pad axis. This area can be configured to occupy either the left half or the right half of the screen.
  * Area 2 is the trigger area. Touching the screen anywhere in this area causes the trigger button to be pressed. This area can be configured to occupy either the left hand or the right hand part of the screen.
  * Area 3 contains the start and reset buttons.

## Hardware D-Pad ##

Some devices, like the original Motorola Droid, have hardware d-pads. Hardware d-pads are enabled by default and can be used along with any other control.

## Accelerometer ##

The device's accelerometer can be used for directional control, that is - tilting the device produces up movement. Note that when the accelerometer joystick is enabled, the joystick is "at rest" when the device is tilted slightly toward you.

| Atari Joystick | Android Device |
|:---------------|:---------------|
| Left	          | Tilt device left |
| Right          | Tilt device right |
| Up             | Tilt device foreward (away from you) |
| Down	          | Tilt device backwards (toward you) |
| Fire	          | Menu Button    |

## Mapped Keys ##

Directional controls and the joystick trigger can be mapped to any available hardware key. See the "Mapping Keys" section for details.

# Preferences #

The preferences screen is accessed by pressing the Android "menu" key while on the home screen.

## Audio Settings ##

  * Sound: Enable or disable sound output.
  * Max Volume: Limit the volume of the emulator. If the current volume is greater than Max, the volume will be reduced during game play.

## Display Settings ##

  * Skip Frames: Skip frames in order to maintain full emulation speed. By default the emulator attempts 60 frames per second which is not attainable on most devices. Set the skip rate to 25% or 50% to enable full speed emulation
  * Video Timing: Determines how main loop implements delay. Best to stick with the default setting

## Game Controls ##

  * Acceleromter: Enalbe or disable the accelerometer joystick
  * On Screen Controls: Enable or disable the on screen controls
  * On Screen Controls Layout: Decide where the on screen d-pad will be displayed (Bottom Right, Top Right, Bottom Left, Top Left
  * On Screen Controls Size: Select the size of the on sceen d-pad (smallest, small, medium, or large)
  * :

## Key Mapping ##

Some emulator actions can be mapped to android hardware keys. The default set of mapped keys:

  * Android camera key -> Atari reset
  * Android search key -> Atari game select
  * Android "Y" key -> P0 Difficulty to A
  * Android "U" key -> P0 Difficulty to B
  * Android "I" key -> P1 Difficulty to A
  * Android "O" key -> P1 Difficulty to B
  * Android DPad UP -> Up
  * Android DPad Down -> Down
  * Android DPad Left -> Left
  * Android DPad right -> Right
  * Android Menu key -> Fire button
  * Android "S" key -> Save game state
  * Android "C" key -> Change game state
  * Android "L" key -> Load game state

Alternate mappings can be configured by opening the preferences menu item, and selecting an action from the list. A dialog will open prompting you to select the key you'd like to map to the action. Multiple keys can be slected.

Note: Only physical keyboards and buttons can be mapped.

