//============================================================================
//
//   SSSS    tt          lll  lll       
//  SS  SS   tt           ll   ll        
//  SS     tttttt  eeee   ll   ll   aaaa 
//   SSSS    tt   ee  ee  ll   ll      aa
//      SS   tt   eeeeee  ll   ll   aaaaa  --  "An Atari 2600 VCS Emulator"
//  SS  SS   tt   ee      ll   ll  aa  aa
//   SSSS     ttt  eeeee llll llll  aaaaa
//
// Copyright (c) 1995-2010 by Bradford W. Mott, Stephen Anthony
// and the Stella Team
//
// See the file "License.txt" for information on usage and redistribution of
// this file, and for a DISCLAIMER OF ALL WARRANTIES.
//
// $Id: Event.hxx,v 1.2 2010-12-06 07:50:29 cvs Exp $
//============================================================================

#ifndef EVENT_HXX
#define EVENT_HXX

#include "bspf.hxx"

class Event;

/**
  @author  Bradford W. Mott
  @version $Id: Event.hxx,v 1.2 2010-12-06 07:50:29 cvs Exp $
*/
class Event
{
  public:
    /**
      Enumeration of all possible events in Stella, including both
      console and controller event types as well as events that aren't
      technically part of the emulation core
    */
    enum Type
    {
      NoType,
      ConsoleOn, ConsoleOff, ConsoleColor, ConsoleBlackWhite,
      ConsoleLeftDifficultyA, ConsoleLeftDifficultyB,
      ConsoleRightDifficultyA, ConsoleRightDifficultyB,
      ConsoleSelect, ConsoleReset,

      JoystickZeroUp, JoystickZeroDown, JoystickZeroLeft, JoystickZeroRight,
        JoystickZeroFire1, JoystickZeroFire2, JoystickZeroFire3,
      JoystickOneUp, JoystickOneDown, JoystickOneLeft, JoystickOneRight,
        JoystickOneFire1, JoystickOneFire2, JoystickOneFire3,

      PaddleZeroDecrease, PaddleZeroIncrease, PaddleZeroAnalog, PaddleZeroFire,
      PaddleOneDecrease, PaddleOneIncrease, PaddleOneAnalog, PaddleOneFire,
      PaddleTwoDecrease, PaddleTwoIncrease, PaddleTwoAnalog, PaddleTwoFire,
      PaddleThreeDecrease, PaddleThreeIncrease, PaddleThreeAnalog, PaddleThreeFire,

      KeyboardZero1, KeyboardZero2, KeyboardZero3,
      KeyboardZero4, KeyboardZero5, KeyboardZero6,
      KeyboardZero7, KeyboardZero8, KeyboardZero9,
      KeyboardZeroStar, KeyboardZero0, KeyboardZeroPound,

      KeyboardOne1, KeyboardOne2, KeyboardOne3,
      KeyboardOne4, KeyboardOne5, KeyboardOne6,
      KeyboardOne7, KeyboardOne8, KeyboardOne9,
      KeyboardOneStar, KeyboardOne0, KeyboardOnePound,

      Combo1, Combo2, Combo3, Combo4, Combo5, Combo6, Combo7, Combo8,
      Combo9, Combo10, Combo11, Combo12, Combo13, Combo14, Combo15, Combo16,
  
      SALeftAxis0Value, SALeftAxis1Value,
      SARightAxis0Value, SARightAxis1Value,

      MouseAxisXValue, MouseAxisYValue, MouseButtonValue,

      ChangeState, LoadState, SaveState, TakeSnapshot, Quit,
      PauseMode, MenuMode, CmdMenuMode, DebuggerMode, LauncherMode,
      Fry, VolumeDecrease, VolumeIncrease,

      UIUp, UIDown, UILeft, UIRight, UIHome, UIEnd, UIPgUp, UIPgDown,
      UISelect, UINavPrev, UINavNext, UIOK, UICancel, UIPrevDir,

      ReloadRom,

      LastType
    };

  public:
    /**
      Create a new event object
    */
    Event();
 
    /**
      Destructor
    */
    virtual ~Event();

  public:
    /**
      Get the value associated with the event of the specified type
    */
    virtual Int32 get(Type type) const;

    /**
      Set the value associated with the event of the specified type
    */
    virtual void set(Type type, Int32 value);

    /**
      Clears the event array (resets to initial state)
    */
    virtual void clear();

  protected:
    // Number of event types there are
    const Int32 myNumberOfTypes;

    // Array of values associated with each event type
    Int32 myValues[LastType];
};

#endif
