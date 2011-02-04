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
// $Id: HelpDialog.cxx,v 1.1.1.1 2010-10-26 03:44:58 cvs Exp $
//
//   Based on code from ScummVM - Scumm Interpreter
//   Copyright (C) 2002-2004 The ScummVM project
//============================================================================

#include "bspf.hxx"

#include "Dialog.hxx"
#include "OSystem.hxx"
#include "Widget.hxx"

#include "HelpDialog.hxx"

// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
HelpDialog::HelpDialog(OSystem* osystem, DialogContainer* parent,
                       const GUI::Font& font)
  : Dialog(osystem, parent, 0, 0, 0, 0),
    myPage(1),
    myNumPages(4)
{
  const int lineHeight   = font.getLineHeight(),
            fontWidth    = font.getMaxCharWidth(),
            fontHeight   = font.getFontHeight(),
            buttonWidth  = font.getStringWidth("Defaults") + 20,
            buttonHeight = font.getLineHeight() + 4;
  int xpos, ypos;
  WidgetArray wid;

  // Set real dimensions
  _w = 46 * fontWidth + 10;
  _h = 12 * lineHeight + 20;

  // Add Previous, Next and Close buttons
  xpos = 10;  ypos = _h - buttonHeight - 10;
  myPrevButton =
    new ButtonWidget(this, font, xpos, ypos, buttonWidth, buttonHeight,
                     "Previous", kPrevCmd);
  myPrevButton->clearFlags(WIDGET_ENABLED);
  wid.push_back(myPrevButton);

  xpos += buttonWidth + 7;
  myNextButton =
    new ButtonWidget(this, font, xpos, ypos, buttonWidth, buttonHeight,
                     "Next", kNextCmd);
  wid.push_back(myNextButton);

  xpos = _w - buttonWidth - 10;
  ButtonWidget* b =
    new ButtonWidget(this, font, xpos, ypos, buttonWidth, buttonHeight,
                     "Close", kCloseCmd);
  wid.push_back(b);
  addOKWidget(b);  addCancelWidget(b);

  xpos = 5;  ypos = 5;
  myTitle = new StaticTextWidget(this, font, xpos, ypos, _w - 10, fontHeight,
                                 "", kTextAlignCenter);

  int lwidth = 15 * fontWidth;
  xpos += 5;  ypos += lineHeight + 4;
  for(uInt8 i = 0; i < kLINES_PER_PAGE; i++)
  {
    myKey[i] =
      new StaticTextWidget(this, font, xpos, ypos, lwidth,
                           fontHeight, "", kTextAlignLeft);
    myDesc[i] =
      new StaticTextWidget(this, font, xpos+lwidth, ypos, _w - xpos - lwidth - 5,
                           fontHeight, "", kTextAlignLeft);
    ypos += fontHeight;
  }

  addToFocusList(wid);
}

// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
HelpDialog::~HelpDialog()
{
}

// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
void HelpDialog::updateStrings(uInt8 page, uInt8 lines, string& title)
{
#define ADD_BIND(k,d) do { myKeyStr[i] = k; myDescStr[i] = d; i++; } while(0)
#define ADD_TEXT(d) ADD_BIND("",d)
#define ADD_LINE ADD_BIND("","")

  uInt8 i = 0;
  switch(page)
  {
    case 1:
#ifdef ANDROID
      title = "Console Switches:";
      ADD_BIND("Camera Button",    "Reset");
      ADD_BIND("Search Button",    "Select");
      ADD_BIND("Volume Down",    "Toggle Left Player Difficulty");
      ADD_BIND("Volume Up",    "Toggle Right Player Difficulty");
#else 
      title = "Common commands:";
#ifndef MAC_OSX
      ADD_BIND("Ctrl Q",    "Quit emulation");
#else
      ADD_BIND("Cmd Q",     "Quit emulation");
#endif
      ADD_BIND("Escape",    "Exit current game");
      ADD_BIND("Tab",       "Enter options menu");
      ADD_BIND("\\",        "Toggle command menu");
#ifndef MAC_OSX
      ADD_BIND("Alt =",     "Increase window size");
      ADD_BIND("Alt -",     "Decrease window size");
      ADD_BIND("Alt Enter", "Toggle fullscreen /");
      ADD_BIND("",          "  windowed mode");
      ADD_BIND("Alt ]",     "Increase volume by 2%");
      ADD_BIND("Alt [",     "Decrease volume by 2%");
#else
      ADD_BIND("Cmd =",     "Increase window size");
      ADD_BIND("Cmd -",     "Decrease window size");
      ADD_BIND("Cmd Enter", "Toggle fullscreen /");
      ADD_BIND("",          "  windowed mode");
      ADD_BIND("Cmd ]",     "Increase volume by 2%");
      ADD_BIND("Cmd [",     "Decrease volume by 2%");
#endif
#endif
      break;
    case 2:
#ifdef ANDROID
      title = "Default Joystick:";
      ADD_BIND("Type", "");
      ADD_TEXT("If your phone has a hardware");
      ADD_TEXT("d-pad (like the Moto Droid),");
      ADD_TEXT("it will be the default");
      ADD_TEXT("joystick. In all other cases,");
      ADD_TEXT("the Accelerometer joystick");
      ADD_TEXT("will be used.");
#else
      title = "Special commands:";
      ADD_BIND("Ctrl g", "Grab mouse (keep in window)");
      ADD_BIND("Ctrl f", "Switch between NTSC/PAL/SECAM");
      ADD_BIND("Ctrl s", "Save game properties");
      ADD_BIND("",       "  to a new file");
      ADD_LINE;
      ADD_BIND("Ctrl 0", "Mouse emulates paddle 0");
      ADD_BIND("Ctrl 1", "Mouse emulates paddle 1");
      ADD_BIND("Ctrl 2", "Mouse emulates paddle 2");
      ADD_BIND("Ctrl 3", "Mouse emulates paddle 3");
#endif
      break;

    case 3:
#ifdef ANDROID
      title = "Accelerometer Joystick:";
      ADD_BIND("Tilt Left", "Left player joystick left");
      ADD_BIND("Tilt Right", "Left player joystick right");
      ADD_BIND("Tilt Forward", "Left player joystick up");
      ADD_BIND("Tilt Back", "Left player joystick down");
      ADD_BIND("Menu Button", "Left player joystick fire");
#else 
      title = "Developer commands:";
#ifndef MAC_OSX
      ADD_BIND("Alt PgUp",  "Increase Display.YStart");
      ADD_BIND("Alt PgDn",  "Decrease Display.YStart");
#else
      ADD_BIND("Cmd PgUp",  "Increase Display.YStart");
      ADD_BIND("Cmd PgDn",  "Decrease Display.YStart");
#endif
      ADD_BIND("Ctrl PgUp", "Increase Display.Height");
      ADD_BIND("Ctrl PgDn", "Decrease Display.Height");
#endif
      break;

    case 4:
#ifdef ANDROID
      title = "DPad Joystick:";
      ADD_BIND("DPad Left", "Left player joystick left");
      ADD_BIND("DPad Right", "Left player joystick right");
      ADD_BIND("DPad Up", "Left player joystick up");
      ADD_BIND("DPad Down", "Left player joystick down");
      ADD_BIND("Space Bar or Menu Button", "Left player joystick fire");
#else
      title = "All other commands:";
      ADD_LINE;
      ADD_BIND("Remapped Events", "");
      ADD_TEXT("Most other commands can be");
      ADD_TEXT("remapped.  Please consult the");
      ADD_TEXT("'Input Settings' section for");
      ADD_TEXT("more information.");
#endif
      break;
  }

  while(i < lines)
    ADD_LINE;
}

// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
void HelpDialog::displayInfo()
{
  string titleStr;
  updateStrings(myPage, kLINES_PER_PAGE, titleStr);

  myTitle->setLabel(titleStr);
  for(uInt8 i = 0; i < kLINES_PER_PAGE; i++)
  {
    myKey[i]->setLabel(myKeyStr[i]);
    myDesc[i]->setLabel(myDescStr[i]);
  }

  _dirty = true;
}

// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
void HelpDialog::handleCommand(CommandSender* sender, int cmd,
                               int data, int id)
{
  switch(cmd)
  {
    case kNextCmd:
      myPage++;
      if(myPage >= myNumPages)
        myNextButton->clearFlags(WIDGET_ENABLED);
      if(myPage >= 2)
        myPrevButton->setFlags(WIDGET_ENABLED);

      displayInfo();
      break;

    case kPrevCmd:
      myPage--;
      if(myPage <= myNumPages)
        myNextButton->setFlags(WIDGET_ENABLED);
      if(myPage <= 1)
        myPrevButton->clearFlags(WIDGET_ENABLED);

      displayInfo();
      break;

    default:
      Dialog::handleCommand(sender, cmd, data, 0);
  }
}
