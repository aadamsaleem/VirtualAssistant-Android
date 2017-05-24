package com.virtualassistant;

/**
 * Created by aadam on 17/12/2016.
 */

public class Constants {

    //Localhost
   // public static final String BASE_URL = "http://192.168.0.23:8800/va/";

    public static final String NEWS_ARTICLES_URL = "https://newsapi.org/v1/articles?source=google-news&sortBy=top&apiKey=d6ece0fbf16544ecaea51ca1338ffbe6";

    public static final String CHATBOT_URL = "https://0r3ytyy93j.execute-api.us-west-2.amazonaws.com/prod/botResponse";

    public static final String CHATBOT_SPEECH_URL = "http://node-express-env.pmqpvmcwp2.us-west-2.elasticbeanstalk.com/bot";

    public static final String CHATBOT_SPEECH_ANALYSIS_URL = "http://node-express-env.pmqpvmcwp2.us-west-2.elasticbeanstalk.com/speech_analysis";

    public static final String URL_SIGN_UP = "user/test_login";

    public static final String URL_UPDATE_PREFERENCES = "user/update_preference";

    public static final String URL_GET_PREFERENCES = "user/get_preference";

    public static final String KEY_NEWS_STRING = "user/get_friends";

    public static final String KEY_CHAT_CONTEXT = "context";

    public static final String KEY_CHAT_TEXT= "text";

    public static final String BASE_THUBMNAIL_URL = "https://api.thumbnail.ws/api/abcb462b6458b6797fb971de13de7dd2256144badbe8/thumbnail/get?url=";

    public static final String EVENT_URL = "https://htcmuo40fi.execute-api.us-west-2.amazonaws.com/prod/event";

    public static final String CALL_LOG_CREATE_URL = "http://ec2-54-213-118-235.us-west-2.compute.amazonaws.com:8000/create_model";

    public static final String CALL_LOG_UPDATE_URL = "http://ec2-54-213-118-235.us-west-2.compute.amazonaws.com:8000/daily_job";

    public static final String BASE_URL = "http://ec2-54-202-80-134.us-west-2.compute.amazonaws.com:8000";
    public static final String PING_URL = BASE_URL;
    public static final String NEW_IMAGE_URL = BASE_URL + "/newImage";
    public static final String TEST_IMAGE_URL = BASE_URL + "/testImage";
    public static final String MAIN_ACTIVITY_TAG = "RECOGNITO";
    public static final String SERVER_UPLOAD_TAG = "SERVER_UPLOAD_RECOGNITO";
    public static final String SERVER_CONNECTIVITY_ISSUE = "Internet not available or server not responding";
}
