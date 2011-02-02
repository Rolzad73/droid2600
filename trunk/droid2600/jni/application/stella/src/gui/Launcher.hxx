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
// $Id: Launcher.hxx,v 1.1.1.1 2010-10-26 03:44:58 cvs Exp $
//============================================================================

#ifndef LAUNCHER_HXX
#define LAUNCHER_HXX

class Properties;
class OSystem;

#include "DialogContainer.hxx"
#include "FrameBuffer.hxx"

/**
  The base dialog for the ROM launcher in Stella.

  @author  Stephen Anthony
  @version $Id: Launcher.hxx,v 1.1.1.1 2010-10-26 03:44:58 cvs Exp $
*/
class Launcher : public DialogContainer
{
  public:
    /**
      Create a new menu stack
    */
    Launcher(OSystem* osystem);

    /**
      Destructor
    */
    virtual ~Launcher();

    /**
      Initialize the video subsystem wrt this class.
    */
    FBInitStatus initializeVideo();

    /**
      Wrapper for LauncherDialog::selectedRomMD5() method.
    */
    string selectedRomMD5();

  private:
    // The width and height of this dialog
    uInt32 myWidth;
    uInt32 myHeight;
};

#endif
