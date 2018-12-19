#Import the necessary methods from tweepy library
#import tweepy
#from tweepy.streaming import StreamListener


#Twtiter package implementation
#from twitter import *
#import json
#

#
#oauth = OAuth(access_token, access_token_secret, consumer_key, consumer_secret)
#
#twitterStream = TwitterStream(auth = oauth)
#
#iterator = twitterStream.statuses.sample()
#
#for tweet in iterator:
#    print(json.dump(tweet))




#Tweepy implementation
import tweepy
import time
from tweepy import OAuthHandler
from tweepy import Stream
from tweepy.streaming import StreamListener
#Variables that contains the user credentials to access Twitter API 

access_token = "973353710456745986-tygfB9kuAsFUxaudnDboDbORX0djFNm"
access_token_secret = "TuynzwkzNMdb9O8j750cNAlHzRS6aOW5YSIQQz6jCqig0"
consumer_key = "97ST0Fwyzsis57tAeuKZ67Bp6"
consumer_secret = "UVqyWX8NBt7PrHNorOv0dNaFsS4qJZ77Rgo5HqHVHNImuHvCwa"

auth = OAuthHandler(consumer_key, consumer_secret)
auth.set_access_token(access_token, access_token_secret)


api = tweepy.API(auth)

#This is a basic listener that just prints received tweets to stdout.
class StdOutListener(StreamListener):

    def on_data(self, data):
        try:
            twitterText = open("twitterText1.json", "a+")
            twitterText.write(data)
            twitterText.close
            return True
        except:
             print('failed onData')
             time.sleep(5)

    def on_error(self, status):
        print(status)
        

#     def on_status(self, data):
#         print(data.text)
#         if data.coordinates:
#             print('coords:', data.coordinates)
#         if data.place:
#             print('place:', data.place.full_name)

#         return True

#     on_event = on_status

#     def on_error(self, status):
#         print(status)
    
    
if __name__ == '__main__':

    #This handles Twitter authetification and the connection to Twitter Streaming API
    l = StdOutListener()
    auth = OAuthHandler(consumer_key, consumer_secret)
    auth.set_access_token(access_token, access_token_secret)
    stream = Stream(auth, l)
    
    loc = [-132.604601, 23.273509, -63.399011, 49.803863]
    stream.filter(locations=loc)
#    for tweet in tweets:
#        if tweet.lang == "en":# and tweet.geo != "null":
#            print(tweet.text)
    #This line filter Twitter Streams to capture data by the keywords: 'flu'
    #stream.sample()
    #stream.sample()
        
        
    
    