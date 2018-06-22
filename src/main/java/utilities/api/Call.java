package utilities.api;

import utilities.StormLog;
import utilities.StormProperties;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

public class Call {

    private static HttpClientContext context;
    private static CloseableHttpClient httpClient;
    private static final int threadMax = Integer.valueOf(StormProperties.getProperty("absoluteApiThreadMax"));

    // HTTP DELETE request
    public static ApiResponse delete(String url, HashMap<String, String> headers) {
        StormLog.info("calling DELETE at " + url, Call.class);
        return sendRequest(new HttpDelete(url), headers);
    }

    // HTTP GET request
    public static ApiResponse get(String url, HashMap<String, String> headers) {
        StormLog.info("calling GET at " + url, Call.class);
        return sendRequest(new HttpGet(url), headers);
    }

    // HTTP PATCH request
    public static ApiResponse patch(String url, HashMap<String, String> headers, String body) {
        StormLog.info("calling PATCH at " + url, Call.class);
        HttpPatch request = new HttpPatch(url);
        try {
            request.setEntity(new StringEntity(body));            
        } catch (UnsupportedEncodingException e) {
            StormLog.error(e, Call.class);
            e.printStackTrace();
        }

        return sendRequest(request, headers);
    }

    // HTTP PUT request
    public static ApiResponse put(String url, HashMap<String, String> headers, String body) {
        StormLog.info("calling PUT at " + url, Call.class);
        HttpPut request = new HttpPut(url);
        try {
            request.setEntity(new StringEntity(body));            
        } catch (UnsupportedEncodingException e) {
            StormLog.error(e, Call.class);
            e.printStackTrace();
        }
        
        return sendRequest(request, headers);
    }

    // HTTP POST request
    public static ApiResponse post(String url, HashMap<String, String> headers, String body) {
        StormLog.info("calling POST at " + url, Call.class);        
        HttpPost request = new HttpPost(url);
        try {
            request.setEntity(new StringEntity(body));            
        } catch (UnsupportedEncodingException e) {
            StormLog.error(e, Call.class);
            e.printStackTrace();
        }
        
        return sendRequest(request, headers);
    }

    // HTTP DELETE request
    public static ApiResponse delete(String url) {
        StormLog.info("calling DELETE at " + url, Call.class);        
        return sendRequest(new HttpDelete(url), new HashMap<>());
    }

    // HTTP GET request
    public static ApiResponse get(String url) {
        StormLog.info("calling GET at " + url, Call.class);        
        return sendRequest(new HttpGet(url), new HashMap<>());
    }

    // HTTP GET request
    public static ApiResponse options(String url) {
        StormLog.info("calling OPTIONS at " + url, Call.class);        
        return sendRequest(new HttpOptions(url), new HashMap<>());
    }

    // HTTP PATCH request
    public static ApiResponse patch(String url, String body) {
        return patch(url, new HashMap<>(), body);
    }

    // HTTP PUT request
    public static ApiResponse put(String url, String body) {
        return put(url, new HashMap<>(), body);
    }

    // HTTP POST request
    public static ApiResponse post(String url, String body) {
        return post(url, new HashMap<>(), body);
    }

    private static ApiResponse sendRequest(HttpRequestBase request, HashMap<String, String> headers) {
        int responseCode = 0;
        StringBuilder fullResponse = new StringBuilder();
        headers.forEach(request::addHeader);
        
        if (context == null) {
            context = HttpClientContext.create();
        }
        try {
            //send the request
            HttpResponse response = getHttpClient().execute(request, context);

            //save response
            responseCode = response.getStatusLine().getStatusCode();
            StormLog.debug("Api call got a '"+responseCode+"' response code", Call.class);
            BufferedReader br = new BufferedReader(
                    new InputStreamReader((response.getEntity().getContent())));

            String output;
            while ((output = br.readLine()) != null) {
                fullResponse.append(output).append("\n");
            }
            StormLog.debug("Api call full response: " + fullResponse, Call.class);
        } catch (IOException e) {
            StormLog.info("API request failed", Call.class);
            e.printStackTrace();
        } finally {
            request.releaseConnection();
        }
        return ApiResponse.create(responseCode, fullResponse.toString());
    }

    private static CloseableHttpClient getHttpClient() {
        if (httpClient == null) {
            PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
            connectionManager.setMaxTotal(200);
            connectionManager.setDefaultMaxPerRoute(threadMax);
            httpClient = HttpClients.custom().setConnectionManager(connectionManager).build();
        }
        return httpClient;
    }
}
