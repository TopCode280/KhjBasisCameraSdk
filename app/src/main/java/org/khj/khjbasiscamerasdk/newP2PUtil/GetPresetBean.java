package org.khj.khjbasiscamerasdk.newP2PUtil;

import java.util.List;

public class GetPresetBean {

    public String methodName;
    public int requestId;
    public List<Arg> arg;


    public class Arg {
        public String presetName;
        public String imageName;
        public Long timestamp;
        public Long presetId;
    }
}
