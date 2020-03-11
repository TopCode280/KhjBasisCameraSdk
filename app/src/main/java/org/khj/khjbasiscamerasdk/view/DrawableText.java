package org.khj.khjbasiscamerasdk.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import com.qmuiteam.qmui.util.QMUIDisplayHelper;

import org.khj.khjbasiscamerasdk.R;

public class DrawableText extends AppCompatTextView {

    private Drawable drawableLeft;
    private int scaleWidth; //dp值
    private int scaleHeight;

    public DrawableText(Context context) {
        super(context);
    }

    public DrawableText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public DrawableText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ImageTextButton);

        drawableLeft = typedArray.getDrawable(R.styleable.ImageTextButton_leftDrawable);
        scaleWidth = typedArray.getDimensionPixelOffset(R.styleable
                .ImageTextButton_drawableWidth, QMUIDisplayHelper.dp2px(getContext(),20));
        scaleHeight = typedArray.getDimensionPixelOffset(R.styleable
                .ImageTextButton_drawableHeight, QMUIDisplayHelper.dp2px(getContext(),20));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (drawableLeft != null) {
            drawableLeft.setBounds(0, 0, QMUIDisplayHelper.dp2px(getContext(),scaleWidth), QMUIDisplayHelper.dp2px(getContext(),scaleHeight));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.setCompoundDrawables(drawableLeft, null, null, null);
    }

    /**
     * 设置左侧图片并重绘
     * @param drawableLeft
     */
    public void setDrawableLeft(Drawable drawableLeft) {
        this.drawableLeft = drawableLeft;
        invalidate();
    }

    /**
     * 设置左侧图片并重绘
     * @param drawableLeftRes
     */
    public void setDrawableLeft(int drawableLeftRes) {
        this.drawableLeft = getContext().getResources().getDrawable(drawableLeftRes);
        invalidate();
    }
}

