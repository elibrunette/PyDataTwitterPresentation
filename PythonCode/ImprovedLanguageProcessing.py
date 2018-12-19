#Tweepy implementation
import tweepy
import time
import json
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
    # def on_status(self, status):
        # try: 
            # if status.retweeted_status:
                # return
            # print(status)
        # except: 
            # time.sleep(5)

    def on_status(self, status):
        if status.retweeted_status:
            return 
        print(status)

    def on_error(self, status):
        print(status)
    
if __name__ == '__main__':

    #This handles Twitter authetification and the connection to Twitter Streaming API
    l = StdOutListener()
    auth = OAuthHandler(consumer_key, consumer_secret)
    auth.set_access_token(access_token, access_token_secret)
    stream = Stream(auth, l)
    
    stream.filter(languages=["en"], track=['flu', 'influenza'])
    