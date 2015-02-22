package com.example.SmartNotes;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

public class MyEditText extends EditText
{
	 
    //The image we are going to use for the Clear button
    private Drawable imgCloseButton = getResources().getDrawable(R.drawable.image_clear);
     
    public MyEditText(Context context) {
        super(context);
        init();
    }
 
    public MyEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
 
    public MyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
     
    void init() {
         
        // Set bounds of the Clear button so it will look ok
        imgCloseButton.setBounds(0, 0, imgCloseButton.getIntrinsicWidth(), imgCloseButton.getIntrinsicHeight());
 
        // There may be initial text in the field, so we may need to display the  button
        handleClearButton();
 
        //if the Close image is displayed and the user remove his finger from the button, clear it. Otherwise do nothing
        this.setOnTouchListener(new OnTouchListener() 
        {
           
            public boolean onTouch(View v, MotionEvent event) {
 
                MyEditText et = MyEditText.this;
 
                if (et.getCompoundDrawables()[2] == null)
                    return false;
                 
                if (event.getAction() != MotionEvent.ACTION_UP)
                    return false;
                 
                if (event.getX() > et.getWidth() - et.getPaddingRight() - imgCloseButton.getIntrinsicWidth()) {
                    et.setText("");
                    MyEditText.this.handleClearButton();
                }
                return false;
            }

			
        });
 
        //if text changes, take care of the button
        this.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
 
                MyEditText.this.handleClearButton();
            }
 
            public void afterTextChanged(Editable arg0) {
            }
 
           
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });
    }
     
   
    void handleClearButton() {
        if (this.getText().toString().equals(""))
        {
            // add the clear button
            this.setCompoundDrawables(this.getCompoundDrawables()[0], this.getCompoundDrawables()[1], null, this.getCompoundDrawables()[3]);
        }
        else
        {
            //remove clear button
            this.setCompoundDrawables(this.getCompoundDrawables()[0], this.getCompoundDrawables()[1], imgCloseButton, this.getCompoundDrawables()[3]);
        }
    }
}