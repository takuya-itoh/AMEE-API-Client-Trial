import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;


public class ProfilesLoader implements IAmeeRequest {
	
	private InputStream result;
	
	public ProfilesLoader() {
		super();
	}
	
	@Override
	public boolean doRequest(HttpClient httpClient, HttpHost targetHost) throws Exception {
		
		System.out.println("----------------------------------------");
		
		HttpGet httpget = new HttpGet("/profiles");
		httpget.addHeader("Accept", "application/xml");
		
		System.out.println("executing request: " + httpget.getRequestLine());
		System.out.println("to target: " + targetHost);
		
		HttpResponse res = httpClient.execute(targetHost, httpget);
		HttpEntity entity = res.getEntity();
		
		System.out.println("----------------------------------------");
		System.out.println(res.getStatusLine());
		
		if (res.getStatusLine().getStatusCode() == HttpStatus.SC_UNAUTHORIZED) {
			httpget.abort();
			return false;
		}
		
		if (entity != null) {
			result = entity.getContent();
		}
		
		return true;
	}
	
	@Override
	public InputStream getResult() {
		return result;
	}
	
	public static String getXpathUid() {
		return "/amee:Resources/amee:ProfilesResource/amee:Profiles/amee:Profile/@uid";
	}
	
	public static String getXpathCreated() {
		return "/amee:Resources/amee:ProfilesResource/amee:Profiles/amee:Profile/@created";
	}
	
	public static String getXpathModified() {
		return "/amee:Resources/amee:ProfilesResource/amee:Profiles/amee:Profile/@modified";
	}
}
