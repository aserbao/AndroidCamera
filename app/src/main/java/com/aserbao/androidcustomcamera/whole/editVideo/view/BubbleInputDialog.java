package com.aserbao.androidcustomcamera.whole.editVideo.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aserbao.androidcustomcamera.R;
import com.aserbao.androidcustomcamera.base.utils.CommonUtils;


/**
 * Created by Abner on 15/6/12.
 * QQ 230877476
 * Email nimengbo@gmail.com
 */
public class BubbleInputDialog extends Dialog {
    private final String defaultStr;
    private EditText et_bubble_input;
    private TextView tv_show_count;
    private TextView tv_action_done;
    private static final int MAX_COUNT = 33; //字数最大限制33个
    private Context mContext;
    private BubbleTextView bubbleTextView;

    public BubbleInputDialog(Context context) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        mContext = context;
        defaultStr = context.getString(R.string.double_click_input_text);
        initView();
    }

    public BubbleInputDialog(Context context, BubbleTextView view) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        mContext = context;
        defaultStr = context.getString(R.string.double_click_input_text);
        bubbleTextView = view;
        initView();
    }

    public void setBubbleTextView(BubbleTextView bubbleTextView) {
        this.bubbleTextView = bubbleTextView;
        if (defaultStr.equals(bubbleTextView.getmStr())) {
            et_bubble_input.setText("");
        } else {
            et_bubble_input.setText(bubbleTextView.getmStr());
            et_bubble_input.setSelection(bubbleTextView.getmStr().length());
        }
    }


    private void initView() {
        setContentView(R.layout.view_input_dialog);
        tv_action_done = (TextView) findViewById(R.id.tv_action_done);
        et_bubble_input = (EditText) findViewById(R.id.et_bubble_input);
        tv_show_count = (TextView) findViewById(R.id.tv_show_count);
        et_bubble_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                long textLength = CommonUtils.calculateLength(s);
                tv_show_count.setText(String.valueOf(MAX_COUNT - textLength));
                if (textLength > MAX_COUNT) {
                    tv_show_count.setTextColor(Color.parseColor("#e73a3d"));
                } else {
                    tv_show_count.setTextColor(Color.parseColor("#8b8b8b"));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        et_bubble_input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    done();
                    return true;
                }
                return false;
            }
        });
        tv_action_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                done();
            }
        });
    }


    @Override
    public void show() {
        super.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager m = (InputMethodManager) et_bubble_input.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                m.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
            }
        }, 500);

    }

    @Override
    public void dismiss() {
        super.dismiss();
        InputMethodManager m = (InputMethodManager) et_bubble_input.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        m.hideSoftInputFromWindow(et_bubble_input.getWindowToken(), 0);
    }

    public interface CompleteCallBack {
        void onComplete(View bubbleTextView, String str);
    }

    private CompleteCallBack mCompleteCallBack;

    public void setCompleteCallBack(CompleteCallBack completeCallBack) {
        this.mCompleteCallBack = completeCallBack;
    }

    private void done() {
        if (Integer.valueOf(tv_show_count.getText().toString()) < 0) {
            Toast.makeText(mContext, "您已超过字数限制", Toast.LENGTH_LONG).show();
            return;
        }
        dismiss();
        if (mCompleteCallBack != null) {
            String str;
            if (TextUtils.isEmpty(et_bubble_input.getText())) {
                str = "";
            } else {
                str = et_bubble_input.getText().toString();
            }
            mCompleteCallBack.onComplete(bubbleTextView, str);
        }
    }
}
