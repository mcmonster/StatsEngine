import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import net.oauth.*;
import net.oauth.client.OAuthClient;
import net.oauth.client.httpclient4.HttpClient4;

public class StatFetcher {
	public StatFetcher() {
		Clients = new DesktopClient[10];
		CurrentClient = 0;
		establishConnection();
	}
	
	public void establishConnection() {
		String key[] = {//"dj0yJmk9ZWljVDc0TXdXUnBEJmQ9WVdrOVpqY3hkMnBtTXpJbWNHbzlORGd6TmprME5qSS0mcz1jb25zdW1lcnNlY3JldCZ4PTYz", // My real account
						//"dj0yJmk9RDJ4WmZ1YkQ4SHI4JmQ9WVdrOU4wMVBia1ZRTlRBbWNHbzlOVEV4TkRJM016WXkmcz1jb25zdW1lcnNlY3JldCZ4PWRh", // Bullshit account
						//"dj0yJmk9U3h4Z0g5THp3RkJGJmQ9WVdrOU9YWjVWV3BoTXpBbWNHbzlNVGs1TlRRME5EWTJNZy0tJnM9Y29uc3VtZXJzZWNyZXQmeD1hMA--",// dsneaky1 aidscake55
						//"dj0yJmk9U3ZkQlRYbU1nWjNPJmQ9WVdrOWNEZFpkSFE1TXpJbWNHbzlNVEF6TmpJM056azJNZy0tJnM9Y29uc3VtZXJzZWNyZXQmeD03Yg--",// dsneaky2
						//"dj0yJmk9d2RlUWl5aWFwTGVGJmQ9WVdrOWVtaFJOM1YzTnpJbWNHbzlNamMwTWprME5qSS0mcz1jb25zdW1lcnNlY3JldCZ4PTll",// dsneaky33
						//"dj0yJmk9OFExdXIxMkdzb2lqJmQ9WVdrOVRteENSbEZVTjJFbWNHbzlNVGM0TWpRMk1ETTJNZy0tJnM9Y29uc3VtZXJzZWNyZXQmeD1kMw--",// dsneaky44
						//"dj0yJmk9bFdQTHpZSE1LbmVhJmQ9WVdrOVR6UlhaSHAwTXpRbWNHbzlOek00TlRJME1UWXkmcz1jb25zdW1lcnNlY3JldCZ4PTc3",// dsneaky55
						//"dj0yJmk9MThVOEt6eE1Nc3hoJmQ9WVdrOVkxTm1lRzVpTlRRbWNHbzlNVGMzT1RNM01UYzJNZy0tJnM9Y29uc3VtZXJzZWNyZXQmeD04Zg--",// dsneaky66
						//"dj0yJmk9TUlTdWgxV2QwcTQ1JmQ9WVdrOVlWRTRObGx6TlRnbWNHbzlOREE0T1RJMk5EWXkmcz1jb25zdW1lcnNlY3JldCZ4PWY4",// dsneaky16
						//"dj0yJmk9TXlPbjI2bW1kelJ4JmQ9WVdrOU5UaHJOVXhNTTJNbWNHbzlNVEUyT1RrMU9EazJNZy0tJnM9Y29uc3VtZXJzZWNyZXQmeD1lOA--",// dsneaky17
						//"dj0yJmk9NzNncUlma1ljZjg3JmQ9WVdrOVlUZDVhRTB4TXpJbWNHbzlNVE0yTURFMk5UVTJNZy0tJnM9Y29uc3VtZXJzZWNyZXQmeD1kOA--",// dsneaky18
						"dj0yJmk9OGEzeHhRQmZqQXlvJmQ9WVdrOVVtRm5lbWQ1Tm1zbWNHbzlNVEV3TVRjMk5qVTJNZy0tJnM9Y29uc3VtZXJzZWNyZXQmeD0yYw--", //dsneaky99
						"dj0yJmk9QXFJb1pEQnFOaDRoJmQ9WVdrOVRHVmFSV1J4Tm5NbWNHbzlNVFkwTWpRNU5EWTJNZy0tJnM9Y29uc3VtZXJzZWNyZXQmeD0xOQ--" //carloshangover001
						};
		String secret[] = {//"b75723075d8ea03aaf778ad138b47d6b06bf87f2",// my real account
				           //"079753541bdc00dad426b735ff7468057ffbe958",//bullshit account
				           //"7db65251cd1d090624465f7483bd6e66cd44917d",// dsneaky1
				           //"9117f801a3b2e386da12276a65a436eacfde5d1b",// dsneaky2
				           //"253bd191dac0d91851862aa12a038869240514d7",// dsneaky33
				           //"b4d9e81f910b61fac42f0cca8ad01ad6984dde95",// dsneaky44
				           //"f6e5b97d9c3ebd6b2fc424c9d3b8d6e2a2df51da",// dsneaky55
				           //"5981d919f8d6184d17a0ef749d53c136c7cbab40",// dsneaky66
				           //"e3eb3f9b3ec304ce4d43253421cb8b2a8d0439d7",// dsneaky16
				           //"cde6bc60a7cd60ffc6c8be937a6c78bc785adb2f",// dsneaky17
				           //"0fd72d2354f7baabc3005d2b5b8cf78739e043a0"// dsneaky18
						   "a2d7d8aafaf34d77f4950c5c06d1f3d8967cb1c7", // dsneaky99
						   "f1aab485942669ee7c9b5f1a887a1482198b6c99" //carloshangover001
				           };

		for(int iter=0; iter<2; iter++) {
			String accessTokenURL = "https://api.login.yahoo.com/oauth/v2/get_token";
			String requestTokenURL = "https://api.login.yahoo.com/oauth/v2/get_request_token";
			String userAuthorizationURL = "https://api.login.yahoo.com/oauth/v2/request_auth";
			OAuthServiceProvider serviceProvider = new OAuthServiceProvider(requestTokenURL,
																			userAuthorizationURL,
				                                                        	accessTokenURL);
			OAuthConsumer consumer = new OAuthConsumer(null, key[iter], secret[iter], serviceProvider);
			Clients[iter] = new DesktopClient(consumer);
			Clients[iter].setOAuthClient(new OAuthClient(new HttpClient4()));
		}
	}
	
	public String fetchPlayerStats(final int playerID,
			                       final Year year) throws Exception {
		String request = "http://fantasysports.yahooapis.com/fantasy/v2/player/";
		request+=year.getCode()+".p."+playerID;
		OAuthMessage response = Clients[CurrentClient].access(OAuthMessage.GET, request, null);
		System.out.println(response.readBodyAsString());
		return response.readBodyAsString();
	}
	
	public String fetch(final String request) throws Exception {
		OAuthMessage response = Clients[CurrentClient].access(OAuthMessage.GET, request, null);
		CurrentClient = (++CurrentClient % 2);
		return response.readBodyAsString();
	}
	
	private DesktopClient[] Clients;
	private int CurrentClient;
	
	public enum Year {
		ONE(12),
		TWO(39),
		THREE(74),
		FOUR(98),
		FIVE(113),
		SIX(147),
		SEVEN(171),
		EIGHT(195),
		NINE(215),
		TEN(238),
		ELEVEN(253);
		
		private Year(final int code) {
			Code = code;
		}
		
		public int getCode() {
			return Code;
		}
		
		@Override
		public String toString() {
			return ""+Code;
		}
		
		private int Code;
	}
}
