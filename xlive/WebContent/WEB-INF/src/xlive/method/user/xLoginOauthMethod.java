package xlive.method.user;


import xlive.method.*;

public class xLoginOauthMethod extends xDefaultMethod{
	
	public Object process() throws xMethodException{
		String why="";
		boolean valid = true;
		//
		this.setMethodArguments("oauth", "location.protocol", this.getArguments("location.protocol"));
		this.setMethodArguments("oauth", "location.host", this.getArguments("location.host"));
		this.processWebObjectMethod("gdata", "oauth");
		valid = !"false".equals(this.getMethodReturnArguments("oauth", "valid"));
		why = this.getMethodReturnArguments("oauth", "why");
		this.setReturnArguments("auth-url", this.getMethodReturnArguments("oauth", "auth-url"));
		this.setReturnArguments("token-secret", this.getMethodReturnArguments("oauth", "token-secret"));
		this.setReturnArguments("valid", String.valueOf(valid));
		this.setReturnArguments("why", why);
		return getServiceContext().doNextProcess(); 
		//
		/*
		String protocol=this.getArguments("location.protocol");
		String host = this.getArguments("location.host");
		String callback=protocol+"//"+host+(xProbeServlet.isGAE()? "":"/xlive")+"/web/user?method=login-oauth-callback";
		String consumer_key = this.getProperties("oauth-consumer-key");
		if(consumer_key == null || consumer_key.trim().length() == 0) consumer_key = "anonymous";
		String consumer_secret = this.getProperties("oauth-consumer-secret");
		if(consumer_secret ==null || consumer_secret.trim().length() == 0) consumer_secret = "anonymous"; // ??
		String scope = this.getArguments("scope");
		//
	    UserInputVariables variables = new UserInputVariables();
	    variables.setConsumerKey(consumer_key);
	    variables.setSignatureMethod(SignatureMethod.HMAC);
	    variables.setSignatureKey(consumer_secret);
	    //variables.setGoogleService(GoogleServiceType.Calendar);
	    //
	    GoogleOAuthParameters oauth_parameters = new GoogleOAuthParameters();
	    oauth_parameters.setOAuthConsumerKey(variables.getConsumerKey());
	    oauth_parameters.setOAuthConsumerSecret(variables.getSignatureKey());
	    oauth_parameters.setOAuthCallback(callback);
	    //oauth_parameters.setScope(variables.getScope());
	    oauth_parameters.setScope(scope);
	    OAuthSigner signer = new OAuthHmacSha1Signer();
	    //
	    try{
	    	GoogleOAuthHelper oauth_helper = new GoogleOAuthHelper(signer);
	    	oauth_helper.getUnauthorizedRequestToken(oauth_parameters);
	    	String request_url = oauth_helper.createUserAuthorizationUrl(oauth_parameters);
			String url=request_url;
			String token_secret=oauth_parameters.getOAuthTokenSecret();
			//
			Cookie cookie=new Cookie("auth-url", url);
			//cookie.setPath("/xlive");
			cookie.setMaxAge(-1);
			this.getServiceContext().addCookie(cookie);
			cookie=new Cookie("token-secret", token_secret);
			//cookie.setPath("/xlive");
			cookie.setMaxAge(-1);
			this.getServiceContext().addCookie(cookie);
			//
			this.setReturnArguments("auth-url", url);
			this.setReturnArguments("token-secret", token_secret);
	    }catch(Exception e){
	    	valid = false;
	    	why = e.getMessage();
	    	e.printStackTrace();
	    }
		this.setReturnArguments("valid", String.valueOf(valid));
		this.setReturnArguments("why", why);
		return getServiceContext().doNextProcess();
		*/
	}
}
