package org.khj.khjbasiscamerasdk.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import org.khj.khjbasiscamerasdk.R;
import org.khj.khjbasiscamerasdk.bean.FileInfoWrapper;

public class SDVideoAdapter extends BaseQuickAdapter<FileInfoWrapper, BaseViewHolder> {
    public SDVideoAdapter() {
        super(R.layout.item_sdcard_video);
    }


    @Override
    protected void convert(BaseViewHolder helper, FileInfoWrapper fileInfo) {
        TextView fileName=helper.getView(R.id.tv_fileName);
        fileName.setText(fileInfo.filename);
//        ImageView ivDownload=helper.getView(R.id.iv_download);
//        ImageView ivPlayVideo=helper.getView(R.id.iv_play);
        ImageView ivDownload=helper.getView(R.id.tv_download);
        if (fileInfo.hasDownload){
            ivDownload.setVisibility(View.INVISIBLE);
        }else {
            ivDownload.setVisibility(View.VISIBLE);
            helper.addOnClickListener(R.id.tv_download);
        }


    }


}
