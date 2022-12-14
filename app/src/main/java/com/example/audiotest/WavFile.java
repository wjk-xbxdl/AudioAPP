package com.example.audiotest;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * a util class to help saving pcm data as wave file.
 * allocate a object of class WavFile, then call loadFile to specify a file end up with '.wav' of which to save data.
 * then call write to save pcm data in wave file.
 * after saving all data to wave file, call finishWrite to finish saving, and the data will flush to disk.
 */
public class WavFile {
    private static final String TAG = "WavFile";
    RandomAccessFile randomAccessFile;
    private int bytesWritten = 0;
    private boolean mFinishWritten = false;

    //header
    private final int riffId = 0x46464952;
    private int riffSize;
    private final int riffFormat = 0x45564157;
    private final int formatId = 0x20746d66;
    private final int formatSize = 16;
    private final short audioFormat = 1;
    private short channelCount ;
    private int sampleRate;
    private int byteRate;
    private short blockAlign; //bytes per frame
    private short bitsPerSample;
    private final int dataId = 0x61746164;
    private int dataSize;

    public WavFile(int sampleRate, int bitsPerSample, int channelCount) {
        this.channelCount = (short)channelCount;
        this.sampleRate = sampleRate;
        this.byteRate = (bitsPerSample / 8) * channelCount * sampleRate;
        this.blockAlign = (short)((bitsPerSample / 8) * channelCount);
        this.bitsPerSample = (short)bitsPerSample;
    }

    public void loadFile(File file) throws IOException, IllegalArgumentException {
        if (file != null) {
            if (!file.getAbsolutePath().endsWith(".wav")) {
                throw new IllegalArgumentException("file name must end up with .wav");
            }
            randomAccessFile = new RandomAccessFile(file, "rw");
            randomAccessFile.setLength(44);
            randomAccessFile.seek(44);
        } else {
            throw new IllegalArgumentException("file can not be null");
        }
    }

    public int write(byte[] data, int offset, int dataSize)
            throws IllegalArgumentException, IOException {
        if (offset < 0 || dataSize < 0) {
            throw new IllegalArgumentException("offset or dataSize should greater than 0");
        }
        if (data == null) {
            throw new IllegalArgumentException("data can not be null");
        }
        if ((offset + dataSize) > data.length) {
            Log.e(TAG, "offset plus dataSize greanter than this size of data");
            dataSize = data.length - offset;
        }
        if (mFinishWritten) {
            Log.e(TAG, "the file has finish written");
            return 0;
        }
        if (randomAccessFile == null) {
            Log.e(TAG, "FileOuputStream didn't open");
            return 0;
        }
        randomAccessFile.write(data, offset, dataSize);
        bytesWritten += dataSize;
        return dataSize;
    }

    public void finishWrite() {
        if (!mFinishWritten) {
            if (randomAccessFile != null) {
                try {
                    randomAccessFile.seek(0);
                    this.dataSize = bytesWritten;
                    this.riffSize = bytesWritten + 36;
                    writeHeader(randomAccessFile);
                    randomAccessFile.close();
                } catch (IOException e) {
                    Log.e(TAG, "write wav file header fail: " + e);
                }
                randomAccessFile = null;
            }
            mFinishWritten = true;
        }
    }

    private void writeHeader(RandomAccessFile file) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(44);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt(riffId);
        buffer.putInt(riffSize);
        buffer.putInt(riffFormat);
        buffer.putInt(formatId);
        buffer.putInt(formatSize);
        buffer.putShort(audioFormat);
        buffer.putShort(channelCount);
        buffer.putInt(sampleRate);
        buffer.putInt(byteRate);
        buffer.putShort(blockAlign);
        buffer.putShort(bitsPerSample);
        buffer.putInt(dataId);
        buffer.putInt(dataSize);
        file.write(buffer.array());
        buffer.clear();
    }
}
