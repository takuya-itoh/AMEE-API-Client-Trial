import java.io.InputStream;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;


public interface IAmeeRequest {
	
	public boolean doRequest(HttpClient httpClient, HttpHost targetHost) throws Exception;
	
	public InputStream getResult();

}
