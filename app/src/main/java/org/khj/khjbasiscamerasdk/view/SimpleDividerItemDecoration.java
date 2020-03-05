package org.khj.khjbasiscamerasdk.view;


import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import org.khj.khjbasiscamerasdk.R;

public class SimpleDividerItemDecoration extends RecyclerView.ItemDecoration {
    private int dividerHeight;


    public SimpleDividerItemDecoration(Context context) {
        dividerHeight = context.getResources().getDimensionPixelSize(R.dimen.divider_height);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom = dividerHeight;//类似加了一个bottom padding
    }
}
