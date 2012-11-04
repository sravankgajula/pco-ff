package net.udsholt.peytz.firmafon.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

public class RestRequest 
{
	public enum Method {
		GET, 
		POST,
	};
	
    private ArrayList <NameValuePair> params;
    private ArrayList <NameValuePair> headers;
    private Method method;
    private String rawPost;
    
    private String url;

    private int responseCode;
    private String message;

    private String response;

    public RestRequest(String url)
    {
        this.url     = url;
        this.method  = Method.GET;
        this.params  = new ArrayList<NameValuePair>();
        this.headers = new ArrayList<NameValuePair>();
    }
    
    public String getResponse() 
    {
        return this.response;
    }

    public String getErrorMessage() 
    {
        return this.message;
    }

    public int getResponseCode() 
    {
        return this.responseCode;
    }

    public void setMethod(Method method)
    {
    	this.method = method;
    }

    public void setRawPost(final String rawPost)
    {
    	this.rawPost = rawPost;
    }
    
    public void addParam(String name, String value)
    {
    	this.params.add(new BasicNameValuePair(name, value));
    }

    public void addHeader(String name, String value)
    {
    	this.headers.add(new BasicNameValuePair(name, value));
    }

    public void execute() throws Exception
    {
        switch(this.method) {
            case GET:
            {
                //add parameters
                String combinedParams = "";
                if(!this.params.isEmpty()){
                    combinedParams += "?";
                    for(NameValuePair p : this.params) {
                        String paramString = p.getName() + "=" + URLEncoder.encode(p.getValue(), "UTF-8");
                        if(combinedParams.length() > 1) {
                            combinedParams  +=  "&" + paramString;
                        } else {
                            combinedParams += paramString;
                        }
                    }
                }

                HttpGet request = new HttpGet(this.url + combinedParams);
                
                //add headers
                for(NameValuePair h : this.headers) {
                    request.addHeader(h.getName(), h.getValue());
                }

                executeRequest(request, this.url);
                break;
            }
            case POST:
            {
                HttpPost request = new HttpPost(this.url);
                
                //add headers
                for(NameValuePair h : this.headers) {
                    request.addHeader(h.getName(), h.getValue());
                }

                if (!this.params.isEmpty()) {
                    request.setEntity(new UrlEncodedFormEntity(this.params, HTTP.UTF_8));
                }
                
                if (this.rawPost != null && this.rawPost != "") {
                	request.setEntity(new StringEntity(this.rawPost, HTTP.UTF_8));
                }

                executeRequest(request, this.url);
                break;
            }
        }
    }

    private void executeRequest(HttpUriRequest request, String url) throws ConnectTimeoutException
    {
    	HttpParams httpParameters = new BasicHttpParams();
    	
    	// Set the timeout in milliseconds until a connection is established.
    	// The default value is zero, that means the timeout is not used. 
    	HttpConnectionParams.setConnectionTimeout(httpParameters, 2000);
    	
    	// Set the default socket timeout (SO_TIMEOUT) 
    	// in milliseconds which is the timeout for waiting for data.
    	HttpConnectionParams.setSoTimeout(httpParameters, 4000);
    	
        HttpClient client = new DefaultHttpClient(httpParameters);

        HttpResponse httpResponse;

        try {
        	
            httpResponse = client.execute(request);
            
            responseCode = httpResponse.getStatusLine().getStatusCode();
            message = httpResponse.getStatusLine().getReasonPhrase();

            HttpEntity entity = httpResponse.getEntity();

            if (entity != null) {

                InputStream instream = entity.getContent();
                
                response = convertStreamToString(instream);

                // Closing the input stream will trigger connection release
                instream.close();
            }

        } catch (ConnectTimeoutException e) {
        	throw e;
        } catch (ClientProtocolException e)  {
            client.getConnectionManager().shutdown();
            e.printStackTrace();
        } catch (IOException e) {
            client.getConnectionManager().shutdown();
            e.printStackTrace();
        }
        
        
    }

    private static String convertStreamToString(InputStream is) 
    {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}