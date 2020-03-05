package org.khj.khjbasiscamerasdk.utils;

import android.media.AudioFormat;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.vise.log.ViseLog;

/**
 * Created by Administrator on 2018/3/29.
 */
public class AACDecoderUtil {
    private static final String TAG = "AACDecoderUtil";
    //声道数
    private static final int KEY_CHANNEL_COUNT = 1;
    //采样率
    private static final int KEY_SAMPLE_RATE = 8000;
    //用于播放解码后的pcm
    private MyAudioTrack mPlayer;
    //解码器
    private MediaCodec mDecoder;
    //用来记录解码失败的帧数
    private int count = 0;
    //初始化解码器需要读取adts头第2,3字节的数据
    private boolean initCSD;
    public volatile boolean  isPausing;

    public AACDecoderUtil() {
        mPlayer = new MyAudioTrack(KEY_SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
        mPlayer.init();
    }


    //返回解码失败的次数
    public int getCount() {
        return count;
    }

    /**
     * 暂停播放
     */
    public void pause(){
        isPausing=true;
    }

    /**
     * 恢复播放
     */
    public void replay(){
        isPausing=false;
    }
    public boolean decodePCM(byte[] buf, int offset, int length){

        if (mPlayer!=null){
            if (!isPausing){
                mPlayer.playAudioTrack(buf, 0, length);

            }

        }

        return true;
    }



    /**
     * 释放资源
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void stop() {
        ViseLog.d("decoderStop");
        try {
            if (mPlayer != null) {
                mPlayer.release();
                mPlayer = null;
            }
            if (mDecoder != null) {
                mDecoder.stop();
                mDecoder.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
