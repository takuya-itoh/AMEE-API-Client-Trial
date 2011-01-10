import com.amee.client.AmeeException;
import com.amee.client.model.profile.AmeeProfile;
import com.amee.client.model.profile.AmeeProfileCategory;
import com.amee.client.service.AmeeContext;
import com.amee.client.service.AmeeObjectFactory;


public class AmeeClientTrialMain {

	/*
	 * Using AMEEJavaClient.1.6 version 2.1
	 * Required:
	 *   commons-codec 1.3
	 *   commons-httpclient 3.1
	 *   commons-logging 1.1
	 *   json 2.0
	 * 
	 */
	public static void main(String[] args) throws AmeeException {
		
		if (args.length < 3) {
			System.out.println("required arguments: AMEE_API_USERNAME AMEE_API_PASSWORD AMEE_PROFILE_ID");
			return;
		}
		
		AmeeContext.getInstance().setUsername(args[0]);
		AmeeContext.getInstance().setPassword(args[1]);
		AmeeContext.getInstance().setBaseUrl("http://stage.amee.com");
		AmeeContext.getInstance().setItemsPerPage(10);
		
		String profileUID = args[2];
		String profileCategory = "home/energy/quantity";
		
		AmeeProfile profile = AmeeObjectFactory.getInstance().getProfile(profileUID);
		System.out.println("####" + profile.getUid());
		
		AmeeProfileCategory cat = AmeeObjectFactory.getInstance().getProfileCategory(profile, profileCategory);
		System.out.println("####" + cat.getName());
	}

}
