package com.example.audiotest;

import android.annotation.SuppressLint;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;
import org.greenrobot.eventbus.EventBus;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class AudioPlayer {
    private final String TAG = "AudioPlayer";
    private String mPlayFilePath;
    private AudioTrack mAudioTrack;
    private Thread mPlayThread;
    private FileInputStream mFileInputStream;
    private SineGenerator mSineGenerator;
    volatile private boolean mIsPlay;
    private boolean mIsCyclicPlay;
    private boolean mIsSineSource;
    private int mStreamType;
    private int mPlaySampleRate;
    private int mPlayFormat;
    private int mPlayChannel;
    private int mPlayTime;
    public int mButtonId;
    private final Map<String, Integer> mapStreamType = new HashMap<>();

    public AudioPlayer () {
        mIsPlay = false;
        mIsCyclicPlay = false;
        mIsSineSource = true;
        mStreamType = 3;
        mPlaySampleRate = 48000;
        mPlayFormat = AudioFormat.ENCODING_PCM_16BIT;
        mPlayChannel = AudioFormat.CHANNEL_OUT_STEREO;
        mPlayTime = 10 * 1000;
        initMap();
    }

    private void initMap() {
        mapStreamType.put("CALL", 0);
        mapStreamType.put("SYSTEM", 1);
        mapStreamType.put("RING", 2);
        mapStreamType.put("MUSIC", 3);
        mapStreamType.put("ALARM", 4);
        mapStreamType.put("NOTIFICATION", 5);
        mapStreamType.put("BLUETOOTH_SCO", 6);
        mapStreamType.put("ENFORCED_AUDIBLE", 7);
        mapStreamType.put("DTMF", 8);
        mapStreamType.put("TTS", 9);
        mapStreamType.put("ACCESSIBILITY", 10);
        mapStreamType.put("GIS", 11);
        mapStreamType.put("BACKCAR", 12);
        mapStreamType.put("RDS", 13);
        mapStreamType.put("ADAS", 14);
        mapStreamType.put("SMIX", 15);
        mapStreamType.put("AUXIN", 16);
        mapStreamType.put("VOICE_ASSISTANT", 17);
        mapStreamType.put("ALTERNATE", 18);
        mapStreamType.put("BOOT", 19);
        mapStreamType.put("VIBSPK", 20);
        mapStreamType.put("REROUTING", 21);
        mapStreamType.put("PATCH", 22);
    }

    public boolean getPlayState() {
        return mIsPlay;
    }

    public void setPlayFilePath(String filePath) {
        Log.d(TAG, "filePath = " + filePath);
        if (filePath == null) {
            mIsSineSource = true;
            return;
        }
        if (!mIsPlay) {
            mPlayFilePath = filePath;
            mIsSineSource = false;
        }
    }

    public void setIsCyclicPlay(boolean cyclicP) {
        if (!mIsPlay) {
            mIsCyclicPlay = cyclicP;
        }
    }

    public void setStreamType(String streamType) {
        if (!mIsPlay && mapStreamType.containsKey(streamType)) {
            mStreamType = mapStreamType.get(streamType);
        }
    }

    public void setPlaySampleRate(String playSampleRate) {
        if (!mIsPlay) {
            mPlaySampleRate = Integer.parseInt(playSampleRate);
        }
    }

    public void setButtonId(int id) {
        mButtonId = id;
    }

    @SuppressLint("InlinedApi")
    public void setPlayFormat(String playFormat) {
        if (!mIsPlay) {
            switch (playFormat) {
                case "PCM_8_BIT":
                    mPlayFormat = AudioFormat.ENCODING_PCM_8BIT;
                    break;

                case "PCM_32_BIT":
                    mPlayFormat = AudioFormat.ENCODING_PCM_32BIT;
                    break;

                case "PCM_16_BIT":
                default:
                    mPlayFormat = AudioFormat.ENCODING_PCM_16BIT;
                    break;
            }
        }
    }

    public void setPlayChannel(String playChannel) {
        if (!mIsPlay) {
            switch (playChannel) {
                case "MONO":
                    mPlayChannel = AudioFormat.CHANNEL_OUT_MONO;
                    break;

                case "5POINT1":
                    mPlayChannel = AudioFormat.CHANNEL_OUT_5POINT1;
                    break;

                case "STEREO":
                default:
                    mPlayChannel = AudioFormat.CHANNEL_OUT_STEREO;
                    break;
            }
        }

    }

    public void setPlayTime(String playTime) {
        if (!mIsPlay) {
            switch (playTime) {
                case "30ms":
                    mPlayTime = 30;
                    break;

                case "50ms":
                    mPlayTime = 50;
                    break;

                case "100ms":
                    mPlayTime = 100;
                    break;

                case "500ms":
                    mPlayTime = 500;
                    break;

                case "1s":
                    mPlayTime = 1000;
                    break;

                case "5s":
                    mPlayTime = 5000;
                    break;

                case "10s":
                    mPlayTime = 10000;
                    break;

                case "60s":
                    mPlayTime = 60000;
                    break;

                case "30s":
                default:
                    mPlayTime = 30000;
            }
        }
    }

    Runnable mRunAble = () -> play();

    private void play() {
        Log.d(TAG, "playTime = " + mPlayTime);
        try {
            Log.d(TAG, "playing");
            byte[] playBytes = new byte[4096];
            long nowTime = System.currentTimeMillis();
            while (mIsPlay) {
                    if (mIsSineSource) {
                        mAudioTrack.write(mSineGenerator.generateSine(1024), 0, 1024 * 4);
                    } else {
                        int read = mFileInputStream.read(playBytes);
                        Log.d(TAG, "read = " + read);
                        if (read > 0) {
                            mAudioTrack.write(playBytes, 0, read);
                        } else {
                            mIsPlay = false;
                            release();
                        }
                    }
                    if ((System.currentTimeMillis() - nowTime) > mPlayTime) {
                        break;
                    }
            }
            if (mIsCyclicPlay && mIsPlay) {
                start();
            } else {
                int buttonId = mButtonId;
                stop();
                EventBus.getDefault().post(new StringEvent("play", buttonId));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() {
        Log.d(TAG, "audioTrack start");
        stop();
        try {
            if (null == mPlayThread) {
                if (null == mPlayFilePath) {
                    AudioFormat audioFormat = new AudioFormat.Builder().setChannelMask(mPlayChannel)
                            .setSampleRate(mPlaySampleRate)
                            .setEncoding(mPlayFormat)
                            .build();
                    mSineGenerator = new SineGenerator(1000, audioFormat);
                    mIsSineSource = true;
                } else {
                    Log.d(TAG, "mPlayFilePath = " + mPlayFilePath);
                    mFileInputStream = new FileInputStream(mPlayFilePath);
                    mIsSineSource = false;
                }
                Log.d(TAG, "streamType: " + mStreamType + " sampleRate " + mPlaySampleRate
                        + " format " + mPlayFormat + " channel " + mPlayChannel + " playFilePath" +
                        mPlayFilePath);
                int minBufferSize = AudioTrack.getMinBufferSize(mPlaySampleRate, mPlayChannel, mPlayFormat);
                mAudioTrack = new AudioTrack(mStreamType, mPlaySampleRate, mPlayChannel, mPlayFormat,
                        minBufferSize, AudioTrack.MODE_STREAM, AudioManager.AUDIO_SESSION_ID_GENERATE);
                mAudioTrack.play();
                mIsPlay = true;
                mPlayThread = new Thread(mRunAble);
                mPlayThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @SuppressLint("SetTextI18n")
    public void stop() {
        try {
            if (null != mPlayThread && mIsPlay) {
                try {
                    mPlayThread.interrupt();
                    mPlayThread = null;
                    mIsPlay = false;
                    mAudioTrack.stop();
                    flush();
                    mAudioTrack = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mPlayThread = null;
        }

    }

    public void flush() {
        if (mAudioTrack != null) {
            mAudioTrack.flush();
        }
    }

    public void release() {
        if (null != mAudioTrack) {
            mAudioTrack.flush();
            mAudioTrack.release();
        }
        try {
            if (null != mFileInputStream) {
                mFileInputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
