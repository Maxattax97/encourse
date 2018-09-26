package edu.purdue.cs.encourse.util;

import org.json.simple.JSONObject;

public class JSONReturnable {
    public int errorCode;
    public JSONObject jsonObject;

    public JSONReturnable(int errorCode, JSONObject jsonObject) {
        this.errorCode = errorCode;
        this.jsonObject = jsonObject;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }
}
