package utilities.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import utilities.StormLog;

import java.io.IOException;

public class ApiResponse {

    private final int responseCode;
    private final String response;

    public static ApiResponse create(int responseCode, String response) {
        return new ApiResponse(responseCode, response);
    }

    public ApiResponse(int responseCode, String response) {
        this.responseCode = responseCode;
        this.response = response;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getResponse() {
        return response;
    }

    public JSONObject getJSON() {
        JSONObject json = null;

        try {
            json = new JSONObject(response);
        } catch (JSONException e) {
            StormLog.error("unable to make json object with response: " + response, getClass());
            StormLog.error(e, getClass());
        }

        return json;
    }

    public JSONArray getJSONArray() {
        JSONArray json = null;

        try {
            json = new JSONArray(response);
        } catch (JSONException e) {
            StormLog.error("unable to make json array with response: " + response, getClass());
            StormLog.error(e, getClass());
        }

        return json;
    }

    public <T> T getObject(Class mapTo) {
        Object value = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            value = mapper.readValue(response, mapTo);
        } catch (IOException e) {
            StormLog.warn(e, getClass());
        }
        return (T)value;
    }

    @Override
    public String toString() {
        return response;
    }
}
