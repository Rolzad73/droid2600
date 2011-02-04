package com.tvi910.android.core.buttonpanel;

import android.app.Activity;

import android.content.Context;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Typeface;

import android.util.Log;

import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.WindowManager;

import android.widget.AbsoluteLayout;
import android.widget.ImageView;

import com.tvi910.android.sdl.SDLInterface;
import com.tvi910.android.sdl.SDLKeysym;

import com.tvi910.android.R;

/**
 * Configurable button panel. Buttons are arranged in a grid and presented 
 * on a transparent overlay. 
 * 
 *
 * @author Trent McNair
 * @since August 2010
 */
public class ButtonPanel extends View {

    private static final int MT_MOVE = 0;
    private static final int MT_UP = 1;
    private static final int MT_DOWN = 2;

    private Context _context;

    private int _buttonWidth;
    private int _buttonHeight;
    private int _width;
    private int _height;

    private Typeface _typeface;
    private Paint _selectPaint;
    private int _textSize;

    private Button _buttonGrid[][];

    // position state
    private Rect _rect;
    private int _currentColumn = -1;
    private int _currentRow = -1;
    private boolean _selected = false;

    private ViewGroup _viewGroup = null;

    private ImageView _panelButton = null;
//    private ImageView _panelButtonDown = null;
    private ButtonCallback _panelButtonCallback = null;

    private float _padding = .85f;

    /**
     * @param context 
     * @param typeface
     * @param numColumns
     * @param numRows
     * @param widthPercent The percent [0-100] of the width of the screen the 
     *     button panel should fill. This will be provided on a best effort 
     *     basis balanced with desired height and aspect ratio. 
     * @param heightPercent The percent [0-100] of the height of the screen the 
     *     button panel should fill. This will be provided on a best effort 
     *     basis balanced with desired width and aspect ratio.
     * @param xPosition A scaled value [0-100] defining the horizontal position 
     *     of the button panel
     * @param yPosition A scaled value [0-100] defining the vertical position 
     *     of the button panel on screen.
     * @param aspectRatio The desired aspect ratio of each button. Priority is 
     *     given to aspect ratio when setting up the panel geometry.
     * @param callback Callback for slider button clicks.
     * @param xOffsetPercent 
     */
    public ButtonPanel(
            Context context,
            Typeface typeface,
            int numColumns,
            int numRows,
            int widthPercent,
            int heightPercent, 
            int xOffsetPercent, 
            int yOffsetPercent, 
            float aspectRatio,
            int slider) {
        super(context);

        // sanity check our input values.
        if (xOffsetPercent < 0) xOffsetPercent = 0;
        else if (xOffsetPercent > 100) xOffsetPercent = 100;
        if (yOffsetPercent < 0) yOffsetPercent = 0;
        else if (yOffsetPercent > 100) yOffsetPercent = 100;

        float xOffset = (float)xOffsetPercent/100f;
        float yOffset = (float)yOffsetPercent/100f;

        _context = context;
        _rect = new Rect(0,0,100,100);

        WindowManager wm = 
            (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        Display d = wm.getDefaultDisplay();
        int screenWidth = d.getWidth();
        int screenHeight = d.getHeight();

        _selectPaint = new Paint();
        _selectPaint.setARGB(128,255,255,255);

        if (null != typeface) {
            _typeface = typeface;
        }
        else {
            _typeface = Typeface.MONOSPACE;
        }

        // calculate button width 
        _buttonWidth = (int)(screenWidth*((float)widthPercent/100))/numColumns;
        _buttonHeight = (int)(screenHeight*((float)heightPercent/100))/numRows;

        if (0 != aspectRatio) {
            float implicitAspectRatio = (float)_buttonWidth/(float)_buttonHeight;
            while (implicitAspectRatio > aspectRatio) {
                // while the button geometry is too wide
                _buttonWidth--;
                implicitAspectRatio = (float)_buttonWidth/(float)_buttonHeight;
            }
            
            while (implicitAspectRatio < aspectRatio) {
                // while the button geometry is too high
                _buttonHeight--;
                implicitAspectRatio = (float)_buttonWidth/(float)_buttonHeight;
            } 
        }

        // set the initial text size to some arbitrarily high number
        _textSize = 128;

        _width = _buttonWidth*numColumns;
        int xBorder = (int)((screenWidth - _width) * xOffset);

        _height = _buttonHeight*numRows;
        int yBorder = (int)((screenHeight - _height) * yOffset);

        _buttonGrid = new Button[numColumns][numRows];
        for (int row=0; row<numRows; row++) {
            for (int col=0; col<numColumns; col++) {
                _buttonGrid[col][row] = null;
            }
        }

        if (slider != 0) initSlider(screenWidth, screenHeight,slider);

        updateLayout(xBorder, yBorder,_width,_height);
    }

    public void setPanelButtonCallback(ButtonCallback panelButtonCallback) {
        _panelButtonCallback = panelButtonCallback;
    }

    public void setPadding(float padding) {
        _padding = padding;
    }

    private void initSlider(int screenWidth, int screenHeight, int slider) {

        int buttonWidth = screenWidth / 20;
        if (screenWidth < screenHeight) buttonWidth = screenHeight/20;

        int xOffset = 0;
        int yOffset = 0;
        int resId = R.drawable.drawertab_32_nw;

        if (slider == 2) {
            xOffset = (screenWidth-buttonWidth);
            resId = R.drawable.drawertab_32_ne;
        }
        else if (slider == 3) {
            xOffset = (screenWidth-buttonWidth);
            yOffset = (screenHeight-buttonWidth);
            resId = R.drawable.drawertab_32_se;
        }
        else if (slider == 4)  {
            yOffset = (screenHeight-buttonWidth);
            resId = R.drawable.drawertab_32_sw;
        }

        _panelButton = new ImageView(_context);
        _panelButton.setPadding(0,0,0,0);
        _panelButton.setImageResource(resId);
        _panelButton.setAlpha(72);
        _panelButton.setOnClickListener(mPanelButtonListener);
        _panelButton.setLayoutParams(new AbsoluteLayout.LayoutParams(buttonWidth, buttonWidth, xOffset, yOffset));
    }
  
    public void setToggle(
            int column, 
            int row, 
            int bgcolor, 
            int fgcolor, 
            String text, 
            ButtonCallback callback) {
        setButton(column, row, bgcolor, fgcolor, text, callback, 1, true);
    }

    public void setButton(
            int column, 
            int row, 
            int bgcolor, 
            int fgcolor, 
            String text, 
            ButtonCallback callback) {
        setButton(column, row, bgcolor, fgcolor, text, callback, 1, false);
    }

    public void setButton(
            int column, 
            int row, 
            int bgcolor, 
            int fgcolor, 
            String text, 
            ButtonCallback callback,
            int colspan) {
        setButton(column, row, bgcolor, fgcolor, text, callback, colspan, false);
    }
    
    private void setButton(
            int column, 
            int row, 
            int bgcolor, 
            int fgcolor, 
            String text, 
            ButtonCallback callback,
            int colspan,
            boolean isToggle) {

        final Rect buttonRect = new Rect(
            column * _buttonWidth + 2, 
            row * _buttonHeight + 2, 
            ((column+colspan) * _buttonWidth) -2,
            ((row+1) * _buttonHeight) -2);

        final Paint bgPaint = new Paint();
        bgPaint.setColor(bgcolor);

        final Paint strokePaint = new Paint();
        strokePaint.setColor(fgcolor);
        strokePaint.setAntiAlias(true);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(2);

        final Paint textPaint = new Paint();
        textPaint.setColor(fgcolor);
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTypeface(_typeface);

        // calculate the font size we'll be using. If the new size is less 
        // than the current font size, then we need to adjust all of the 
        // buttons to the new font size.
        final Rect textRect = new Rect();  
        textPaint.setTextSize(_textSize);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTypeface(_typeface);
        int newTextSize = _textSize;
        while (newTextSize > 1) {
            textPaint.getTextBounds(text, 0, text.length(), textRect);
            if ((textRect.width() < (_buttonWidth*(_padding))) && (textRect.height() < (_buttonHeight*(_padding)))) break;
            newTextSize-=1;
            textPaint.setTextSize(newTextSize);
        }
        if (newTextSize < _textSize) {
//            Log.v("ButtonPanel", "Found new largest button text: " + text 
//                + ", _textSize: " + newTextSize);
            updateTextSize(newTextSize);
        }
        else {
            textPaint.setTextSize(_textSize);
        }

        final Rect fontRect = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), fontRect);

        final Button b = new Button(callback, bgPaint, strokePaint, textPaint, text, 
            buttonRect.centerX(), 
            buttonRect.bottom - ( (buttonRect.height() - fontRect.height())/2),
            buttonRect,
            (colspan-1));

        if (isToggle) {
            for (int i=0; i<colspan; i++) {
                _buttonGrid[column+i][row].setToggle(b);
            }
            
        }
        else {
            for (int i=0; i<colspan; i++) {
                _buttonGrid[column+i][row] = b;
            }
        }

    }

    /**
     * This is called from setButton() when we have found a button text geometry 
     * that causes textSize to be lowered. 
     */
    private void updateTextSize(int newTextSize) {
        _textSize = newTextSize;
        final Rect r = new Rect();
        final Paint p = new Paint();
        p.setTextAlign(Paint.Align.CENTER);
        p.setAntiAlias(true);
        p.setTypeface(_typeface);
        p.setTextSize(_textSize);

        // walk through each button and reset the text size
        for (int i=0; i<_buttonGrid.length; i++) {
            for (int j=0; j<_buttonGrid[i].length; j++) {
                if (null != _buttonGrid[i][j]) {
                    // calculate the new button rect
                    p.getTextBounds(
                        _buttonGrid[i][j].getText(), 
                        0, 
                        _buttonGrid[i][j].getText().length(), 
                        r);
                    // update the button
                    Button toggle = null;
                    if (_buttonGrid[i][j].getHasToggle()) {
                        toggle = new Button(
                            _buttonGrid[i][j].getToggle().getCallback(), 
                            _buttonGrid[i][j].getToggle().getBgPaint(),
                            _buttonGrid[i][j].getToggle().getStrokePaint(),
                            _buttonGrid[i][j].getToggle().getTextPaint(),
                            _buttonGrid[i][j].getToggle().getText(),
                            _buttonGrid[i][j].getToggle().getRect().centerX(),
                            _buttonGrid[i][j].getToggle().getRect().bottom - ( (_buttonGrid[i][j].getToggle().getRect().height() - r.height())/2),
                            _buttonGrid[i][j].getToggle().getRect(),
                            _buttonGrid[i][j].getToggle().getColspan());
                    }

                    _buttonGrid[i][j] = new Button(
                        _buttonGrid[i][j].getCallback(), 
                        _buttonGrid[i][j].getBgPaint(),
                        _buttonGrid[i][j].getStrokePaint(),
                        _buttonGrid[i][j].getTextPaint(),
                        _buttonGrid[i][j].getText(),
                        _buttonGrid[i][j].getRect().centerX(),
                        _buttonGrid[i][j].getRect().bottom - ( (_buttonGrid[i][j].getRect().height() - r.height())/2),
                        _buttonGrid[i][j].getRect(),
                        _buttonGrid[i][j].getColspan());
                    _buttonGrid[i][j].getTextPaint().setTextSize(_textSize);

                    if (null != toggle) _buttonGrid[i][j].setToggle(toggle);
                }
            }
        }
    }

    private void processMotion(int x, int y, int motionType) {
        if (x >= 0 && x < _width && y >= 0 && y < _height) {
            int col = (x/_buttonWidth);
            int row = (y/_buttonHeight);
            int newX = col*_buttonWidth;
            int newY = row*_buttonHeight;
            
            if (MT_MOVE == motionType) {
                if (col != _currentColumn || row != _currentRow) {
                    invalidate(_rect);
                    if (null != _buttonGrid[col][row]) {
                        _selected = true;
                        _rect = _buttonGrid[col][row].getRect();
                        invalidate(_rect);
                    }
                    else {
                        _selected = false;
                    }
                }
            }
            else if (MT_DOWN == motionType) {
                if (null != _buttonGrid[col][row]) {
                    _selected = true;
                    _rect = _buttonGrid[col][row].getRect();
                    invalidate(_rect);
                }
            }
            else if (MT_UP == motionType) { 
                _selected = false;
                invalidate(_rect);
                if (null != _buttonGrid[col][row]) {
                    _rect = _buttonGrid[col][row].getRect();
                    _buttonGrid[col][row].onButtonUp();
                    _buttonGrid[col][row] = _buttonGrid[col][row].getToggle();
                    invalidate(_rect);
                }
            }

            _currentColumn = col;
            _currentRow = row;
        }
        else {  
            if (_selected) {
                _selected = false;
                invalidate(_rect);
            }
        }
    }

    public boolean onTouchEvent(MotionEvent event) {

        final int action = event.getAction();

        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                processMotion((int)event.getX(), (int)event.getY(), MT_DOWN);
                return true;
            }
            case MotionEvent.ACTION_MOVE: {
                processMotion((int)event.getX(), (int)event.getY(), MT_MOVE);
                return true;
            }
            case MotionEvent.ACTION_UP: {
                processMotion((int)event.getX(), (int)event.getY(), MT_UP);
                return true;
            }
        }

        return true;
    }

    /**
     * Force a layout change to this view
     */
    public void updateLayout(int x, int y, int width, int height) {
        setLayoutParams(new AbsoluteLayout.LayoutParams(width, height, x, y));
        requestLayout();
    }

    public ButtonPanel(Context context) {
        super(context);
    }

    @Override protected void onDraw(Canvas canvas) {

        for (int row=0; row<_buttonGrid[0].length; row++) {
            for (int col=0; col<_buttonGrid.length; col++) {
                if (null != _buttonGrid[col][row]) {
                    canvas.drawRoundRect(new RectF(_buttonGrid[col][row].getRect()),5,5,_buttonGrid[col][row].getBgPaint());
                    canvas.drawRoundRect(new RectF(_buttonGrid[col][row].getRect()),5,5,_buttonGrid[col][row].getStrokePaint());
                    canvas.drawText(
                        _buttonGrid[col][row].getText(), 
                        _buttonGrid[col][row].getFontX(),
                        _buttonGrid[col][row].getFontY(),
                        _buttonGrid[col][row].getTextPaint());
                    col += _buttonGrid[col][row].getColspan();
                } 
            }
        }

        if (_selected) canvas.drawRoundRect(new RectF(_rect), 5, 5, _selectPaint);
    }

    /**
     * A call back for when the user presses the start button
     */
    OnClickListener mPanelButtonListener = new OnClickListener() {
        public void onClick(View v) {
            if (null != _panelButtonCallback) _panelButtonCallback.onButtonUp();
        }
    };

    public void addToLayout(ViewGroup viewGroup) {
        if (_viewGroup == null) {
            _viewGroup = viewGroup;
            if (null != _panelButton) _viewGroup.addView(_panelButton);
        }
    }
    public void hidePanel() {
        if (_viewGroup != null) {
            _viewGroup.removeView(this);
        }
    }
    public void showPanel() {
        if (_viewGroup != null) {
            if (getParent() != null) _viewGroup.removeView(this);
            _viewGroup.addView(this);
        }
    }

    public boolean isVisible() {
        return (null != getParent());
    }
}
