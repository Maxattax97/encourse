package edu.purdue.cs.encourse.util;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

public class JSONReturnable {
public int errorCode;
private JSONObject jsonObject;
private JSONArray jsonArray;

public JSONReturnable(int errorCode, JSONObject jsonObject) {
    this.errorCode = errorCode;
    this.jsonObject = jsonObject;
}

public JSONReturnable(int errorCode, JSONArray jsonArray) {
    this.errorCode = errorCode;
    this.jsonArray = jsonArray;
}

public int getErrorCode() {
                        return errorCode;
                                         }

public JSONObject getJsonObject() {
    return jsonObject;
}

public JSONArray getJsonArray() {
    return jsonArray;
}

}
