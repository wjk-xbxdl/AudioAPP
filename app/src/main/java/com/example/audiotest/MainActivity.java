package com.example.audiotest;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TabHost;
import android.widget.TextView;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    final String TAG = "AudioTestApp";
    private Button buttonPlayOne;
    private Button buttonPlayTwo;
    private Button buttonPlayThree;
    private Button buttonOpenPlayFileOne;
    private Button buttonOpenPlayFileTwo;
    private Button buttonOpenPlayFileThree;
    private Button buttonRecordOne;
    private Button buttonRecordTwo;
    private Button buttonRecordThree;
    private Button buttonBroadcast;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch switchPCyclicOne;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch switchPCyclicTwo;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch switchPCyclicThree;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch switchPViceScreenOne;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch switchPViceScreenTwo;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch switchPViceScreenThree;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch switchRCyclicOne;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch switchRCyclicTwo;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch switchRCyclicThree;
    private Spinner spinnerPStreamTypeOne;
    private Spinner spinnerPSampleRateOne;
    private Spinner spinnerPFormatOne;
    private Spinner spinnerPChannelOne;
    private Spinner spinnerPCyclicTimeOne;
    private Spinner spinnerPStreamTypeTwo;
    private Spinner spinnerPSampleRateTwo;
    private Spinner spinnerPFormatTwo;
    private Spinner spinnerPChannelTwo;
    private Spinner spinnerPCyclicTimeTwo;
    private Spinner spinnerPStreamTypeThree;
    private Spinner spinnerPSampleRateThree;
    private Spinner spinnerPFormatThree;
    private Spinner spinnerPChannelThree;
    private Spinner spinnerPCyclicTimeThree;
    private Spinner spinnerRSource;
    private Spinner spinnerRSampleRate;
    private Spinner spinnerRFormat;
    private Spinner spinnerRChannel;
    private Spinner spinnerRCyclicTime;
    private Spinner spinnerSetVolume;
    private Spinner spinnerSetMode;
    private Spinner spinnerSetParameter;
    private SeekBar seekBarVolume;
    private TextView textviewPlayPathOne;
    private TextView textviewPlayPathTwo;
    private TextView textviewPlayPathThree;
    private TextView textViewRecordPathOne;
    private TextView textViewRecordPathTwo;
    private TextView textViewRecordPathThree;
    private TextView textviewVolumeValue;
    private final AudioPlayer mAudioPlayerOne = new AudioPlayer();
    private final AudioPlayer mAudioPlayerTwo = new AudioPlayer();
    private final AudioPlayer mAudioPlayerThree = new AudioPlayer();
    private final AudioRecorder mAudioRecorderOne = new AudioRecorder();
    private final AudioRecorder mAudioRecorderTwo = new AudioRecorder();
    private final AudioRecorder mAudioRecorderThree = new AudioRecorder();

    private ActivityResultLauncher<String> mGetContentOne;
    private ActivityResultLauncher<String> mGetContentTwo;
    private ActivityResultLauncher<String> mGetContentThree;
    private ActivityResultLauncher<String[]> mGetPermission;
    String[] mSignPermission = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.MODIFY_AUDIO_SETTINGS
    };

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission();
        initialize();
        initTabHost();
        initContent();


        buttonPlayOne.setOnClickListener(v -> {
            if (!mAudioPlayerOne.getPlayState()) {
                Log.d(TAG, "button play one is start");
                buttonPlayOne.setText("stop");
                mAudioPlayerOne.start();
            } else {
                Log.d(TAG, "button play one is stop");
                buttonPlayOne.setText("play");
                mAudioPlayerOne.stop();
            }
        });

        buttonPlayTwo.setOnClickListener(v -> {
            if (!mAudioPlayerTwo.getPlayState()) {
                buttonPlayTwo.setText("stop");
                mAudioPlayerTwo.start();
            } else {
                buttonPlayTwo.setText("play");
                mAudioPlayerTwo.stop();
            }
        });

        buttonPlayThree.setOnClickListener(v -> {
            if (!mAudioPlayerThree.getPlayState()) {
                buttonPlayThree.setText("stop");
                mAudioPlayerThree.start();
            } else {
                buttonPlayThree.setText("play");
                mAudioPlayerThree.stop();
            }
        });

        buttonRecordOne.setOnClickListener(v -> {
            if (!mAudioRecorderOne.getRecordState()) {
                mAudioRecorderOne.start();
                buttonRecordOne.setText("stop");
                textViewRecordPathOne.setText(mAudioRecorderOne.getRecordFilePath());
            } else {
                mAudioRecorderOne.stop();
                buttonRecordOne.setText("record");
            }
        });

        buttonRecordTwo.setOnClickListener(v -> {
            if (!mAudioRecorderTwo.getRecordState()) {
                mAudioRecorderTwo.start();
                buttonRecordTwo.setText("stop");

                textViewRecordPathTwo.setText(mAudioRecorderTwo.getRecordFilePath());
            } else {
                mAudioRecorderTwo.stop();
                buttonRecordTwo.setText("record");
            }
        });

        buttonRecordThree.setOnClickListener(v -> {
            if (!mAudioRecorderThree.getRecordState()) {
                mAudioRecorderThree.start();
                buttonRecordThree.setText("stop");
                textViewRecordPathThree.setText(mAudioRecorderThree.getRecordFilePath());
            } else {
                mAudioRecorderThree.stop();
                buttonRecordThree.setText("record");
            }
        });

        buttonOpenPlayFileOne.setOnClickListener(v -> mGetContentOne.launch("audio/x-wav"));

        buttonOpenPlayFileTwo.setOnClickListener(v -> mGetContentTwo.launch("audio/x-wav"));

        buttonOpenPlayFileThree.setOnClickListener(v -> mGetContentThree.launch("audio/x-wav"));

        buttonBroadcast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("track", "track");

            }
        });

        switchPCyclicOne.setOnCheckedChangeListener((buttonView, isChecked) -> mAudioPlayerOne.setIsCyclicPlay(isChecked));

        switchPCyclicTwo.setOnCheckedChangeListener((buttonView, isChecked) -> mAudioPlayerTwo.setIsCyclicPlay(isChecked));

        switchPCyclicThree.setOnCheckedChangeListener((buttonView, isChecked) -> mAudioPlayerThree.setIsCyclicPlay(isChecked));

        switchPViceScreenOne.setOnCheckedChangeListener((buttonView, isChecked) -> Log.d(TAG, "mSwitch = " + isChecked));

        switchPViceScreenTwo.setOnCheckedChangeListener((buttonView, isChecked) -> Log.d(TAG, "mSwitch = " + isChecked));

        switchPViceScreenThree.setOnCheckedChangeListener((buttonView, isChecked) -> Log.d(TAG, "mSwitch = " + isChecked));

        switchRCyclicOne.setOnCheckedChangeListener((buttonView, isChecked) -> mAudioRecorderOne.setIsCyclicRecord(isChecked));

        switchRCyclicTwo.setOnCheckedChangeListener((buttonView, isChecked) -> mAudioRecorderTwo.setIsCyclicRecord(isChecked));

        switchRCyclicThree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mAudioRecorderThree.setIsCyclicRecord(isChecked);
            }
        });

        spinnerPStreamTypeOne.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String streamType = (String) parent.getItemAtPosition(position);
                Log.d(TAG, "streamType: " + streamType);
                mAudioPlayerOne.setStreamType(streamType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        spinnerPSampleRateOne.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String sampleRate = (String) parent.getItemAtPosition(position);
                Log.d(TAG, "sampleRate: " + sampleRate);
                mAudioPlayerOne.setPlaySampleRate(sampleRate);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerPFormatOne.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String format = (String) parent.getItemAtPosition(position);
                Log.d(TAG, "format: " + format);
                mAudioPlayerOne.setPlayFormat(format);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerPChannelOne.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String channel = (String) parent.getItemAtPosition(position);
                Log.d(TAG, "" + channel);
                mAudioPlayerOne.setPlayChannel(channel);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerPCyclicTimeOne.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String cyclicTime = (String) parent.getItemAtPosition(position);
                Log.d(TAG, "" + cyclicTime);
                mAudioPlayerOne.setPlayTime(cyclicTime);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerPStreamTypeTwo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String streamType = (String) parent.getItemAtPosition(position);
                Log.d(TAG, "streamType: " + streamType);
                mAudioPlayerTwo.setStreamType(streamType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        spinnerPSampleRateTwo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String sampleRate = (String) parent.getItemAtPosition(position);
                Log.d(TAG, "sampleRate: " + sampleRate);
                mAudioPlayerTwo.setPlaySampleRate(sampleRate);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerPFormatTwo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String format = (String) parent.getItemAtPosition(position);
                Log.d(TAG, "format: " + format);
                mAudioPlayerTwo.setPlayFormat(format);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerPChannelTwo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String channel = (String) parent.getItemAtPosition(position);
                Log.d(TAG, "" + channel);
                mAudioPlayerTwo.setPlayChannel(channel);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerPCyclicTimeTwo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String cyclicTime = (String) parent.getItemAtPosition(position);
                Log.d(TAG, "" + cyclicTime);
                mAudioPlayerTwo.setPlayTime(cyclicTime);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerPStreamTypeThree.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String streamType = (String) parent.getItemAtPosition(position);
                Log.d(TAG, "streamType: " + streamType);
                mAudioPlayerThree.setStreamType(streamType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        spinnerPSampleRateThree.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String sampleRate = (String) parent.getItemAtPosition(position);
                Log.d(TAG, "sampleRate: " + sampleRate);
                mAudioPlayerThree.setPlaySampleRate(sampleRate);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerPFormatThree.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String format = (String) parent.getItemAtPosition(position);
                Log.d(TAG, "format: " + format);
                mAudioPlayerThree.setPlayFormat(format);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerPChannelThree.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String channel = (String) parent.getItemAtPosition(position);
                Log.d(TAG, "" + channel);
                mAudioPlayerThree.setPlayChannel(channel);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerPCyclicTimeThree.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String cyclicTime = (String) parent.getItemAtPosition(position);
                Log.d(TAG, "" + cyclicTime);
                mAudioPlayerThree.setPlayTime(cyclicTime);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerRSource.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String recordSource = (String) parent.getItemAtPosition(position);
                mAudioRecorderOne.setRecordSource(recordSource);
                mAudioRecorderTwo.setRecordSource(recordSource);
                mAudioRecorderThree.setRecordSource(recordSource);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerRSampleRate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String recordSampleRate = (String) parent.getItemAtPosition(position);
                mAudioRecorderOne.setRecordSampleRate(recordSampleRate);
                mAudioRecorderTwo.setRecordSampleRate(recordSampleRate);
                mAudioRecorderThree.setRecordSampleRate(recordSampleRate);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerRFormat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.S)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String recordFormat = (String) parent.getItemAtPosition(position);
                mAudioRecorderOne.setRecordFormat(recordFormat);
                mAudioRecorderTwo.setRecordFormat(recordFormat);
                mAudioRecorderThree.setRecordFormat(recordFormat);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerRChannel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String recordChannel = (String) parent.getItemAtPosition(position);
                mAudioRecorderOne.setRecordChannel(recordChannel);
                mAudioRecorderTwo.setRecordChannel(recordChannel);
                mAudioRecorderThree.setRecordChannel(recordChannel);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerRCyclicTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String cyclicTime = (String) parent.getItemAtPosition(position);
                mAudioRecorderOne.setRecordTime(cyclicTime);
                mAudioRecorderTwo.setRecordTime(cyclicTime);
                mAudioRecorderThree.setRecordTime(cyclicTime);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerSetVolume.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String str = (String) parent.getItemAtPosition(position);
                Log.d(TAG, "" + str);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textviewVolumeValue.setText(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        spinnerSetMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String str = (String) parent.getItemAtPosition(position);
                Log.d(TAG, "" + str);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerSetParameter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String str = (String) parent.getItemAtPosition(position);
                Log.d(TAG, "" + str);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void initialize() {
        buttonPlayOne = findViewById(R.id.button_play_one);
        buttonPlayTwo = findViewById(R.id.button_play_two);
        buttonPlayThree = findViewById(R.id.button_play_three);
        buttonRecordOne = findViewById(R.id.button_record_one);
        buttonRecordTwo = findViewById(R.id.button_record_two);
        buttonRecordThree = findViewById(R.id.button_record_three);
        buttonOpenPlayFileOne = findViewById(R.id.button_openPlay_file_one);
        buttonOpenPlayFileTwo = findViewById(R.id.button_openPlay_file_two);
        buttonOpenPlayFileThree = findViewById(R.id.button_openPlay_file_three);
        buttonBroadcast = findViewById(R.id.button_setBroadcast);
        switchPCyclicOne = findViewById(R.id.switch_cyclicPlay_one);
        switchPCyclicTwo = findViewById(R.id.switch_cyclicPlay_two);
        switchPCyclicThree = findViewById(R.id.switch_cyclicPlay_three);
        switchPViceScreenOne = findViewById(R.id.switch_viceScreen_one);
        switchPViceScreenTwo = findViewById(R.id.switch_viceScreen_two);
        switchPViceScreenThree = findViewById(R.id.switch_viceScreen_three);
        switchRCyclicOne = findViewById(R.id.switch_cyclicRecord_one);
        switchRCyclicTwo = findViewById(R.id.switch_cyclicRecord_two);
        switchRCyclicThree = findViewById(R.id.switch_cyclicRecord_three);
        spinnerPStreamTypeOne = findViewById(R.id.spinner_streamType_one);
        spinnerPSampleRateOne = findViewById(R.id.spinner_play_sampleRate_one);
        spinnerPFormatOne = findViewById(R.id.spinner_play_format_one);
        spinnerPChannelOne = findViewById(R.id.spinner_play_channel_one);
        spinnerPCyclicTimeOne = findViewById(R.id.spinner_P_cyclic_time_one);
        spinnerPStreamTypeTwo = findViewById(R.id.spinner_streamType_two);
        spinnerPSampleRateTwo = findViewById(R.id.spinner_play_sampleRate_two);
        spinnerPFormatTwo = findViewById(R.id.spinner_play_format_two);
        spinnerPChannelTwo = findViewById(R.id.spinner_play_channel_two);
        spinnerPCyclicTimeTwo = findViewById(R.id.spinner_P_cyclic_time_two);
        spinnerPStreamTypeThree = findViewById(R.id.spinner_streamType_three);
        spinnerPSampleRateThree = findViewById(R.id.spinner_play_sampleRate_three);
        spinnerPFormatThree = findViewById(R.id.spinner_play_format_three);
        spinnerPChannelThree = findViewById(R.id.spinner_play_channel_three);
        spinnerPCyclicTimeThree = findViewById(R.id.spinner_P_cyclic_time_three);
        spinnerRSource = findViewById(R.id.spinner_recordSource);
        spinnerRSampleRate = findViewById(R.id.spinner_record_sampleRate);
        spinnerRFormat = findViewById(R.id.spinner_record_format);
        spinnerRChannel = findViewById(R.id.spinner_record_channel);
        spinnerRCyclicTime = findViewById(R.id.spinner_R_cyclic_time);
        spinnerSetVolume = findViewById(R.id.spinner_setVolume);
        spinnerSetMode = findViewById(R.id.spinner_setMode);
        spinnerSetParameter = findViewById(R.id.spinner_setParameter);
        seekBarVolume = findViewById(R.id.seekbar_setVolume);
        textviewPlayPathOne = findViewById(R.id.textview_filePlay_path_one);
        textviewPlayPathTwo = findViewById(R.id.textview_filePlay_path_two);
        textviewPlayPathThree = findViewById(R.id.textview_filePlay_path_three);
        textViewRecordPathOne = findViewById(R.id.textview_recordFile_path_one);
        textViewRecordPathTwo = findViewById(R.id.textview_recordFile_path_two);
        textViewRecordPathThree = findViewById(R.id.textview_recordFile_path_three);
        textviewVolumeValue = findViewById(R.id.textview_volumeValue);
        mAudioPlayerOne.setButtonId(buttonPlayOne.getId());
        mAudioPlayerTwo.setButtonId(buttonPlayTwo.getId());
        mAudioPlayerThree.setButtonId(buttonPlayThree.getId());
        mAudioRecorderOne.setButtonId(buttonRecordOne.getId());
        mAudioRecorderTwo.setButtonId(buttonRecordTwo.getId());
        mAudioRecorderThree.setButtonId(buttonRecordThree.getId());
    }

    private void initTabHost() {
        TabHost tabHost = findViewById(R.id.tab_host);
        TabHost tabHostPlay = findViewById(R.id.tab_host_play);
        int[] layRes = {
                R.id.relative_track,
                R.id.relative_record,
                R.id.relative_setting};
        int[] layResPlay = {
                R.id.relative_play_one,
                R.id.relative_play_two,
                R.id.relative_play_three};
        String[] tagInfo = {"play", "record", "setting"};
        String[] tagInfoPlay = {"one", "two", "three"};
        tabHost.setup();
        tabHostPlay.setup();
        for (int i = 0; i < 3; i++) {
            TabHost.TabSpec setTab = tabHost.newTabSpec(tagInfo[i]);
            setTab.setIndicator(tagInfo[i]);
            setTab.setContent(layRes[i]);
            tabHost.addTab(setTab);
            TabHost.TabSpec setTabPlay = tabHostPlay.newTabSpec(tagInfoPlay[i]);
            setTabPlay.setIndicator(tagInfoPlay[i]);
            setTabPlay.setContent(layResPlay[i]);
            tabHostPlay.addTab(setTabPlay);
        }
        tabHost.setCurrentTab(0);
        tabHostPlay.setCurrentTab(0);
    }

    private void initContent() {
        mGetContentOne = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
            if (result == null) {
                return;
            }
            String filePath = PathAnalysis.getPath(getBaseContext(), result);
            String fileName = PathAnalysis.getFileName(getBaseContext(), result);
            Log.d(TAG, "filepath: " + filePath + " name: " + fileName);
            textviewPlayPathOne.setText(filePath);
            mAudioPlayerOne.setPlayFilePath(filePath);
        });

        mGetContentTwo = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
            if (result == null) {
                return;
            }
            String filePath = PathAnalysis.getPath(getBaseContext(), result);
            String fileName = PathAnalysis.getFileName(getBaseContext(), result);
            Log.d(TAG, "filepath: " + filePath + " name: " + fileName);
            textviewPlayPathTwo.setText(filePath);
            mAudioPlayerTwo.setPlayFilePath(filePath);
        });

        mGetContentThree = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
            if (result == null) {
                return;
            }
            String filePath = PathAnalysis.getPath(getBaseContext(), result);
            String fileName = PathAnalysis.getFileName(getBaseContext(), result);
            Log.d(TAG, "filepath: " + filePath + " name: " + fileName);
            textviewPlayPathThree.setText(filePath);
            mAudioPlayerThree.setPlayFilePath(filePath);
        });
    }

    private void requestPermission() {
        mGetPermission = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    if (result.get(Manifest.permission.READ_EXTERNAL_STORAGE) != null
                            && result.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) != null
                            && result.get(Manifest.permission.RECORD_AUDIO) != null
                            && result.get(Manifest.permission.MODIFY_AUDIO_SETTINGS) != null) {
                        if (Objects.requireNonNull(result.get(Manifest.permission.READ_EXTERNAL_STORAGE)).equals(true)
                                && Objects.requireNonNull(result.get(Manifest.permission.WRITE_EXTERNAL_STORAGE)).equals(true)
                                && Objects.requireNonNull(result.get(Manifest.permission.RECORD_AUDIO)).equals(true)
                                && Objects.requireNonNull(result.get(Manifest.permission.MODIFY_AUDIO_SETTINGS)).equals(true)) {
                            Log.d(TAG, "request permission is ok");
                        } else {
                            super.finish();
                        }
                    } else {
                        Log.d(TAG, "request permission is fail");
                    }
                });

        if (mSignPermission != null) {
            mGetPermission.launch(mSignPermission);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @SuppressLint("SetTextI18n")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStringEvent(StringEvent event) {
        if (event.message.equals("play")) {
            if (event.id == buttonPlayOne.getId()) {
                buttonPlayOne.setText(event.message);
            } else if (event.id == buttonPlayTwo.getId()) {
                buttonPlayTwo.setText(event.message);
            } else if (event.id == buttonPlayThree.getId()) {
                buttonPlayThree.setText(event.message);
            }
        } else if (event.message.equals("record")) {
            if (event.id == buttonRecordOne.getId()) {
                buttonRecordOne.setText(event.message);
            } else if (event.id == buttonRecordTwo.getId()) {
                buttonRecordTwo.setText(event.message);
            } else if (event.id == buttonRecordThree.getId()) {
                buttonRecordThree.setText(event.message);
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        super.onDestroy();
    }
}