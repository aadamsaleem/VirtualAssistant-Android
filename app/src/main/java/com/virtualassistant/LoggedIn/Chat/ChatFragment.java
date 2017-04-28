package com.virtualassistant.LoggedIn.Chat;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.virtualassistant.Constants;
import com.virtualassistant.R;
import com.virtualassistant.client.ChatManager;
import com.virtualassistant.client.CompletionInterface;
import com.virtualassistant.models.ChatMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by aadam on 11/4/2017.
 */

public class ChatFragment extends android.support.v4.app.Fragment {

    //    public static Chat chat;
//    public Bot bot;
    private ListView mListView;
    private TextView mButtonSend;
    private TextView typing;
    private EditText mEditTextMessage;
    private ChatMessageAdapter mAdapter;
    private View view;
    private JSONObject contextJson;

    private ImageView mic_Button;
    private ImageView recording;
    private Animation animation;
    private static final String AUDIO_RECORDER_FILE_EXT_3GP = ".3gp";
    private static final String AUDIO_RECORDER_FILE_EXT_MP4 = ".mp4";
    private static final String AUDIO_RECORDER_FOLDER = "VirtualAssistant";
    private MediaRecorder recorder = null;
    private int currentFormat = 0;
    private int output_formats[] = { MediaRecorder.OutputFormat.MPEG_4, MediaRecorder.OutputFormat.THREE_GPP};
    private String file_exts[] = { AUDIO_RECORDER_FILE_EXT_MP4, AUDIO_RECORDER_FILE_EXT_3GP };
    private String filename;
    private TextToSpeech textToSpeech;


    public ChatFragment() {
    }

    ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_chat, container, false);

        setupViews();
        hideKeyBoard();

        contextJson = new JSONObject();
        mAdapter = new ChatMessageAdapter(getContext(), new ArrayList<ChatMessage>());
        mListView.setAdapter(mAdapter);
        mListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        mEditTextMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count>0){
                    mButtonSend.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //code for sending the message
        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideKeyBoard();
                String message = mEditTextMessage.getText().toString();
                if (TextUtils.isEmpty(message)) {
                    return;
                }
                sendMessage(message);
                mEditTextMessage.setText("");
                mListView.setSelection(mAdapter.getCount() - 1);
            }
        });

        mic_Button =(ImageView) view.findViewById(R.id.mic_button);
        recording = (ImageView) view.findViewById(R.id.recording);

        mic_Button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                switch(event.getAction()){

                    case MotionEvent.ACTION_DOWN:
                        startRecording();
                        break;
                    case MotionEvent.ACTION_UP:
                        stopRecording();
                        break;
                }
                return true;
            }
        });
        textToSpeech = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status!= TextToSpeech.ERROR)
                    textToSpeech.setLanguage(Locale.US);

            }
        });


        return view;
    }

    //region private methods
    private void setupViews() {

        animation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
        animation.setDuration(500); // duration - half a second
        animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
        animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
        animation.setRepeatMode(Animation.REVERSE);

        getViewIds();


    }

    private void getViewIds() {
        mListView = (ListView) view.findViewById(R.id.listView);
        mButtonSend = (TextView) view.findViewById(R.id.btn_send);
        mEditTextMessage = (EditText) view.findViewById(R.id.et_message);
        typing = (TextView) view.findViewById(R.id.typing);

    }

    private void sendMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message, true, false);
        mAdapter.add(chatMessage);

        typing.setVisibility(View.VISIBLE);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.KEY_CHAT_TEXT, message);
            jsonObject.put(Constants.KEY_CHAT_CONTEXT,contextJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        ChatManager.sendMessage(getActivity(), jsonObject, new CompletionInterface() {
            @Override
            public void onSuccess(JSONObject result) {

                try {
                    mimicOtherMessage(result.getString(Constants.KEY_CHAT_TEXT));
                    contextJson = result.getJSONObject(Constants.KEY_CHAT_CONTEXT);
                } catch (JSONException e) {
                    typing.setVisibility(View.GONE);
                    e.printStackTrace();
                }

                try {
                    JSONArray arrJson= result.getJSONObject("context").getJSONArray("urls");
                    for(int i=0;i<arrJson.length();i++) {
                        mimicOtherURLMessage(arrJson.getString(i));
                    }
                }
                catch (JSONException e){
                }
            }

            @Override
            public void onFailure() {

            }
        });
    }

    private void mimicOtherMessage(String message) {
        textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null);
        ChatMessage chatMessage = new ChatMessage(message, false, false);
        mAdapter.add(chatMessage);
        typing.setVisibility(View.GONE);
    }

    private void mimicOtherURLMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message, false, true);
        mAdapter.add(chatMessage);
        typing.setVisibility(View.GONE);
    }

    private String getFilename(){
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath,AUDIO_RECORDER_FOLDER);

        if(!file.exists()){
            file.mkdirs();
        }

        filename =  (file.getAbsolutePath() + "/" + System.currentTimeMillis() + file_exts[currentFormat]);
        return filename;
    }

    private void startRecording(){

        recording.setVisibility(View.VISIBLE);
        recording.startAnimation(animation);
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(output_formats[currentFormat]);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.setOutputFile(getFilename());
            recorder.setOnErrorListener(errorListener);
            recorder.setOnInfoListener(infoListener);
        try {
            recorder.prepare();
            recorder.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
        @Override
        public void onError(MediaRecorder mr, int what, int extra) {
        }
    };

    private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
        @Override
        public void onInfo(MediaRecorder mr, int what, int extra) {
        }
    };

    private void stopRecording() {

        recording.setVisibility(View.GONE);
        recording.clearAnimation();

        try{
            recorder.stop();
            recorder.reset();
            recorder.release();

            recorder = null;
        }
        catch (Exception e){
            e.printStackTrace();
        }

        ChatManager.sendAudio(getActivity(), filename, new CompletionInterface() {
            @Override
            public void onSuccess(JSONObject result) {
                try {

                    ChatMessage chatMessage = new ChatMessage(result.getString("transcript"), true, false);
                    mAdapter.add(chatMessage);

                    result = result.getJSONObject("response");
                    mimicOtherMessage(result.getString(Constants.KEY_CHAT_TEXT));
                    contextJson = result.getJSONObject(Constants.KEY_CHAT_CONTEXT);
                } catch (JSONException e) {
                    typing.setVisibility(View.GONE);
                    e.printStackTrace();
                }

                try {
                    JSONArray arrJson= result.getJSONObject(Constants.KEY_CHAT_CONTEXT).getJSONArray("urls");
                    for(int i=0;i<arrJson.length();i++) {
                        mimicOtherURLMessage(arrJson.getString(i));
                    }
                }
                catch (JSONException e){
                }

            }

            @Override
            public void onFailure() {

            }
        });
    }
    //endregion

    @Override
    public void onPause(){
        if(textToSpeech !=null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onPause();
    }

    private void hideKeyBoard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
