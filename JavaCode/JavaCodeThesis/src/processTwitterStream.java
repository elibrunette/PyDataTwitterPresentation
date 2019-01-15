import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class processTwitterStream {
	
	//TODO Change me!
	private static String _filePath = "C:/Users/ebrunette/Documents/Presentation/PyDataTwitterPresentation/DataBackup/MoreData/twitterText.txt";
	private static String _outputFile = "C:/Users/ebrunette/Documents/Presentation/PyDataTwitterPresentation/JavaCode/JavaCodeThesis/processedTwitterData.csv";
	private static String _badOutputFile = "C:/Users/ebrunette/Documents/Presentation/PyDataTwitterPresentation/JavaCode/JavaCodeThesis/badProccedTwitterData.csv";
	private static String _statistics = "C:/Users/ebrunette/Documents/Presentation/PyDataTwitterPresentation/JavaCode/JavaCodeThesis/statistic.txt";
	
	
	private static PrintWriter _processedTweet;
	private static PrintWriter _badProcessedTweet;
	private static PrintWriter _processedStatistics;

	private static JsonObject _tweet;
	private static JsonObject _user;
	private static JsonObject _entities;
	private static JsonArray _hashtags;
	private static JsonObject _delete;

	private static double _total;
	private static double _retweetedTotal;

	public static void main(String... args) throws FileNotFoundException {

		JsonParser fileParser = new JsonParser();
		_processedTweet = new PrintWriter(_outputFile);
		_badProcessedTweet = new PrintWriter(_badOutputFile);
		_processedStatistics = new PrintWriter(_statistics);

		Scanner fin = getScanner();

		Object obj = null;
		
		while (fin.hasNext()) {
			try {
				obj = fileParser.parse(fin.nextLine());
				_tweet = (JsonObject) obj;
				establishClassLevelVariables();
				if (_delete == null) {
					JsonElement[] desiredElements = getDesiredElements();
					if (!retweeted(desiredElements)) {
						if (isInEnglish(desiredElements)) {
							outputTheDesiredElements(desiredElements);
						}
					}
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
				System.out.println(e.getStackTrace());
				System.out.println(obj);
				printBadLine(obj);
			} finally {
				if (_total > 100000) {
					System.out.println("Processed 100000 records");
					_processedStatistics.println("Total Tweets Processed:" + _total + "::Total amount of retweets:" +
													_retweetedTotal + "::Percentage of retweets:" +(_retweetedTotal/_total));//Output processed Statistics
					_total = 0;
					_retweetedTotal = 0;
				}
				if (_delete != null)
					_delete = null;
				if (fin.hasNextLine())
					fin.nextLine();
				_total++;
			}
		}

		_processedStatistics.println("Total Tweets Processed:" + _total + "::Total amount of retweets:" 
										+ _retweetedTotal + "::Percentage of retweets:" +(_retweetedTotal/_total));//Output what we want to see in the statistics
		fin.close();
		_badProcessedTweet.close();
		_processedTweet.close();
		_processedStatistics.close();
		System.out.println("Done!");

	}

	private static boolean retweeted(JsonElement[] theElements) {
		String tweetText = theElements[2].getAsString();
		if(tweetText.startsWith("RT @")) {
			_retweetedTotal++;
			return true;
		}
		return false;
	}

	private static boolean isInEnglish(JsonElement[] desiredElements) {
		if (desiredElements[4].getAsString().equals("en"))// check this is right index
			return true;
		return false;
	}

	private static void printBadLine(Object obj) {
		_badProcessedTweet.println(obj);
	}

	private static void outputTheDesiredElements(JsonElement[] desiredElements) {

		String toOutput = desiredElements[0].getAsString();
		for (int x = 1; x < desiredElements.length; x++) {
			toOutput = toOutput + "::" + desiredElements[x];
		}
		Gson gson = new Gson();
		Hashtags[] theHashtags = gson.fromJson(_hashtags, Hashtags[].class);

		if (theHashtags.length != 0) {
			String hash = "[" + theHashtags[0];
			for (int x = 1; x < theHashtags.length; x++) {
				hash = hash + "::" + theHashtags[x].getText();
			}
			hash = hash + "]";
			toOutput = toOutput + "::" + hash;
		}
		_processedTweet.println(toOutput);
	}

	private static JsonElement[] getDesiredElements() {
		JsonElement[] theElements = new JsonElement[5];

		theElements[0] = getTweetID();
		theElements[1] = getCreated();
		theElements[2] = _tweet.get("text");
		theElements[3] = getUserID();
		theElements[4] = getLang();

		return theElements;
	}

	private static JsonElement getCreated() {
		try {
			if (_tweet != null)
				return _tweet.get("created_at");
		} catch (ClassCastException e) {
			return null;
		}
		return null;

	}

	private static JsonElement getTweetID() {
		try {
			if (_tweet != null)
				return _tweet.get("id_str");
		} catch (ClassCastException e) {
			return null;
		}
		return null;

	}

	private static JsonElement getUserID() {
		try {
			if (_user != null)
				return _user.get("id");
		} catch (ClassCastException e) {
			return null;
		}
		return null;

	}

	private static void establishClassLevelVariables() {
		Object tweet = _tweet;
		Object user = _tweet.get("user");
		Object entities = _tweet.get("entities");
		Object delete = _tweet.get("delete");
		if (delete instanceof JsonObject) { //check for edge case of a deletedf tweet
			_delete = _tweet.getAsJsonObject("delete");
			return;
		}
		if (!(user instanceof JsonNull))
			_user = _tweet.getAsJsonObject("user");
		try {
			_entities = (JsonObject) entities;
			_hashtags = _entities.getAsJsonArray("hashtags");
		} catch (ClassCastException e) {
			System.out.println(tweet);
		}

	}

	private static Scanner getScanner() throws FileNotFoundException {
		Scanner toReturn = new Scanner(new File(_filePath));
		return toReturn;
	}

	private static JsonElement getLang() {
		try {
			if (_user != null)
				return _user.get("lang");
		} catch (ClassCastException e) {
			return null;
		}
		return null;

	}
}
