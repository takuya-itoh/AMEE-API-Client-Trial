import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

public class AmeeCookieAuthClientMain {
	
	private static String TARGET_HOST = "stage.amee.com";
	private static int TARGET_PORT = 443;

	public static void main(String[] args) throws Exception {
		
		if (args.length < 2) {
			System.out.println("required arguments: AMEE_API_USERNAME AMEE_API_PASSWORD");
			return;
		}
		
		String username = args[0];
		String password = args[1];
		
		HttpClient httpClient = new DefaultHttpClient();
		HttpHost targetHost = new HttpHost(TARGET_HOST, TARGET_PORT, "https");

		String cookie = getCookie(httpClient, targetHost, username, password);
		
        if (cookie == null) return;

        System.out.println("----------------------------------------");

        HttpGet httpget = new HttpGet("/profiles");
        httpget.addHeader("Cookie", cookie);
		httpget.addHeader("Accept", "application/xml");
		
		System.out.println("executing request: " + httpget.getRequestLine());
		System.out.println("to target: " + targetHost);
		
		HttpResponse res = httpClient.execute(targetHost, httpget);
		HttpEntity entity = res.getEntity();
		
        System.out.println("----------------------------------------");
        System.out.println(res.getStatusLine());
        
        if (entity != null) {
        	
        	System.out.println(EntityUtils.toString(entity));
        }
        
		httpClient.getConnectionManager().shutdown();
	}
	
	private static String getCookie(HttpClient httpClient, HttpHost targetHost, String username, String password) throws Exception {
		
        System.out.println("----------------------------------------");

		HttpPost httppost = new HttpPost("/auth");
		httppost.addHeader("Accept", "application/xml");
		httppost.addHeader("Content-Type", "application/x-www-form-urlencoded");
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("username", username));
		params.add(new BasicNameValuePair("password", password));
		httppost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
		
        System.out.println("executing request: " + httppost.getRequestLine());
        System.out.println("to target: " + targetHost);
		
		HttpResponse res = httpClient.execute(targetHost, httppost);
		HttpEntity entity = res.getEntity();
		
        System.out.println("----------------------------------------");
        System.out.println(res.getStatusLine());
        
        String cookie = "";

		if (entity != null) {
			
			Header[] headers = res.getHeaders("authToken");
			
			if (headers.length > 0) {
				Header authTokenHeader = headers[0];
				cookie = authTokenHeader.getValue();
				System.out.println(authTokenHeader.getValue());
			}
			
			System.out.println(EntityUtils.toString(entity));
		}
		
		return cookie;
	}
}
