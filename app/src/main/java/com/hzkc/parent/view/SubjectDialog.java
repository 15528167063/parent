package com.hzkc.parent.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.NumberPicker;

import com.hzkc.parent.R;

import butterknife.OnClick;

/**
 * 选择科目
 */
public class SubjectDialog extends Dialog implements View.OnClickListener {
    Button subject_confirm;
    Button subject_cancle;
    TextColorNumberPicker numberPicker;

    private int selectedIndex = 0;
    private subjectClickListener clickListener;
    private String[] course;

    public void setClickListener(subjectClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public SubjectDialog(Context context) {
        super(context, R.style.customDialog);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_subject, null);
        Window w = this.getWindow();
        w.setGravity(Gravity.BOTTOM);
        w.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        w.setAttributes(lp);

        subject_cancle= (Button)view. findViewById(R.id.subject_cancel);
        subject_confirm= (Button) view.findViewById(R.id.subject_confirm);
        numberPicker= (TextColorNumberPicker) view.findViewById(R.id.subject_picker);
        subject_cancle.setOnClickListener(this);
        subject_confirm.setOnClickListener(this);
        this.setContentView(view);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                selectedIndex = i1;

            }
        });
    }

    @OnClick({R.id.subject_cancel, R.id.subject_confirm})
    public void onClickMethod(View v) {
        switch (v.getId()) {
            case R.id.subject_cancel:
                dismiss();
                break;
            case R.id.subject_confirm:
                dismiss();
                if (clickListener != null) {
                    clickListener.subjectClick(course[selectedIndex], selectedIndex);
                }
                break;
        }

    }

    public void setPickerData(String[] data) {
        if (data == null) {
            return;
        }
        course = data;
        numberPicker.setDisplayedValues(course);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(course.length - 1);
        numberPicker.setNumberPickerDividerColor(numberPicker);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.subject_cancel:
                dismiss();
                break;
            case R.id.subject_confirm:
                dismiss();
                if (clickListener != null) {
                    clickListener.subjectClick(course[selectedIndex], selectedIndex);
                }
                break;
        }
    }

    /**
     * 选择科目监听
     */
    public interface subjectClickListener {
        /**
         * 选择科目
         *
         * @param subject 选择的文字
         * @param index   下标
         */
        void subjectClick(String subject, int index);
    }


}
