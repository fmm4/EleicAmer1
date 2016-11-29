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
	          .setOAuthConsumerKey("Z6PXBblm7JLiCSBMGy4TMrG9z")
	          .setOAuthConsumerSecret("xYyO8DZQjBydMaWY53SXaJPy1owXgNMyD5RgHD8gCfbaQHd0oR")
	          .setOAuthAccessToken("4277442394-ajzWomJerOij6DKfFSxbi5Gy62tflz81Ct3KbyW")
	          .setOAuthAccessTokenSecret("xp8RQ2Sqc9YRmKl2PG5XspNaPBJxPAnblo6rkQR9QIy2W");
	    TwitterFactory tf = new TwitterFactory(cb.build());
	    Twitter twitter = tf.getInstance();
	        try {
	            Query query = new Query(stringToSearch);
	            QueryResult result;
	            result = twitter.search(query);
	            //List<Status> tweets = result.getTweets();
	           // for (Status tweet : tweets) {
	            //    System.out.println("@" + tweet.getUser().getScreenName() + " - " + tweet.getText());
	           // }
	            return result;
	        } catch (TwitterException te) {
	            te.printStackTrace();
	            return null;
	        }
	}
}
