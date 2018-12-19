# -*- coding: utf-8 -*-
"""
Created on Tue May 22 21:24:34 2018

@author: csfaculty
"""

import json
from pprint import pprint


def ProcessLargeTextFile():
    #bunchsize = 1000000     # Experiment with different sizes
    #bunch = []
    #with open("filepath", "r") as r, open("outfilepath", "w") as w:
    with open("twitterText.txt", "r") as inputFile:
        for line in inputFile:
            print("it worked")
            data = json.load(line)
            pprint(data)
            #pprint(data["user"]["geo_enabled"])
            #pprint(data["coordinates"])
            
            #x, y, z, rest = line.split(' ', 3)
            #bunch.append(' '.join((x[:-3], y[:-3], z[:-3], rest)))
            #if len(bunch) == bunchsize:
            #    w.writelines(bunch)
            #    bunch = []
        #w.writelines(bunch)
        
#to import into a json object
#from pprint import pprint

#with open('data.json') as f:
#    data = json.load(f)

#pprint(data)

#to print out data from the data object
#data["maps"][0]["id"]
#data["masks"]["id"]
#data["om_points"]
        
if __name__ == '__main__':
    ProcessLargeTextFile()
    # with open("twitterText.txt", "r") as inputFile:
    #    for line in inputFile:
    #        print("it worked")
    #        data = json.load(line)
    #        pprint(data)