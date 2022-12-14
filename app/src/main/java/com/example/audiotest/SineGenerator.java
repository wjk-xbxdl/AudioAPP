package com.example.audiotest;

import android.media.AudioFormat;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * a class to help generate pcm data of sine.
 * limit: only support encoding format of ENCODING_PCM_16BIT/ENCODING_PCM_8BIT/ENCODING_PCM_FLOAT
 */
public class SineGenerator {
    private static final String TAG = "SineGenerator";
    private static final double PI = 3.141592653589793;
    private int frequency;
    private int sampleRate;
    private int channelCount;
    private int encoding;
    private int bytesPerSample;
    private ByteBuffer byteBuffer;
    private long currentFrames = 0;

    public SineGenerator(int frequency, AudioFormat audioFormat) {
        Log.v(TAG, "frequency=" + frequency
                + ", sampleRate=" + sampleRate
                + ", channelCount=" + channelCount);
        this.frequency = frequency;
        sampleRate = audioFormat.getSampleRate();
        channelCount = audioFormat.getChannelCount();
        encoding = audioFormat.getEncoding();
        if (encoding == AudioFormat.ENCODING_PCM_8BIT) {
            bytesPerSample = 1;
        } else if (encoding == AudioFormat.ENCODING_PCM_16BIT) {
            bytesPerSample = 2;
        } else if (encoding == AudioFormat.ENCODING_PCM_FLOAT) {
            bytesPerSample = 4;
        } else {
            Log.e(TAG, "error encoding " + encoding + ", use PCM_16BIT insteand");
            encoding = AudioFormat.ENCODING_PCM_16BIT;
            bytesPerSample = 2;
        }
    }

    public byte[] generateSine(int numFrames) {
        if (null != byteBuffer) {
            byteBuffer.clear();
            //byteBuffer = null;
        }
        byteBuffer = ByteBuffer.allocate(numFrames * channelCount * bytesPerSample);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        if (encoding == AudioFormat.ENCODING_PCM_8BIT) {
            for (int i = 0; i < numFrames; i++) {
                byte data = (byte) (Math.sin((i + currentFrames) * 2 * PI * frequency / sampleRate)  * Byte.MAX_VALUE);
                for (int j = 0; j < channelCount; j++) {
                    byteBuffer.put(data);
                }
            }
        } else if (encoding == AudioFormat.ENCODING_PCM_16BIT) {
            for (int i = 0; i < numFrames; i++) {
                short data = (short) (Math.sin((i + currentFrames) * 2 * PI * frequency / sampleRate)  * Short.MAX_VALUE);
                for (int j = 0; j < channelCount; j++) {
                    byteBuffer.putShort(data);
                }
            }
        } else {
            for (int i = 0; i < numFrames; i++) {
                float data = (float) Math.sin((i + currentFrames) * 2 * PI * frequency / sampleRate);
                for (int j = 0; j < channelCount; j++) {
                    byteBuffer.putFloat(data);
                }
            }
        }
        currentFrames += numFrames;
        return byteBuffer.array();
    }

}
