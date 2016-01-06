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
// $Id: CartFE.cxx,v 1.1.1.1 2010-10-26 03:44:58 cvs Exp $
//============================================================================

#include <cassert>
#include <cstring>

#include "System.hxx"
#include "CartFE.hxx"

// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
CartridgeFE::CartridgeFE(const uInt8* image)
  : myLastAddress1(0),
    myLastAddress2(0),
    myLastAddressChanged(false)
{
  // Copy the ROM image into my buffer
  memcpy(myImage, image, 8192);
}

// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
CartridgeFE::~CartridgeFE()
{
}

// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
void CartridgeFE::reset()
{
}

// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
void CartridgeFE::install(System& system)
{
  mySystem = &system;
  uInt16 shift = mySystem->pageShift();
  uInt16 mask = mySystem->pageMask();

  // Make sure the system we're being installed in has a page size that'll work
  assert((0x1000 & mask) == 0);

  // Map all of the accesses to call peek and poke
  System::PageAccess access;
  access.directPeekBase = 0;
  access.directPokeBase = 0;
  access.device = this;
  access.type = System::PA_READ;
  for(uInt32 i = 0x1000; i < 0x2000; i += (1 << shift))
    mySystem->setPageAccess(i >> shift, access);
}

// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
uInt8 CartridgeFE::peek(uInt16 address)
{
  // The bank is determined by A13 of the processor
  // We keep track of the two most recent accesses to determine which bank
  // we're in, and when the values actually changed
  myLastAddress2 = myLastAddress1;
  myLastAddress1 = address;
  myLastAddressChanged = true;

  return myImage[(address & 0x0FFF) + (((address & 0x2000) == 0) ? 4096 : 0)];
}

// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
bool CartridgeFE::poke(uInt16, uInt8)
{
  return false;
}

// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
void CartridgeFE::bank(uInt16 b)
{
  // Doesn't support bankswitching in the normal sense
}

// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
uInt16 CartridgeFE::bank() const
{
  // The current bank depends on the last address accessed
  return ((myLastAddress1 & 0x2000) == 0) ? 1 : 0;
}

// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
uInt16 CartridgeFE::bankCount() const
{
  return 2;
}

// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
bool CartridgeFE::bankChanged()
{
  if(myLastAddressChanged)
  {
    // A bankswitch occurs when the addresses transition from state to another
    bool a1 = ((myLastAddress1 & 0x2000) == 0),
         a2 = ((myLastAddress2 & 0x2000) == 0);
    myBankChanged = (a1 && !a2) || (a2 && !a1);
    myLastAddressChanged = false;
  }
  else
  {
    myBankChanged = false;
  }

  // In any event, let the base class know about it
  return Cartridge::bankChanged();
}


// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
bool CartridgeFE::patch(uInt16 address, uInt8 value)
{
  myImage[(address & 0x0FFF) + (((address & 0x2000) == 0) ? 4096 : 0)] = value;
  return myBankChanged = true;
} 

// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
const uInt8* CartridgeFE::getImage(int& size) const
{
  size = 8192;
  return myImage;
}

// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
bool CartridgeFE::save(Serializer& out) const
{
//   try
//   {
    out.putString(name());
    out.putInt(myLastAddress1);
    out.putInt(myLastAddress2);
//   }
//   catch(const char* msg)
//   {
//     cerr << "ERROR: CartridgeFE::save" << endl << "  " << msg << endl;
//     return false;
//   }

  return true;
}

// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
bool CartridgeFE::load(Serializer& in)
{
//   try
//   {
    if(in.getString() != name())
      return false;

    myLastAddress1 = (uInt16)in.getInt();
    myLastAddress2 = (uInt16)in.getInt();
//   }
//   catch(const char* msg)
//   {
//     cerr << "ERROR: CartridgeF8SC::load" << endl << "  " << msg << endl;
//     return false;
//   }

  return true;
}
