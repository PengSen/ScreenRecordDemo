//package com.ob.demo.util;
//
//import android.util.Log;
//
//import java.io.IOException;
//import java.net.DatagramPacket;
//import java.net.DatagramSocket;
//import java.net.InetAddress;
//import java.util.Timer;
//import java.util.TimerTask;
//
///**
// * Created by pengs on 2017/3/1.
// * UDP帮助类
// */
//
//public class UdpMessageUtil {
//    private byte[] bytes = new byte[1024];
//    // DatagramSocket代表UDP协议的Socket,作用就是接收和发送数据报
//    private DatagramSocket mDatagramSocket = null;
//
//    private UdpMessageUtil() {
//
//    }
//
//    //内部类单例
//    private static class SingleUdpMessage {
//        private static UdpMessageUtil instance = new UdpMessageUtil();
//    }
//
//    public static UdpMessageUtil getInstance() {
//        return SingleUdpMessage.instance;
//    }
//
////    // 设置超时时间
////    public final void setTimeOut(final int timeout) throws Exception {
////        mDatagramSocket.setSoTimeout(timeout);
////    }
//
//    /**
//     * 向指定的服务端发送数据信息
//     * @param host 服务器主机地址
//     * @param serverPort 服务端端口
//     * @param bytes 发送的数据信息
//     * @param localPort 本地端口
//     */
//    public void send(String host, int serverPort, byte[] bytes, int localPort) {
//        try {
//            if (mDatagramSocket == null) {
//                mDatagramSocket = new DatagramSocket(localPort);
//            }
//            DatagramPacket dp = new DatagramPacket(bytes, bytes.length, InetAddress.getByName(host), serverPort);
//            mDatagramSocket.send(dp);
//        } catch (Exception e) {
//            Log.e("pds", "Exception: "+e.toString());
////            close();
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     *  接收从指定的服务端发回的数据
//     * @param port 终端端口
//     * @return 服务端发回的数据
//     */
//    public byte[] receiveData(int port) {
//        DatagramPacket dp = new DatagramPacket(bytes, bytes.length);
//        dp.setPort(port);
//        try {
//            mDatagramSocket.receive(dp);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return dp.getData();
//    }
//
//    // 关闭udp连接
//    public void close() {
//        if (mDatagramSocket != null) {
//            try {
//                mDatagramSocket.close();
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//            mDatagramSocket = null;
//        }
//    }
//}