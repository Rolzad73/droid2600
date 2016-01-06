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
// $Id: OSystemAndroid.hxx,v 1.2 2010-12-08 16:48:05 cvs Exp $
//============================================================================

#ifndef OSYSTEM_ANDROID_HXX
#define OSYSTEM_ANDROID_HXX

#include "bspf.hxx"

/**
  This class defines Android specific system settings.
*/
class OSystemAndroid : public OSystem
{
  public:
    /**
      Create a new Android-specific operating system object.
    */
    OSystemAndroid(const char * baseDir, const char * configFile, int keepRatio);

    /**
      Destructor
    */
    virtual ~OSystemAndroid();
};

#endif
