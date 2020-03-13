package com.adorkable.iosdialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


/**
 * Author: liuqiang
 * Time: 2018-01-02 13:28
 * Description:
 */
public class IOSAlertDialog {

    private Context context;
    private Dialog dialog;
    private LinearLayout container;
    private TextView titleTv;
    private TextView msgTv;
    private Button negBtn;
    private Button posBtn;
    private ImageView img_line;
    private Display display;
    private boolean showTitle = false;
    private boolean showMsg = false;
    private boolean showPosBtn = false;
    private boolean showNegBtn = false;
    private boolean otherCancellation = false;
    private boolean ClickBlankCancellation = true;
    private Disposable subscribe;
    protected CompositeDisposable mDisposable;

    public IOSAlertDialog(Context context) {
        this.context = context;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    public IOSAlertDialog init() {
        // 获取Dialog布局
        View view = LayoutInflater.from(context).inflate(R.layout.view_alertdialog, null);

        // 获取自定义Dialog布局中的控件
        container = (LinearLayout) view.findViewById(R.id.container);
        titleTv = (TextView) view.findViewById(R.id.txt_title);
        titleTv.setVisibility(View.GONE);
        msgTv = (TextView) view.findViewById(R.id.txt_msg);
        msgTv.setVisibility(View.GONE);
        negBtn = (Button) view.findViewById(R.id.btn_neg);
        negBtn.setVisibility(View.GONE);
        posBtn = (Button) view.findViewById(R.id.btn_pos);
        posBtn.setVisibility(View.GONE);
        img_line = (ImageView) view.findViewById(R.id.img_line);
        img_line.setVisibility(View.GONE);

        // 定义Dialog布局和参数
        dialog = new Dialog(context, R.style.AlertDialogStyle);
        dialog.setContentView(view);

        // 调整dialog背景大小
        container.setLayoutParams(new FrameLayout.LayoutParams((int) (display.getWidth() * 0.85), LinearLayout.LayoutParams.WRAP_CONTENT));

        mDisposable = new CompositeDisposable();
        return this;
    }

    public IOSAlertDialog setIosDialogDismissListen(onDismissListen onDismissListen) {
        dialog.setOnDismissListener((dialogInterface -> {
            if (!otherCancellation) {
                onDismissListen.onDismiss();
                dialog.dismiss();
                if (mDisposable != null) {
                    mDisposable.dispose();
                }
            }
        }));
        return this;
    }

    //是否点击空白取消
    public IOSAlertDialog setClickBlankCancellation(boolean ClickBlankCancellation) {
        this.ClickBlankCancellation = ClickBlankCancellation;
        return this;
    }

    public IOSAlertDialog setTitle(String title) {
        showTitle = true;
        if ("".equals(title)) {
            titleTv.setGravity(View.INVISIBLE);
        } else {
            titleTv.setText(title);
        }
        return this;
    }

    public IOSAlertDialog setMsg(String msg) {
        showMsg = true;
        if ("".equals(msg)) {
            msgTv.setGravity(View.GONE);
        } else {
            msgTv.setText(msg);
        }
        return this;
    }

    public IOSAlertDialog setGravity(int gravity) {
        titleTv.setGravity(gravity);
        return this;
    }

    public IOSAlertDialog setCancelable(boolean cancel) {
//        dialog.setCancelable(cancel);
        return this;
    }

    public IOSAlertDialog setTitleTextSize(float txtSize) {
        if (txtSize > 0) {
            titleTv.setTextSize(txtSize);
        }
        return this;
    }

    public IOSAlertDialog setPositiveButton(String text, final View.OnClickListener listener) {
        showPosBtn = true;
        if ("".equals(text)) {
            posBtn.setGravity(View.GONE);
        } else {
            posBtn.setText(text);
        }
        posBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                listener.onClick(v);
                otherCancellation = true;
            }
        });
        return this;
    }

    public IOSAlertDialog setPositiveButton(String text, ActionListener actionListener) {
        showPosBtn = true;
        if ("".equals(text)) {
            posBtn.setGravity(View.GONE);
        } else {
            posBtn.setText(text);
        }
        posBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionListener.onClick(dialog, 0);
                otherCancellation = true;
                if (mDisposable != null) {
                    mDisposable.dispose();
                }
            }
        });
        return this;
    }

    public IOSAlertDialog setNegativeButton(String text, final View.OnClickListener listener) {
        showNegBtn = true;
        if ("".equals(text)) {
            negBtn.setGravity(View.GONE);
        } else {
            negBtn.setText(text);
        }
        negBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                listener.onClick(v);
                otherCancellation = true;
                if (mDisposable != null) {
                    mDisposable.dispose();
                }
            }
        });
        return this;
    }

    public IOSAlertDialog setNegativeButton(String text, ActionListener actionListener) {
        showNegBtn = true;
        if ("".equals(text)) {
            negBtn.setGravity(View.GONE);
        } else {
            negBtn.setText(text);
        }
        negBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionListener.onClick(dialog, 1);
                otherCancellation = true;
            }
        });
        return this;
    }

    private void setLayout() {
        if (!showTitle && !showMsg) {
            titleTv.setText(context.getString(R.string.tips));
            titleTv.setVisibility(View.INVISIBLE);
        }

        if (showTitle) {
            titleTv.setVisibility(View.VISIBLE);
        }

        if (showMsg) {
            msgTv.setVisibility(View.VISIBLE);
        }

        if (!showPosBtn && !showNegBtn) {
            posBtn.setText(context.getString(R.string.commit));
            posBtn.setVisibility(View.VISIBLE);
            posBtn.setBackgroundResource(R.drawable.alertdialog_single_selector);
            posBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }


        if (showPosBtn && showNegBtn) {
            posBtn.setVisibility(View.VISIBLE);
            posBtn.setBackgroundResource(R.drawable.alertdialog_right_selector);
            negBtn.setVisibility(View.VISIBLE);
            negBtn.setBackgroundResource(R.drawable.alertdialog_left_selector);
            img_line.setVisibility(View.VISIBLE);
        }

        if (showPosBtn && !showNegBtn) {
            posBtn.setVisibility(View.VISIBLE);
            posBtn.setBackgroundResource(R.drawable.alertdialog_single_selector);
        }

        if (!showPosBtn && showNegBtn) {
            negBtn.setVisibility(View.VISIBLE);
            negBtn.setBackgroundResource(R.drawable.alertdialog_single_selector);
        }
    }

    public void show() {
        if (context instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) context;
            if (!activity.isFinishing()) {
                setLayout();
                if (ClickBlankCancellation) {
                    dialog.setCanceledOnTouchOutside(true);
                } else {
                    dialog.setCanceledOnTouchOutside(false);
                }
                dialog.show();
            }
        }

    }

    public Dialog getDialog() {
        return dialog;
    }

    public interface ActionListener {
        void onClick(Dialog dialog, int index);
    }

    public IOSAlertDialog setCommitDelay(int seconds) {
        if (subscribe != null) {
            subscribe.dispose();
        }
        posBtn.setEnabled(false);
        posBtn.setTextColor(context.getResources().getColor(R.color.dialog_line));
        subscribe = Observable.interval(0, 1, TimeUnit.SECONDS, Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    String text = posBtn.getText().toString();
                    StringBuilder stringBuilder = new StringBuilder(text);
                    if (!TextUtils.isEmpty(stringBuilder)) {
                        int second = seconds - aLong.intValue();
                        int index = stringBuilder.indexOf("(");
                        if (index > 0) {
                            stringBuilder.deleteCharAt(index + 1);
                            stringBuilder.insert(index + 1, second);
                        } else {
                            stringBuilder.append("(").append(second).append(")");
                        }
                        posBtn.setText(stringBuilder.toString());
                    }
                    if (aLong.intValue() == seconds) {
                        posBtn.setTextColor(context.getResources().getColor(R.color.actionsheet_red));
                        posBtn.setText(stringBuilder.delete(stringBuilder.indexOf("("),stringBuilder.indexOf(")")+1));
                        posBtn.setEnabled(true);
                        subscribe.dispose();
                    }
                });
        mDisposable.add(subscribe);
        return this;
    }
}
