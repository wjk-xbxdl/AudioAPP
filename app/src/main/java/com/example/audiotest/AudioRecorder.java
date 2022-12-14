package com.example.audiotest;

import android.annotation.SuppressLint;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import androidx.annotation.RequiresApi;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AudioRecorder {
    private final String TAG = "AudioRecorder";
    private File mRecordFilePath;
    volatile private static int fileCount;
    private AudioRecord mAudioRecord;
    private Thread mRecordThread;
    private FileOutputStream mFileOutputStream;
    private boolean mIsRecord;
    private boolean mIsCyclicRecord;
    private int mRecordSource;
    private int mRecordSampleRate;
    private int mRecordFormat;
    private int mRecordChannel;
    private int mRecordTime;
    private int mButtonId;
    private final Map<String, Integer> mapRecordSource = new HashMap<>();

    public AudioRecorder() {
        mIsRecord = false;
        mIsCyclicRecord = false;
        mRecordTime = 10 * 1000;
        fileCount = 0;
        mRecordSource = MediaRecorder.AudioSource.MIC;
        mRecordSampleRate = 48000;
        mRecordFormat = AudioFormat.ENCODING_PCM_16BIT;
        mRecordChannel = AudioFormat.CHANNEL_IN_STEREO;
        initRecordSource();
    }

    private void initRecordSource() {
        mapRecordSource.put("DEFAULT", 0);
        mapRecordSource.put("MIC", 1);
        mapRecordSource.put("VOICE_UPLINK", 2);
        mapRecordSource.put("VOICE_DOWNLINK", 3);
        mapRecordSource.put("VOICE_CALL", 4);
        mapRecordSource.put("CAMCORDER", 5);
        mapRecordSource.put("VOICE_RECOGNITION", 6);
        mapRecordSource.put("VOICE_COMMUNICATION", 7);
        mapRecordSource.put("REMOTE_SUBMIX", 8);
        mapRecordSource.put("UNPROCESSED", 9);
        mapRecordSource.put("DIGITAL_IN", 10);
        mapRecordSource.put("ANALOG_IN_GROUP_1", 11);
        mapRecordSource.put("ANALOG_IN_GROUP_2", 12);
        mapRecordSource.put("ANALOG_IN_GROUP_3", 13);
        mapRecordSource.put("ANALOG_IN_GROUP_4", 14);
        mapRecordSource.put("AUDIO_SOURCE_FM_TUNER", 1998);
        mapRecordSource.put("AUDIO_SOURCE_HOTWORD", 1999);
    }

    public String getRecordFilePath() {
        return mRecordFilePath.toString();
    }

    public boolean getRecordState() {
        return mIsRecord;
    }

    public void setIsCyclicRecord(boolean cyclicRecord) {
        if (!mIsRecord) {
            mIsCyclicRecord = cyclicRecord;
        }
    }

    public void setRecordSource(String recordSource) {
        if (!mIsRecord && mapRecordSource.containsKey(recordSource)) {
            mRecordSource = mapRecordSource.get(recordSource);
        }
    }

    public void setRecordSampleRate(String recordSampleRate) {
        if (!mIsRecord) {
            mRecordSampleRate = Integer.parseInt(recordSampleRate);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    public void setRecordFormat(String recordFormat) {
        if (!mIsRecord) {
            switch (recordFormat) {
                case "PCM_8_BIT":
                    mRecordFormat = AudioFormat.ENCODING_PCM_8BIT;
                    break;

                case "PCM_32_BIT":
                    mRecordFormat = AudioFormat.ENCODING_PCM_32BIT;
                    break;

                case "PCM_16_BIT":
                default:
                    mRecordFormat = AudioFormat.ENCODING_PCM_16BIT;
                    break;
            }
        }
    }

    public void setRecordChannel(String recordChannel) {
        if (!mIsRecord) {
            switch (recordChannel) {
                case "MONO":
                    mRecordChannel = AudioFormat.CHANNEL_IN_MONO;
                    break;

                case "5POINT1":
                default:
                    mRecordChannel = AudioFormat.CHANNEL_IN_STEREO;
                    break;
            }
        }

    }

    public void setRecordTime(String recordTime) {
        if (!mIsRecord) {
            switch (recordTime) {
                case "30ms":
                    mRecordTime = 30;
                    break;

                case "50ms":
                    mRecordTime = 50;
                    break;

                case "100ms":
                    mRecordTime = 100;
                    break;

                case "500ms":
                    mRecordTime = 500;
                    break;

                case "1s":
                    mRecordTime = 1000;
                    break;

                case "5s":
                    mRecordTime = 5000;
                    break;

                case "10s":
                    mRecordTime = 10000;
                    break;

                case "60s":
                    mRecordTime = 60000;
                    break;

                case "30s":
                default:
                    mRecordTime = 30000;
            }
        }
    }

    public void setButtonId(int id) {
        mButtonId = id;
    }

    Runnable mRunAble = () -> record();

    private void record() {
        Log.d(TAG, "mRecordTime = " + mRecordTime);
        try {
            Log.d(TAG, "record is ok");
            byte[] recordBytes = new byte[4096];
            //playCyclicTimer();
            long nowTime = System.currentTimeMillis();
            while (mIsRecord) {
                int read = mAudioRecord.read(recordBytes, 0, 4096);
                if (read > 0) {
                    mFileOutputStream.write(recordBytes, 0, read);
                } else {
                    mIsRecord = false;
                    release();
                }
                if ((System.currentTimeMillis() - nowTime) > mRecordTime) {
                    break;
                }
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        if (mIsCyclicRecord && mIsRecord) {
            start();
        } else {
            Log.d(TAG, "is stop");
            stop();
            int buttonId = mButtonId;
            EventBus.getDefault().post(new StringEvent("record", buttonId));
        }
    }

    @SuppressLint("MissingPermission")
    public void start() {
        stop();
        try {
            if (null == mRecordThread) {
                createRecordFile();
                mFileOutputStream = new FileOutputStream(mRecordFilePath);
                Log.d(TAG, "mRecordSource: " + mRecordSource + " SampleRate " + mRecordSampleRate
                        + " format " + mRecordFormat + " channel " + mRecordChannel + " recordFilePath" +
                        mRecordFilePath);
                int minBufferSize = AudioRecord.getMinBufferSize(mRecordSampleRate,
                        mRecordChannel,
                        mRecordFormat);
                mAudioRecord = new AudioRecord(mRecordSource,
                        mRecordSampleRate,
                        mRecordChannel,
                        mRecordFormat,
                        minBufferSize);
                mAudioRecord.startRecording();
                mIsRecord = true;
                mRecordThread = new Thread(mRunAble);
                mRecordThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void stop() {
        try {
            if (null != mRecordThread && mIsRecord) {
                try {
                    mRecordThread.interrupt();
                    mIsRecord = false;
                    mRecordThread = null;
                    mAudioRecord.stop();
                    mAudioRecord = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mRecordThread = null;
        }

    }

    public void release() {
        if (null != mAudioRecord) {
            mAudioRecord.release();
        }
        try {
            if (null != mFileOutputStream) {
                mFileOutputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void createRecordFile() {
        Calendar calendarTime = Calendar.getInstance();
        int hour = calendarTime.get(Calendar.HOUR_OF_DAY);
        int minute = calendarTime.get(Calendar.MINUTE);
        int sec = calendarTime.get(Calendar.SECOND);
        String recordFileName = "/" +  hour + ":" + minute+ ":" + sec + "record" + fileCount + ".pcm";
        fileCountAdd();
        mRecordFilePath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + recordFileName);
        if (mRecordFilePath.exists()) {
            mRecordFilePath.delete();
        }
        try {
            mRecordFilePath.createNewFile();
        } catch (IOException e) {
            throw new IllegalStateException("create failed" + mRecordFilePath.toString());
        }
    }

    public synchronized void fileCountAdd() {
        if (fileCount < 20) {
            fileCount++;
        } else {
            fileCount = 0;
        }
    }


}
