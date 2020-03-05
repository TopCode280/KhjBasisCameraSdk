package org.khj.khjbasiscamerasdk.utils;

import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import com.vise.log.ViseLog;

/**
 * Created by Administrator on 2018/3/29.
 */

public class MyAudioTrack {
    private int mFrequency;// 采样率
    private int mChannel;// 声道
    private int mSampBit;// 采样精度
    private AudioTrack mAudioTrack;

    public MyAudioTrack(int frequency, int channel, int sampbit) {
        this.mFrequency = frequency;
        this.mChannel = channel;
        this.mSampBit = sampbit;
    }

    /**
     * 初始化
     */
    public void init() {
        if (mAudioTrack != null) {
            release();
        }
        // 获得构建对象的最小缓冲区大小
        int minBufSize = getMinBufferSize();
        ViseLog.w("minBufSize:" + minBufSize);
        mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                mFrequency, mChannel, mSampBit, 2 * minBufSize, AudioTrack.MODE_STREAM);
        if (mAudioTrack.getState() == AudioTrack.STATE_INITIALIZED) {
            mAudioTrack.play();
        }

    }

    /**
     * 释放资源
     */
    public void release() {
        if (mAudioTrack != null) {
            mAudioTrack.release();
        }
    }

    /**
     * 将解码后的pcm数据写入audioTrack播放
     *
     * @param data   数据
     * @param offset 偏移
     * @param length 需要播放的长度
     */
    public void playAudioTrack(byte[] data, int offset, int length) {
//        KLog.d("播放pcm"+length);
        if (data == null || data.length == 0) {
            return;
        }

        try {
            if (mAudioTrack.getState() == AudioTrack.STATE_INITIALIZED){
                mAudioTrack.write(data, offset, length);
            }
        } catch (Exception e) {
            ViseLog.e("MyAudioTrack", "AudioTrack Exception : " + e.toString());
        }
    }


    public int getMinBufferSize() {
        return AudioTrack.getMinBufferSize(mFrequency,
                mChannel, mSampBit);
    }
}
