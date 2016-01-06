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
// $Id: GameInfoDialog.hxx,v 1.1.1.1 2010-10-26 03:44:58 cvs Exp $
//
//   Based on code from ScummVM - Scumm Interpreter
//   Copyright (C) 2002-2004 The ScummVM project
//============================================================================

#ifndef GAME_INFO_DIALOG_HXX
#define GAME_INFO_DIALOG_HXX

class OSystem;
class GuiObject;
class EditTextWidget;
class PopUpWidget;
class StaticTextWidget;
class TabWidget;
class SliderWidget;

#include "Array.hxx"
#include "Dialog.hxx"
#include "Command.hxx"
#include "Props.hxx"
#include "StringList.hxx"

class GameInfoDialog : public Dialog, public CommandSender
{
  public:
    GameInfoDialog(OSystem* osystem, DialogContainer* parent,
                   const GUI::Font& font, GuiObject* boss);
    virtual ~GameInfoDialog();

  protected:
    void loadConfig();
    void saveConfig();
    void handleCommand(CommandSender* sender, int cmd, int data, int id);

  private:
    void setDefaults();
    void loadView();

  private:
    TabWidget* myTab;
    ButtonWidget* myCancelButton;

    // Cartridge properties
    EditTextWidget*   myName;
    StaticTextWidget* myMD5;
    EditTextWidget*   myManufacturer;
    EditTextWidget*   myModelNo;
    EditTextWidget*   myRarity;
    EditTextWidget*   myNote;
    PopUpWidget*      mySound;
    PopUpWidget*      myType;

    // Console properties
    PopUpWidget* myLeftDiff;
    PopUpWidget* myRightDiff;
    PopUpWidget* myTVType;

    // Controller properties
    PopUpWidget* myP0Controller;
    PopUpWidget* myP1Controller;
    PopUpWidget* mySwapPaddles;
    PopUpWidget* myLeftPort;
    PopUpWidget* myRightPort;

    // Display properties
    PopUpWidget*      myFormat;
    EditTextWidget*   myYStart;
    EditTextWidget*   myHeight;
    PopUpWidget*      myPhosphor;
    SliderWidget*     myPPBlend;
    StaticTextWidget* myPPBlendLabel;

    enum {
      kLeftCChanged    = 'LCch',
      kRightCChanged   = 'RCch',
      kPhosphorChanged = 'PPch',
      kPPBlendChanged  = 'PBch'
    };

    // Game properties for currently loaded ROM
    Properties myGameProperties;

    // Indicates that we've got a valid properties entry
    bool myPropertiesLoaded;

    // Indicates that the default properties have been loaded
    bool myDefaultsSelected;
};

#endif
