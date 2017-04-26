package com.virtualassistant.LoggedIn.Chat;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.virtualassistant.Constants;
import com.virtualassistant.LoggedIn.News.WebViewActivity;
import com.virtualassistant.R;
import com.virtualassistant.models.ChatMessage;

import java.util.List;

/**
 * Created by aadam on 4/12/17.
 */

public class ChatMessageAdapter extends ArrayAdapter<ChatMessage> {
    private static final int MY_MESSAGE = 0, OTHER_MESSAGE = 1, OTHER_URL = 2, OTHER_IMAGE = 3;

    public ChatMessageAdapter(Context context, List<ChatMessage> data) {
        super(context, R.layout.item_mine_message, data);
    }

    @Override
    public int getViewTypeCount() {
        // my message, other message, my image, other image
        return 4;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage item = getItem(position);
        if (item.isMine() && !item.isURL()) return MY_MESSAGE;
        else if (!item.isMine() && !item.isURL()) return OTHER_MESSAGE;
        else if (!item.isMine() && item.isURL()) return OTHER_URL;
        else return OTHER_IMAGE;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        int viewType = getItemViewType(position);
        if (viewType == MY_MESSAGE) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_mine_message, parent, false);
            TextView textView = (TextView) convertView.findViewById(R.id.text);
            textView.setText(getItem(position).getContent());
        } else if (viewType == OTHER_MESSAGE) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_other_message, parent, false);
            TextView textView = (TextView) convertView.findViewById(R.id.text);
            textView.setText(getItem(position).getContent());
        } else if (viewType == OTHER_URL) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_other_message_url, parent, false);
            TextView textView = (TextView) convertView.findViewById(R.id.text);
            final String url = getItem(position).getContent();
            textView.setText(url);
            ImageView thumbnail = (ImageView) convertView.findViewById(R.id.website_thumbnail);
            ImageLoader.getInstance().displayImage(Constants.BASE_THUBMNAIL_URL+ url +"/&width=640", thumbnail);
            convertView.findViewById(R.id.chatMessageView).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    getContext().startActivity(i);
                }
            });
        } else {
            // convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_other_image, parent, false);
        }

        return convertView;
    }
}
