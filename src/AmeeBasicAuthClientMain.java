import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

public class AmeeBasicAuthClientMain {

	private static String TARGET_HOST = "stage.amee.com";
	private static int TARGET_PORT = 443;

	public static void main(String[] args) {
		
		if (args.length < 2) {
			System.out.println("required arguments: AMEE_API_USERNAME AMEE_API_PASSWORD");
			return;
		}
		
		String username = args[0];
		String password = args[1];
		
		DefaultHttpClient httpClient = new DefaultHttpClient();
		
		AuthScope authScope = new AuthScope(TARGET_HOST, TARGET_PORT);
		Credentials credentials = new UsernamePasswordCredentials(username, password);
		
		httpClient.getCredentialsProvider().setCredentials(authScope, credentials);
		
		HttpContext localContext = new BasicHttpContext();
		BasicScheme basicScheme = new BasicScheme();
		localContext.setAttribute("preemptive-auth", basicScheme);
		httpClient.addRequestInterceptor(new PreemptiveAuth(), 0);
		
		HttpHost targetHost = new HttpHost(TARGET_HOST, TARGET_PORT, "https");
		
		System.out.println("----------------------------------------");
		
		HttpGet httpget = new HttpGet("/profiles");
		httpget.addHeader("Accept", "application/xml");
		
		System.out.println("executing request: " + httpget.getRequestLine());
		System.out.println("to target: " + targetHost);
		
		try {
			HttpResponse res = httpClient.execute(targetHost, httpget, localContext);
			HttpEntity entity = res.getEntity();
			
			System.out.println("----------------------------------------");
			System.out.println(res.getStatusLine());
			
			if (entity != null) {
				System.out.println("Response content length: " + entity.getContentLength());
				System.out.println(EntityUtils.toString(entity));
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		httpClient.getConnectionManager().shutdown();
	}
	
	// http://svn.apache.org/repos/asf/httpcomponents/httpclient/tags/4.0.1/httpclient/src/examples/org/apache/http/examples/client/ClientPreemptiveBasicAuthentication.java
	static class PreemptiveAuth implements HttpRequestInterceptor {
		
		public void process(
				final HttpRequest request, 
				final HttpContext context) throws HttpException, IOException {
			
			AuthState authState = (AuthState) context.getAttribute(ClientContext.TARGET_AUTH_STATE);
			
			// If no auth scheme avaialble yet, try to initialize it preemptively
			if (authState.getAuthScheme() == null) {
				
				AuthScheme authScheme = (AuthScheme) context.getAttribute("preemptive-auth");
				CredentialsProvider credsProvider = (CredentialsProvider) context.getAttribute(ClientContext.CREDS_PROVIDER);
				HttpHost targetHost = (HttpHost) context.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
				
				if (authScheme != null) {
					
					Credentials creds = credsProvider.getCredentials(
							new AuthScope(targetHost.getHostName(), targetHost.getPort()));
					
					if (creds == null) {
						throw new HttpException("No credentials for preemptive authentication");
					}
					
					authState.setAuthScheme(authScheme);
					authState.setCredentials(creds);
				}
			}
		}
	}
}
