import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;


public class AmeeXmlParseClientMain {
	
	private static String TARGET_HOST = "stage.amee.com";
	private static int TARGET_PORT = 443;
	
	public static void main(String[] args) throws Exception {
		
		if (args.length < 3) {
			System.out.println("required arguments: AMEE_API_USERNAME AMEE_API_PASSWORD AMEE_PROFILE_ID");
			return;
		}
		
		HttpClient httpClient = new DefaultHttpClient();
		HttpHost targetHost = new HttpHost(TARGET_HOST, TARGET_PORT, "https");
		
		String username = args[0];
		String password = args[1];
		String profileId = args[2];
		
		AmeeRequestExecutor executor =
			new AmeeRequestExecutor(httpClient, targetHost, username, password, profileId);
		AmeeXmlReader xmlReader = new AmeeXmlReader();
		
		
		Document doc = executor.exec(new ProfilesLoader());
		System.out.println("####" + xmlReader.read(doc, ProfilesLoader.getXpathUid()));
		System.out.println("####" + xmlReader.read(doc, ProfilesLoader.getXpathCreated()));
		System.out.println("####" + xmlReader.read(doc, ProfilesLoader.getXpathModified()));
		
		
		httpClient.getConnectionManager().shutdown();
		
		System.out.println("----------------------------------------");
		System.out.println("terminated");
	}
}
