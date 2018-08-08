package com.example.abc.random_videocall_application.VideoClasses.configs;

import android.content.Context;

import com.example.abc.random_videocall_application.VideoClasses.AssetsUtils;
import com.example.abc.random_videocall_application.VideoClasses.CoreApp;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class ConfigParser {

    private Context context;

    public ConfigParser() {
        context = CoreApp.getInstance().getApplicationContext();
    }

    public String getConfigsAsJsonString(String fileName) throws IOException {
        return AssetsUtils.getJsonAsString(fileName, context);
    }

    public JSONObject getConfigsAsJson(String fileName) throws IOException, JSONException {
        return new JSONObject(getConfigsAsJsonString(fileName));
    }

    public String getConfigByName(JSONObject jsonObject, String fieldName) throws JSONException {
        return jsonObject.getString(fieldName);
    }
}
