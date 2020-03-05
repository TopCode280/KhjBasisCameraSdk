package org.khj.khjbasiscamerasdk.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

public class SimpleMiddleDividerItemDecoration extends RecyclerView.ItemDecoration {
    private int dividerHeight;
    private Paint mPaint;


    public SimpleMiddleDividerItemDecoration(Context context) {
        dividerHeight = 1;
        mPaint=new Paint();
        mPaint.setColor(Color.parseColor("#777777"));
    }

    @Override
    public void getItemOffsets(@NotNull Rect outRect, @NotNull View view, @NotNull RecyclerView parent, @NotNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

            outRect.bottom = dividerHeight;//类似加了一个bottom padding


    }
    @Override
    public void onDraw(@NotNull Canvas c, @NotNull RecyclerView parent, @NotNull RecyclerView.State state) {
        super.onDraw(c, parent, state);

        int childCount = parent.getChildCount();

        for ( int i = 0; i < childCount; i++ ) {
            View view = parent.getChildAt(i);
//
//            int index = parent.getChildAdapterPosition(view);
//            //第一个ItemView不需要绘制
//            if ( index == 0 ) {
//                continue;
//            }

            float dividerTop = view.getBottom();
            float dividerLeft = parent.getPaddingLeft();
            float dividerBottom = view.getBottom()+dividerHeight;
            float dividerRight = parent.getWidth() - parent.getPaddingRight();

            c.drawRect(dividerLeft,dividerTop,dividerRight,dividerBottom,mPaint);
        }
    }



    }
