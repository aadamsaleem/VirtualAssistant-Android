package com.virtualassistant.LoggedIn.Chat;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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
import org.w3c.dom.Text;

import java.util.ArrayList;

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


        return view;
    }

    //region private methods
    private void setupViews() {

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
                    JSONArray arrJson= result.getJSONArray("urls");
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
        ChatMessage chatMessage = new ChatMessage(message, false, false);
        mAdapter.add(chatMessage);
        typing.setVisibility(View.GONE);
    }

    private void mimicOtherURLMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message, false, true);
        mAdapter.add(chatMessage);
        typing.setVisibility(View.GONE);
    }
    //endregion

    private void hideKeyBoard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
