package com.appodeal.test.layout;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HorizontalNumberPicker extends LinearLayout implements View.OnTouchListener {

    public interface OnChangeNumberListener {
        void numberChanged(View view, int newNumber);
    }

    private TextView increaseNumber, decreaseNumber;
    private TextView numberTextView;
    private int number = 1, minNumber = 1, maxNumber = 10;

    private static final int SEPARATOR_THICK = 1;
    private static final int CLICKED_CONTROL_COLOR = Color.LTGRAY;
    private static final int NOT_CLICKED_CONTROL_COLOR = Color.TRANSPARENT;
    private static final int SEPARATOR_COLOR = Color.argb(255, 60, 96, 159);
    private static final int DEFAULT_VIEW_SIDE_SIZE = 45; //dp
    private static final long LONGCLICK_CHANGE_NUMBER_PERIOD = 300;
    private OnChangeNumberListener changeNumberListener;

    public HorizontalNumberPicker(Context context) {
        super(context);
        init(context);
    }

    public HorizontalNumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public HorizontalNumberPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public HorizontalNumberPicker(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public void setChangeNumberListener(OnChangeNumberListener listener) {
        changeNumberListener = listener;
    }

    public void setMaxNumber(int maxNumber) {
        this.maxNumber = maxNumber;
        if (number > maxNumber) {
            number = maxNumber;
        }

        numberTextView.setText(String.valueOf(number));
    }

    public void setMinNumber(int minNumber) {
        this.minNumber = minNumber;
        if (number > minNumber) {
            number = minNumber;
        }

        numberTextView.setText(String.valueOf(number));
    }

    /**
     *
     * @param number is initial number of Horizontal number picker
     *               @throws IllegalArgumentException if number less then min number (default is 1) and more then max number (default is 10)
     */
    public void setNumber (int number) {
        if (number < minNumber || number > maxNumber) {
            throw new IllegalArgumentException(String.format("Number must be between MaxNumber: %d1 and MinNumber: %d2", maxNumber, minNumber));
        }

        this.number = number;
        numberTextView.setText(String.valueOf(number));
    }

    public int getNumber () {
        return number;
    }

    private void init(Context context) {
        initControls(context);
        setOrientation(HORIZONTAL);

        addViews(context);

        setDefaultControlValues(context);
        setListeners(context);
    }

    private void setListeners(Context context) {
        decreaseNumber.setOnTouchListener(this);
        increaseNumber.setOnTouchListener(this);
    }

    private void addViews(Context context) {
        LayoutParams params = getParams(context);

        addView(decreaseNumber, params);
        addSeparator(context);
        addView(numberTextView, params);
        addSeparator(context);
        addView(increaseNumber, params);
    }

    private void initControls(Context context) {
        increaseNumber = new TextView(context);
        decreaseNumber = new TextView(context);
        numberTextView = new TextView(context);
    }

    private void setDefaultControlValues(Context context) {
        increaseNumber.setText("+");
        increaseNumber.setGravity(Gravity.CENTER);

        decreaseNumber.setText("-");
        decreaseNumber.setGravity(Gravity.CENTER);

        numberTextView.setText(String.valueOf(number));
        numberTextView.setGravity(Gravity.CENTER);
    }

    private void addSeparator(Context context) {
        LayoutParams separatorParams = new LayoutParams((int)convertToDp(context, SEPARATOR_THICK), ViewGroup.LayoutParams.MATCH_PARENT);
        View separator = new View(context);
        separator.setBackgroundColor(SEPARATOR_COLOR);

        addView(separator, separatorParams);
    }

    private LayoutParams getParams(Context context) {
        int height = (int) convertToDp(context, DEFAULT_VIEW_SIDE_SIZE);
        int width = (int) convertToDp(context, DEFAULT_VIEW_SIDE_SIZE);


        LayoutParams params = new LayoutParams(width, height);

        return params;
    }

    private float convertToDp(Context context, int size) {
        Resources r = context.getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, r.getDisplayMetrics());
        return px;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                handleDownEvent(v);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                handleUpEvent(v);
                break;
        }

        return true;
    }

    private void handleDownEvent (View v) {
        v.setBackgroundColor(CLICKED_CONTROL_COLOR);
        if (v == increaseNumber) {
            increaseNumber();
        } else if (v == decreaseNumber) {
            decreaseNumber();
        }
    }

    private void decreaseNumber() {
        if (number > minNumber) {
            number--;
            numberTextView.setText(String.valueOf(number));
            HorizontalNumberPicker.this.postDelayed(longTouchDecreaseNumberRunnable, LONGCLICK_CHANGE_NUMBER_PERIOD);
            notifyListener ();
        }
    }

    private void increaseNumber() {
        if (number < maxNumber) {
            number++;
            numberTextView.setText(String.valueOf(number));
            HorizontalNumberPicker.this.postDelayed(longTouchIncreaseNumberRunnable, LONGCLICK_CHANGE_NUMBER_PERIOD);
            notifyListener ();
        }
    }

    private void notifyListener() {
        if (changeNumberListener != null) {
            changeNumberListener.numberChanged(this, number);
        }
    }

    private void handleUpEvent (View v) {
        this.removeCallbacks(longTouchIncreaseNumberRunnable);
        this.removeCallbacks(longTouchDecreaseNumberRunnable);
        v.setBackgroundColor(NOT_CLICKED_CONTROL_COLOR);
    }

    private Runnable longTouchIncreaseNumberRunnable = new Runnable() {
        @Override
        public void run() {
            increaseNumber();
        }
    };

    private Runnable longTouchDecreaseNumberRunnable = new Runnable() {
        @Override
        public void run() {
            decreaseNumber();
        }
    };
}
