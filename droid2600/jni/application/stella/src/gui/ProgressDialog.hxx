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
// $Id: ProgressDialog.hxx,v 1.1.1.1 2010-10-26 03:44:58 cvs Exp $
//
//   Based on code from ScummVM - Scumm Interpreter
//   Copyright (C) 2002-2004 The ScummVM project
//============================================================================

#ifndef PROGRESS_DIALOG_HXX
#define PROGRESS_DIALOG_HXX

class StaticTextWidget;
class SliderWidget;

#include "GuiObject.hxx"
#include "bspf.hxx"

class ProgressDialog : public Dialog
{
  public:
    ProgressDialog(GuiObject* boss, const GUI::Font& font,
                   const string& message);
    virtual ~ProgressDialog();

    void setMessage(const string& message);
    void setRange(int begin, int end, int step);
    void setProgress(int progress);

  protected:
    StaticTextWidget* myMessage;
    SliderWidget*     mySlider;

    int myStart, myFinish, myStep, myCurrentStep;
};

#endif
