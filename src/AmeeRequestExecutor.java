import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;


public class AmeeRequestExecutor {
	
	private final HttpClient httpClient;
	private final HttpHost targetHost;
	
	private final DocumentBuilder xmlBuilder;
	
	private final String username;
	private final String password;
	private final String profileId;
	
	public AmeeRequestExecutor(HttpClient httpClient, HttpHost targetHost, String username, String password, String profileId) throws Exception {
		super();
		
		this.httpClient = httpClient;
		this.targetHost = targetHost;
		this.username = username;
		this.password = password;
		this.profileId = profileId;
		
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		docFactory.setNamespaceAware(true);
		this.xmlBuilder = docFactory.newDocumentBuilder();
	}
	
	public Document exec(IAmeeRequest request) throws Exception {
		
		Document doc = null;
		
		int unauthorizedCount = 0;
		
		do {
			
			if (request.doRequest(httpClient, targetHost)) {
				
				InputStream im = request.getResult();
				
				try {
					
					doc = xmlBuilder.parse(im);
					
				} finally {
					im.close();
				}
				
				break;
			}
			
			unauthorizedCount++;
			authorize();
			
		} while (unauthorizedCount < 3);
		
		return doc;
	}
	
	private void authorize() throws Exception {
		
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
		
		if (entity != null) {
			
			Header[] headers = res.getHeaders("authToken");
			
			if (headers.length > 0) {
				Header authTokenHeader = headers[0];
				System.out.println(authTokenHeader.getValue());
			}
			
			System.out.println(EntityUtils.toString(entity));
		}
	}
}
