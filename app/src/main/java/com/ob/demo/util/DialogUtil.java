package com.ob.demo.util;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.ob.demo.R;

/**
 * Created by ob on 2017/8/20.
 *
 */

public class DialogUtil extends Dialog {

    private Context context;
    private DialogUtil.OnOkClickListener mOkListener;
    private DialogUtil.OnCancleClickListener mCancleListener;
    private String text, cancleStr, confirmStr;
    private Spannable spannable;
    private int idRes;
    private boolean isSingleCancle;
    private TextView dialog_new_content;

    public DialogUtil(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.customize_dialog);

        getWindow().getDecorView().setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.transparent));
        ImageView dialog_new_img = (ImageView) findViewById(R.id.dialog_new_img);
        dialog_new_content = (TextView) findViewById(R.id.dialog_new_content);
        dialog_new_content.setText(text == null? spannable : text);
        if(idRes != 0)dialog_new_img.setImageDrawable(ContextCompat.getDrawable(context, idRes));
        TextView dialog_new_confirm = (TextView) findViewById(R.id.dialog_new_confirm);
        dialog_new_confirm.setOnClickListener(OnOkClickListener);
        TextView dialog_new_cancle = (TextView) findViewById(R.id.dialog_new_cancle);
        dialog_new_cancle.setOnClickListener(OnCancelClickListener);
        if(isSingleCancle){//单 取消按钮
            dialog_new_confirm.setVisibility(View.GONE);
            findViewById(R.id.dialog_new_line).setVisibility(View.GONE);
            dialog_new_cancle.setText(cancleStr == null ? context.getString(R.string.cancel) : cancleStr);
        }else{//重置双按钮的文字
            dialog_new_confirm.setText(confirmStr  == null ? context.getString(R.string.confirm) : confirmStr);
            dialog_new_cancle.setText(cancleStr == null ? context.getString(R.string.cancel) : cancleStr);
        }
    }
    public DialogUtil setContentText(String text){
        this.text = text;
        if(dialog_new_content != null){//用于更新同一个dialog上的content
            dialog_new_content.setText(text);
            dialog_new_content.postInvalidate();
        }
        return this;
    }
    public DialogUtil setContentText(int idRes){
        setContentText(context.getString(idRes));
        return this;
    }

    public DialogUtil setImage(int id){
        this.idRes = id;
        return this;
    }
    public DialogUtil showDilaog(){
        show();
        return this;
    }

    /**
     * 只有 取消按钮
     */
    public DialogUtil setSingleCancle(boolean isSingleCancle){
        this.isSingleCancle = isSingleCancle;
        return this;
    }
    /**
     * @param str 确定按钮的文字
     */
    public DialogUtil setConfirmStr(String str){
        this.confirmStr = str;
        return this;
    }
    /**
     * @param str 取消按钮的文字
     */
    public DialogUtil setCancleStr(String str){
        this.cancleStr = str;
        return this;
    }
    public String getContentText(){
        return text;
    }
    public DialogUtil setOnOkClickListener(OnOkClickListener mOkListener){
        this.mOkListener = mOkListener;
        return this;
    }
    public DialogUtil setOnCancleClickListener(OnCancleClickListener mCancleListener){
        this.mCancleListener = mCancleListener;
        return this;
    }
    public interface OnOkClickListener {
        void onClick();
    }
    public interface OnCancleClickListener {
        void onClick();
    }

    private View.OnClickListener OnCancelClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            dismiss();
            if (mCancleListener != null){
                mCancleListener.onClick();
            }
        }
    };
    private View.OnClickListener OnOkClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            dismiss();
            if (mOkListener != null) {
                mOkListener.onClick();
            }
        }
    };
}
