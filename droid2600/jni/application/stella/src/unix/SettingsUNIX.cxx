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
// $Id: SettingsUNIX.cxx,v 1.1.1.1 2010-10-26 03:44:58 cvs Exp $
//============================================================================

#include "bspf.hxx"
#include "Settings.hxx"
#include "SettingsUNIX.hxx"

// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
SettingsUNIX::SettingsUNIX(OSystem* osystem)
  : Settings(osystem)
{
  setInternal("gl_lib", "libGL.so");
  // Most Linux GL implementations don't support this yet
  setInternal("gl_vsync", "false");
}

// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
SettingsUNIX::~SettingsUNIX()
{
}
