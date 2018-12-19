import java.io.*;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class processTwitterData {

	/*
	private static String _filePath = "G:/CSCDCourses/CSCD 599/PythonCode/twitterText.txt";
	private static String _outputFile = "G:/CSCDCourses/CSCD 599/processedTwitterData.csv"; 
	private static String _badOutputFile = "G:/CSCDCourses/CSCD 599/badProccedTwitterData.csv"; 
	private static String _statistics = "G:/CSCDCourses/CSCD 599/statistic.txt";
	*/
	
	
	private static String _filePath = "F:/twitterText.txt";
	private static String _outputFile = "F:/Thesis/processedTwitterData1.csv";
	private static String _badOutputFile = "F:/Thesis/badProccedTwitterData1.csv";
	private static String _statistics = "F:/Thesis/statistic1.txt";
	
	
	private static PrintWriter _processedTweet;
	private static PrintWriter _badProcessedTweet;
	private static PrintWriter _processedStatistics;

	private static JsonObject _tweet;
	private static JsonObject _user;
	private static JsonObject _place;
	private static JsonObject _entities;
	private static JsonArray _hashtags;
	private static JsonObject _delete;

	private static double _total;
	private static double _geoTotal;
	private static double _geoUseful;

	public static void main(String... args) throws FileNotFoundException {

		JsonParser fileParser = new JsonParser();
		_processedTweet = new PrintWriter(_outputFile);
		_badProcessedTweet = new PrintWriter(_badOutputFile);
		_processedStatistics = new PrintWriter(_statistics);

		Scanner fin = getScanner();

		Object obj = null;
		
		if (fin.hasNext()) {
			for(double x = 0; x < 35100000; x++) {
				fin.nextLine();
				fin.nextLine();
				if(x % 1000000 == 0)
					System.out.println("Processed 1000000 lines");
			}
		}
		System.out.println("Done with catching up to before crash.");
		
		
		while (fin.hasNext()) {
			try {
				obj = fileParser.parse(fin.nextLine());
				_tweet = (JsonObject) obj;
				establishClassLevelVariables();
				if (_delete == null) {
					JsonElement[] desiredElements = getDesiredElements();
					if (isGeoEnabled() || desiredElements[5] != null) {
						if (isInEnglish(desiredElements)) {
							outputTheDesiredElements(desiredElements);
							_geoTotal++;
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
					_processedStatistics.println("Total Count::" + _total + "::Total Geo_Enabled::" + _geoTotal
							+ "::Percentage Geo_Enabled::" + (_geoTotal / _total) + "::Total Useful::" + _geoUseful
							+ "::Percentage Useful::" + (_geoUseful / _total));
					_total = 0;
					_geoTotal = 0;
					_geoUseful = 0;
				}
				if (_delete != null)
					_delete = null;
				if (fin.hasNextLine())
					fin.nextLine();
				_total++;
			}
		}

		_processedStatistics.println("Total Count::" + _total + "::Total Geo_Enabled::" + _geoTotal
				+ "::Percentage Geo_Enabled::" + (_geoTotal / _total) + "::Total Useful::" + _geoUseful
				+ "::Percentage Useful::" + (_geoUseful / _total));
		fin.close();
		_badProcessedTweet.close();
		_processedTweet.close();
		_processedStatistics.close();
		System.out.println("Done!");

	}

	private static boolean isInEnglish(JsonElement[] desiredElements) {
		if (desiredElements[7].getAsString().equals("en"))// check this is right index
			return true;
		return false;
	}

	private static boolean isGeoEnabled() {
		if (_user != null) {
			Object geo = _user.get("geo_enabled");
			JsonElement isGeoEnabled = null;
			if (!(geo instanceof JsonNull)) {
				isGeoEnabled = (JsonElement) geo;
				if (isGeoEnabled.getAsString().equals("true"))
					return true;
			}
		}
		return false;
	}

	private static void printBadLine(Object obj) {
		_badProcessedTweet.println(obj);
	}

	private static void outputTheDesiredElements(JsonElement[] desiredElements) {

		if (desiredElements[5] == null)
			return;

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
		_processedTweet.println("");
		_geoUseful++;
	}

	private static JsonElement[] getDesiredElements() {
		JsonElement[] theElements = new JsonElement[8];

		theElements[0] = getCreated();
		theElements[1] = getTweetID();
		theElements[2] = getUserID();
		theElements[3] = _tweet.get("text");
		theElements[4] = getLocation();
		theElements[5] = getCoordinates();
		theElements[6] = getNameOfPlace();
		theElements[7] = getLang();

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
		Object place = _tweet.get("place");
		Object entities = _tweet.get("entities");
		Object delete = _tweet.get("delete");
		if (delete instanceof JsonObject) {
			_delete = _tweet.getAsJsonObject("delete");
			return;
		}
		if (!(user instanceof JsonNull))
			_user = _tweet.getAsJsonObject("user");
		if (!(place instanceof JsonNull))
			_place = _tweet.getAsJsonObject("place");
		try {
			_entities = (JsonObject) entities;
			_hashtags = _entities.getAsJsonArray("hashtags");
		} catch (ClassCastException e) {
			System.out.println(tweet);
		}

	}

	private static JsonElement getLocation() {
		if (_tweet != null)
			if (_user != null)
				return _user.get("location");

		return null;
	}

	private static JsonElement getCoordinates() {
		JsonElement firstLocation = findCoordinatesFromCoordinates();
		if (firstLocation != null)
			return firstLocation;
		JsonElement fourthLocation = findCoordinatesFromGeo();
		if (fourthLocation != null)
			return fourthLocation;
		JsonElement thirdLocation = findCoordinatesFromUserLocation();
		if (thirdLocation != null)
			return thirdLocation;
		JsonElement secondLocation = processBoundingBox();
		if (secondLocation != null)
			return secondLocation;

		return null;
	}

	private static JsonElement findCoordinatesFromGeo() {
		Object geo = _tweet.get("geo");
		if (!(geo instanceof JsonNull)) {
			JsonObject geoObject = (JsonObject) geo;
			JsonElement coordinates = geoObject.get("coordinates");
			return coordinates;
		}
		return null;
	}

	private static JsonElement findCoordinatesFromCoordinates() {
		try {
			JsonObject coordinates = (JsonObject) _tweet.get("coordinates");
			if (coordinates != null)
				return coordinates.get("coordinates");
			return null;
		} catch (ClassCastException e) {
			return null;
		}

	}

	private static JsonElement processBoundingBox() {
		JsonArray boundingBox = getBoundingBox();
		if (boundingBox != null) {
			return generateCentroid(boundingBox);
		}
		return null;
	}

	private static JsonElement generateCentroid(JsonArray boundingBox) {
		Gson bound = new Gson();
		float[][][] box = bound.fromJson(boundingBox, float[][][].class);
		return computeControid(box).get("coordinates");
	}

	private static JsonObject computeControid(float[][][] box) {
		float runningLat = 0;
		float runningLong = 0;
		for (int x = 0; x < box[0].length; x++) {
			for (int y = 0; y < box[0][x].length; y++) {
				if (y == 0)
					runningLat = runningLat + box[0][x][y];
				else if (y == 1)
					runningLong = runningLong + box[0][x][y];
			}
		}
		String toReturn = "{\"coordinates\":[" + (runningLat / box[0].length) + "," + (runningLong / box[0].length)
				+ "]}";
		JsonParser jsonParser = new JsonParser();
		JsonObject jo = (JsonObject) jsonParser.parse(toReturn);
		return jo;

	}

	private static JsonArray getBoundingBox() {
		try {
			if (_place != null) {
				JsonObject bounding_box = (JsonObject) _place.get("bounding_box");
				if (bounding_box != null) {
					JsonElement bBox = bounding_box.get("coordinates");
					if (bBox instanceof JsonArray)
						return (JsonArray) bBox;
				}
			}

			return null;
		} catch (ClassCastException e) {
			return null;
		}

	}

	private static JsonElement findCoordinatesFromUserLocation() {
		try {
			if (_user != null) {
				JsonObject derived = (JsonObject) _user.get("derived");
				if (derived != null) {
					JsonObject location = (JsonObject) derived.get("location");
					if (location != null) {
						JsonObject geo = (JsonObject) location.get("geo");
						if (geo != null)
							return geo.get("coordinates");
					}
				}
			}
			return null;

		} catch (ClassCastException e) {
			return null;
		}
	}

	private static Scanner getScanner() throws FileNotFoundException {
		Scanner toReturn = new Scanner(new File(_filePath));
		return toReturn;
	}

	public static JsonElement getNameOfPlace() {
		try {
			if (_place != null) {
				return _place.get("full_name");
			}
		} catch (ClassCastException e) {
			return null;
		}
		return null;
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
