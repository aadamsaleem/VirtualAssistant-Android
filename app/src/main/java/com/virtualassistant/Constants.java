package com.virtualassistant;

/**
 * Created by aadam on 17/12/2016.
 */

public class Constants {

    //Localhost
    public static final String BASE_URL = "http://192.168.0.23:8800/eatout/";

    public static final String NEWS_ARTICLES_URL = "https://newsapi.org/v1/articles?source=google-news&sortBy=top&apiKey=d6ece0fbf16544ecaea51ca1338ffbe6";

    public static final String CHATBOT_URL = "https://nbk6ta8rdb.execute-api.us-west-2.amazonaws.com/prod/botResponse";

    public static final String URL_SIGN_UP = "user/test_login";

    public static final String URL_UPDATE_PREFERENCES = "user/update_preference";

    public static final String URL_GET_PREFERENCES = "user/get_preference";

    public static final String KEY_NEWS_STRING = "user/get_friends";

    public static final String KEY_CHAT_CONTEXT = "context";

    public static final String KEY_CHAT_TEXT= "text";

    public static final String BASE_THUBMNAIL_URL = "https://api.thumbnail.ws/api/abcb462b6458b6797fb971de13de7dd2256144badbe8/thumbnail/get?url=";
}
