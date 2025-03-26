package org.khj.khjbasiscamerasdk.newP2PUtil;

import java.util.Random;

public class MethodArgBean {

    public String presetName;
    public int setType;
    public Long presetId;

    public static Long getPresetId() {
        String time = "" + System.nanoTime();
        time = time.substring(time.length() - 4);
        int number = new Random().nextInt(9 - 1 + 1) + 1;
        time = number + time;
        return Long.parseLong(time);
    }
}
