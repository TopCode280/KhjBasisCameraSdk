package org.khj.khjbasiscamerasdk.utils;

import com.vise.log.ViseLog;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastServer {
    String groupHost = "224.0.1.2";  //组播组虚拟IP
    int port = 6008;  //端口
    private MulticastSocket multicastSocket;
    private InetAddress inetAddress;

    public MulticastServer() {
        try {
            //MulticastSocket实例
            multicastSocket = new MulticastSocket(port);
            //组地址
            inetAddress = InetAddress.getByName(groupHost);
            multicastSocket.joinGroup(inetAddress); //加入到组播组中
        } catch (Exception exception) {
            exception.printStackTrace(); //输出出错信息
        }
    }

    public void sendData(byte[] data, String groupurl){// 向组播组发送数据的函数
        try {
            InetAddress group = InetAddress.getByName(groupurl);
            // 存储在数组中
            DatagramPacket packet = new DatagramPacket(data, data.length, group, port); // 初始化DatagramPacket
            multicastSocket.send(packet); // 通过MulticastSocket实例端口向组播组发送数据
            ViseLog.d("以UDP形式发送组播报文");
        } catch (Exception e1) {
            System.out.println("Error: " + e1); // 捕捉异常情况
        }
    }

    public String recieveData() {
        String message;
        try {

            byte[] data = new byte[512];
            DatagramPacket packet = new DatagramPacket(data, data.length, inetAddress, port);
            multicastSocket.receive(packet); // 通过MulticastSocket实例端口从组播组接收数据
            // 将接受的数据转换成字符串形式
            message = new String(packet.getData(), 0, packet.getLength());
        } catch (Exception e1) {
            System.out.println("Error: " + e1); // 捕捉异常情况
            message = "Error: " + e1;
        }
        return message;
    }


    public void release() {
        multicastSocket.close();
        multicastSocket = null;
    }

}
