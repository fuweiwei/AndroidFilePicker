package com.veer.filepicker;

import android.content.Context;
import android.widget.Toast;

import java.lang.ref.WeakReference;

/**
 * <li>Author:Veer</li>
 * <li>Date:  2020/3/6</li>
 * <li>Description: </li>
 */
public class FPToastUtil {
    private static WeakReference<Context> mContext;
    private static FPToastUtil mInstance;
    private Toast mToast;

    public static FPToastUtil getInstance(Context ctx) {
        if (mInstance == null || mContext.get() == null) {
            mInstance = new FPToastUtil(ctx);
        }

        return mInstance;
    }

    private FPToastUtil(Context ctx) {
        mContext = new WeakReference<>(ctx);
    }

    public void showToast(String text) {
        if(mToast == null) {
            mToast = Toast.makeText(mContext.get(), text, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(text);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

    public void showToast(int resID) {
        showToast(mContext.get().getResources().getString(resID));
    }

    public void cancelToast() {
        if (mToast != null) {
            mToast.cancel();
        }
    }
}
