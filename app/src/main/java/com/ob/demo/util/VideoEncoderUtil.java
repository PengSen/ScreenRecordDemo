package com.ob.demo.util;


import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.projection.MediaProjection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.Surface;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class VideoEncoderUtil {
    private Encoder encoder;
    private MediaProjection mediaProjection;
    private VirtualDisplay virtualDisplay;
    private long timeStamp = 0;
    private int secondFrame = 5000;//5s一帧关键帧, GOP
    private String ip;

    public VideoEncoderUtil(MediaProjection mediaProjection, String ip) {
        this.mediaProjection = mediaProjection;
        this.ip = ip;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void onSurfaceCreated(Surface surface, int mWidth, int mHeight) {
        virtualDisplay = mediaProjection.createVirtualDisplay("-display",
                mWidth, mHeight, 1, DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC,
                surface, null, null);//将屏幕数据与surface进行关联

    }

    private void onSurfaceDestroyed(Surface surface) {
        virtualDisplay.release();
        surface.release();
    }


    public void start() {
        if (encoder == null) {
            encoder = new Encoder();
        }
        new Thread(encoder).start();
    }

    public void stop() {
        if (encoder != null) {
            encoder.release();
            encoder = null;
        }
    }


    private class Encoder implements Runnable {

        String MIME_TYPE = "video/avc";//编码格式,  h264
        int VIDEO_FRAME_PER_SECOND = 20;//fps
        int VIDEO_I_FRAME_INTERVAL = 5;
        private int mWidth = 1280;//大屏上会因为分辨率显示马赛克
        private int mHeight = 720;
        private int VIDEO_BITRATE = 2 * 1024 * 1024; //2M码率
//      private int VIDEO_BITRATE = 500 * 1024; //500K码率,有兴趣的可以看看2M和500K的区别~
        /**
         * 子线程的hanlder
         */
        private Handler threadHandler;
        private DatagramSocket mDatagramSocket;
        private MediaCodec mCodec;
        private Surface mSurface;
        private Bundle params = new Bundle();

        Encoder() {
            try {
                if(mDatagramSocket == null){
                    mDatagramSocket = new DatagramSocket(null);
                    mDatagramSocket.setReuseAddress(true);
                    mDatagramSocket.bind(new InetSocketAddress(6666));
                }
            } catch (SocketException e) {
                e.printStackTrace();
            }
            params.putInt(MediaCodec.PARAMETER_KEY_REQUEST_SYNC_FRAME, 0);//做Bundle初始化  主要目的是请求编码器“即时”产生同步帧
            prepare();
        }

        @Override
        public void run() {
            Looper.prepare();
            threadHandler = new Handler() {

                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    byte[] dataFrame = (byte[]) msg.obj;
                    int frameLength = dataFrame.length;
                    byte[] lengthByte = TyteUtil.intToByteArray(frameLength);
                    byte[] concat = ArrayUtil.concat(lengthByte, dataFrame);
                    Log.e("pds", concat.length+"长度数组:" + Arrays.toString(concat));
                    try {
                        DatagramPacket dp = new DatagramPacket(concat, concat.length, InetAddress.getByName(ip), 2333);
                        mDatagramSocket.send(dp);
                    } catch (IOException e) {
                        Log.e("pds", "IOException:"+e.toString());
                        e.printStackTrace();
                    }
                }

            };
            Looper.loop();
        }

        void sendData(byte[] data) {
            Message message = new Message();
            message.obj = data;
            threadHandler.sendMessage(message);
        }

        private void release() {
            onSurfaceDestroyed(mSurface);
            if (mCodec != null) {
                mCodec.stop();
                mCodec.release();
                mCodec = null;
            }
        }
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        private boolean prepare() {
            MediaFormat format = MediaFormat.createVideoFormat(MIME_TYPE, mWidth, mHeight);
            //COLOR_FormatSurface这里表明数据将是一个graphicbuffer元数据s
            format.setInteger(MediaFormat.KEY_COLOR_FORMAT,
                    MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
            format.setInteger(MediaFormat.KEY_BIT_RATE, VIDEO_BITRATE);//编码器需要, 解码器可选
            format.setInteger(MediaFormat.KEY_FRAME_RATE, VIDEO_FRAME_PER_SECOND);
            format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, VIDEO_I_FRAME_INTERVAL);//帧间隔  这个参数在很多手机上无效, 第二帧关键帧过了之后全是P帧。 不过可以用其它方法，方法具体忘记出处了抱歉... 全局搜关键字GOP

            try {
                mCodec = MediaCodec.createEncoderByType(MIME_TYPE);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            mCodec.setCallback(new MediaCodec.Callback() {
                @Override
                public void onInputBufferAvailable(@NonNull MediaCodec codec, int index) {
                }

                @Override
                public void onOutputBufferAvailable(@NonNull MediaCodec codec, int index, @NonNull MediaCodec.BufferInfo info) {
                    if (index > -1) {
                        ByteBuffer outputBuffer = codec.getOutputBuffer(index);
                        byte[] data = new byte[info.size];
                        assert outputBuffer != null;
                        outputBuffer.get(data);
                        sendData(data);
                        codec.releaseOutputBuffer(index, false);
                    }
                    if (System.currentTimeMillis() - timeStamp >= secondFrame) {//5秒后，设置请求关键帧的参数    GOP
                        timeStamp = System.currentTimeMillis();
                        codec.setParameters(params);
                    }
                }

                @Override
                public void onError(@NonNull MediaCodec codec, @NonNull MediaCodec.CodecException e) {
                    codec.reset();
                }

                @Override
                public void onOutputFormatChanged(@NonNull MediaCodec codec, @NonNull MediaFormat format) {

                }
            });
            mCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            //创建关联的输入surface
            mSurface = mCodec.createInputSurface();
            mCodec.start();
            onSurfaceCreated(mSurface, mWidth, mHeight);
            return true;
        }

    }
//    转换一个视频（各项参数都很高），转换参数假设：帧率20fps，分辨率640*480,，去掉声音。
//    那么按照此参数，视频中一个像素点占据2个字节，
//    一帧就占用：640*480*2=614400个字节，
//            20帧就占用：614400*20=12288000个字节，
//    也就是每秒：12288000*8=98304000=98304k比特，也即：比特率为98304kbps
//    也就是说，在“分辨率与帧率”都已经确定的情况下，视频应有的、固有的比特率就会被唯一确定下来（至于采用H264或者AVC编码压缩，实质上还是跟刚才计算的“固有的”比特率成正比例缩小，假设压缩为原来的1%，其实还是是相当于固定码率983k）。


}
