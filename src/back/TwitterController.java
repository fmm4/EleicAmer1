package back;

import java.util.List;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;


public class TwitterController {
	
	public TwitterController(){}
	
	public static QueryResult searchTweetsWith(String stringToSearch)
	{
	    ConfigurationBuilder cb = new ConfigurationBuilder();
	    cb.setDebugEnabled(true)
	          .setOAuthConsumerKey("FIq7HSkEl45Z7QLVaENmyhCN3")
	          .setOAuthConsumerSecret("Mpltm5iLOH85EgwLnVKEjlgISXFxB6zvGgvolmRyGjJJFlUnlD")
	          .setOAuthAccessToken("5DVlxFRHYHtMt0RVvhiyvD9HqmeUODkkv8AyBxZ1")
	          .setOAuthAccessTokenSecret("bOu1JXy2wzOleS3ujNJxYvquSVRc52SHtKMOc2KyQbOJ8");
	    TwitterFactory tf = new TwitterFactory(cb.build());
	    Twitter twitter = tf.getInstance();
	    List<Status> p;
	        try {
	            Query query = new Query(stringToSearch);
	            query.setLang("en");
//	            query.setSince(sdf.format(startDate));
//	            query.setUntil(sdf.format(endDate));
	            

	            QueryResult result;
	            result = twitter.search(query);
	            List<Status> tweets = result.getTweets();
	            int num = 0;
	            for (Status tweet : tweets) {
	                if(!tweet.getText().contains("RT") && !tweet.getText().contains("&&"))
	                {
	                	System.out.println(tweet.getText());
	                }
	            }

	            return result;
	        } catch (TwitterException te) {
	            te.printStackTrace();
	            return null;
	        }
	}
}
