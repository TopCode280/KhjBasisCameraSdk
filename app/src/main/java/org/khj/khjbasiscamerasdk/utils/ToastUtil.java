package org.khj.khjbasiscamerasdk.utils;

import android.content.Context;
import android.widget.Toast;

import org.khj.khjbasiscamerasdk.R;


public class ToastUtil {


    public static void showToast(Context context,
                                 String content) {
        Toast.makeText(context,content,Toast.LENGTH_LONG).show();
    }

    public static void showToast(Context context,
                                 boolean success) {
        try {
            Toast.makeText(context,success ? context.getString(R.string.modifySuccess) : context.getString(R.string.modifyFailure),Toast.LENGTH_LONG).show();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

}
