package com.example.project.controller.json;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ResponseMessageJson {
    String Description;
    String DateTime;
    int Code;
    String Message;
    Object ApiData;

    public ResponseMessageJson(String description, int code, String message, Object apiData) {
        Description = description;
        DateTime = GetNowDateTime();
        Code = code;
        Message = message;
        ApiData = apiData;
    }

    private String GetNowDateTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        return formatter.format(date);
    }
}









