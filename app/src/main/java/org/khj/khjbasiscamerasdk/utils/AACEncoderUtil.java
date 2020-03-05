package org.khj.khjbasiscamerasdk.utils;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaRecorder;
import com.vise.log.ViseLog;
import java.nio.ByteBuffer;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class AACEncoderUtil {
    //声道数
    private static final int KEY_CHANNEL_COUNT = 1;
    //采样率
    private static final int KEY_SAMPLE_RATE = 8000;
    //相对于上面的音频录制，我们需要一个编码器的实例
    private static int audioSource = MediaRecorder.AudioSource.MIC;
    // 设置音频的录制的声道CHANNEL_IN_STEREO为双声道，CHANNEL_CONFIGURATION_MONO为单声道  
    private static int channelConfig = AudioFormat.CHANNEL_IN_MONO;
    // 音频数据格式:PCM 16位每个样本。保证设备支持。PCM 8位每个样本。不一定能得到设备支持。  
    private static int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    private AudioRecord audioRecord;
    public boolean isRecord = false;// 设置正在录制的状态
    private int bufferSize;

    private MediaCodec mEncoder;
    MediaCodec.BufferInfo mBufferInfo;
    String MIME_TYPE = "audio/mp4a-latm";
    int KEY_BIT_RATE = 14500;
    int KEY_AAC_PROFILE = MediaCodecInfo.CodecProfileLevel.AACObjectLC;
    private ByteBuffer[] inputBuffers;
    private ByteBuffer[] outputBuffers;
    private byte[] mFrameByte;
    private final String TAG = "AudioEncoder";
    private Worker worker;
    private LinkedBlockingQueue<AACFrame> AACList;
    private LinkedBlockingQueue<byte[]> PCMList;
    private volatile boolean isPausing;
    private boolean hasPrepared;
    private boolean isPCM = true;


    public enum MyAudioFormart {AAC, G711A}

    public boolean prepare(MyAudioFormart formart) {
        //音频录制实例化和录制过程中需要用到的数据
        bufferSize = AudioRecord.getMinBufferSize(KEY_SAMPLE_RATE, channelConfig, audioFormat);
        //实例化AudioRecord
        audioRecord = new AudioRecord(audioSource, KEY_SAMPLE_RATE, channelConfig,
                audioFormat, bufferSize * 2);
        if (audioRecord.getRecordingState() != AudioRecord.STATE_INITIALIZED) {
            return false;
        }
        try {
            audioRecord.startRecording();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return false;
        }
        ;
//        LogHelper.logE("shurun",audioRecord.getSampleRate()+"pinlv");
        isRecord = true;
      /*  if (formart==MyAudioFormart.AAC){
            //配置AAC编码器
            try {
                mBufferInfo = new MediaCodec.BufferInfo();
                mEncoder = MediaCodec.createEncoderByType(MIME_TYPE);
                MediaFormat mediaFormat = MediaFormat.createAudioFormat(MIME_TYPE,
                        KEY_SAMPLE_RATE, KEY_CHANNEL_COUNT);
                mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, KEY_BIT_RATE);
                mediaFormat.setInteger(MediaFormat.KEY_AAC_PROFILE,
                        KEY_AAC_PROFILE);
                mEncoder.configure(mediaFormat, null, null,
                        MediaCodec.CONFIGURE_FLAG_ENCODE);
                mEncoder.start();
                AACList=new LinkedBlockingQueue<>(100);
                inputBuffers = mEncoder.getInputBuffers();
                outputBuffers = mEncoder.getOutputBuffers();
                isPCM=false;
            } catch (IOException e) {
                e.printStackTrace();
                KLog.e("配置AAC编码器失败");
                Toasty.error(App.context,"配置AAC编码器失败").show();
                return false;
            }
        }else {
            PCMList=new LinkedBlockingQueue<>(100);
            isPCM=true;
        }

        worker=new Worker();
        isRecord=true;
        worker.start();*/
        hasPrepared = true;
        return true;
    }

    public int getBufferSize() {
        ViseLog.e("每一帧音频大小为" + bufferSize);
        return bufferSize;
    }

    public AudioRecord getAudioRecord() {
        return audioRecord;
    }

    public boolean ifPrepared() {
        return hasPrepared;
    }

    public void stop() {
        ViseLog.e("stopRecord");
        isRecord = false;
    }

    public void pause() {
        isPausing = true;
    }

    public void play() {
        isPausing = false;
        ViseLog.e("开始录音");
    }

    public AACFrame getAACData() {
        try {
            AACFrame aacFrame = AACList.take();

            return aacFrame;
        } catch (Exception e) {
            e.printStackTrace();
            ViseLog.e("获取AAC数据出错");
            return null;
        }
    }

    public byte[] getPCMData() {
        try {
            byte[] pcmData = PCMList.poll(40, TimeUnit.MILLISECONDS);
            return pcmData;
        } catch (Exception e) {
            e.printStackTrace();
            ViseLog.e("获取pcm数据出错");
            return null;
        }
    }


    private void encode(byte[] data) {

        int inputBufferIndex = mEncoder.dequeueInputBuffer(0);
//        ViseLog.i("获取inputBufferIndex："+inputBufferIndex);
        if (inputBufferIndex >= 0) {
            ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
            inputBuffer.clear();
            inputBuffer.put(data);
            inputBuffer.limit(data.length);
            mEncoder.queueInputBuffer(inputBufferIndex, 0, data.length,
                    System.nanoTime(), 0);
        }

        int outputBufferIndex = mEncoder.dequeueOutputBuffer(mBufferInfo, 0);
//        ViseLog.i("获取outputBufferIndex："+outputBufferIndex);
        while (outputBufferIndex >= 0) {
            ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
            //给adts头字段空出7的字节
            int length = mBufferInfo.size + 7;
            mFrameByte = new byte[length];
            addADTStoPacket(mFrameByte, length);
            outputBuffer.get(mFrameByte, 7, mBufferInfo.size);
            if (mBufferInfo.size > 20) {
                AACFrame frame = new AACFrame(length, mFrameByte);
                try {
                    AACList.add(frame);
//                    ViseLog.i("编码一帧AAC，加入队列");
                } catch (Exception e) {
                    AACList.clear();
                }
            }
            mEncoder.releaseOutputBuffer(outputBufferIndex, false);
            outputBufferIndex = mEncoder.dequeueOutputBuffer(mBufferInfo, 0);
        }
    }

    public static class AACFrame {
        public int length;
        public byte[] frameData;

        public AACFrame(int length, byte[] frameData) {
            this.length = length;
            this.frameData = frameData;
        }
    }


    /**
     * 给编码出的aac裸流添加adts头字段
     *
     * @param packet    要空出前7个字节，否则会搞乱数据
     * @param packetLen
     */
    private void addADTStoPacket(byte[] packet, int packetLen) {
        int profile = 2;  //AAC LC
        int freqIdx = 11;  //8KHz
        int chanCfg = 1;  //CPE
        packet[0] = (byte) 0xFF;
        packet[1] = (byte) 0xF9;
        packet[2] = (byte) (((profile - 1) << 6) + (freqIdx << 2) + (chanCfg >> 2));
        packet[3] = (byte) (((chanCfg & 3) << 6) + (packetLen >> 11));
        packet[4] = (byte) ((packetLen & 0x7FF) >> 3);
        packet[5] = (byte) (((packetLen & 7) << 5) + 0x1F);
        packet[6] = (byte) 0xFC;
    }

    private class Worker extends Thread {
        private byte[] mBuffer;

        @Override
        public void run() {
            while (isRecord) {
                mBuffer = new byte[bufferSize];
                int num = audioRecord.read(mBuffer, 0, 512);
                if (num < 100) {
                    continue;
                }
//                Log.d(TAG, "buffer = " + mBuffer.toString() + ", num = " + num);
                if (isPausing) {
                    //暂停时候，数据抛弃
                } else {
                    if (isPCM) {
                        try {
                            byte[] realBuffer = new byte[512];
                            System.arraycopy(mBuffer, 0, realBuffer, 0, 512);
                            PCMList.add(realBuffer);
                        } catch (Exception e) {
                            ViseLog.e("满了");
                            PCMList.clear();
                        }

                    } else {
                        encode(mBuffer);
                    }


                }

            }

        }


    }

    /**
     * 释放资源
     */
    public void release() {
        hasPrepared = false;
        if (mEncoder != null) {
            mEncoder.stop();
            mEncoder.release();
        }
        if (audioRecord != null) {
            try {

                audioRecord.release();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }

            audioRecord = null;

        }

    }
}
    
    
	


