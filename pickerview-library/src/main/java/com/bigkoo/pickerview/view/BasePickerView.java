package com.bigkoo.pickerview.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.bigkoo.pickerview.utils.PickerViewAnimateUtil;
import com.bigkoo.pickerview.R;

/**
 * Created by Sai on 15/11/22.
 * 精仿iOSPickerViewController控件
 */
public class BasePickerView extends Dialog{
    protected ViewGroup contentContainer;

    public BasePickerView(Context context){
        super(context, R.style.dialog_fullscreen);

        setContentView(R.layout.layout_basepickerview);

        contentContainer = (ViewGroup) findViewById(R.id.content_container);
    }

    /**
     * 点击非dialog区域自动取消
     * @param cancel
     */
    @Override
    public void setCanceledOnTouchOutside(boolean cancel) {
        View view = findViewById(R.id.outmost_container);

        if (cancel) {
            view.setOnTouchListener(onCancelableTouchListener);
        } else{
            view.setOnTouchListener(null);
        }
    }

    /**
     * Called when the user touch on black overlay in order to dismiss the dialog
     */
    private final View.OnTouchListener onCancelableTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                dismiss();
            }
            return false;
        }
    };
}
