###
# Shufflizer
# A Python script designed to simply automate a radio station
# Written by Jacob Turner
# Released under the MIT License
###

# Python imports
import csv
import datetime
import glob
import os
import random
import time

# Local imports
import mutagen.id3
import mutagen.mp3
import vlc

### CHANGE THESE VARIABLES, BUT LEAVE THE LOWERCASE r ###
songpath = r""
idpath = r""
idtime = 1200.0 # THIS IS IN SECONDS (DEFAULT IS 20 MINUTES)

### DO NOT CHANGE THESE VARIABLES ###
lastid = ""
logtimeformat = "%m-%d-%Y %I:%M:%S%p"
lastfive = []

# From http://stackoverflow.com/a/33681543
def in_between(now, start, end):
    if start <= end:
        return start <= now < end
    else: # over midnight e.g., 23:30-04:15
        return start <= now or now < end

def istime():
    day = time.strftime("%A")
    try:
        with open('schedule.csv', 'rb') as csvfile:
            reader = csv.reader(csvfile, delimiter=',', quotechar='|')
            for row in reader:
                if day == row[0]:
                    strttime = datetime.time(int(row[1].split(":")[0]),
                                             int(row[1].split(":")[1]))
                    endtime = datetime.time(int(row[2].split(":")[0]),
                                            int(row[2].split(":")[1]))
                    if in_between(datetime.datetime.now().time(),
                                  strttime, endtime):
                        return row[3]
        return ""
    except IOError:
        return ""

def getsong(genre=""):
    if genre == "":
        songlist = glob.glob("%s/*/*.mp3" % songpath)
    else:
        songlist = glob.glob("%s/%s/*.mp3" % (songpath, genre))
    while True:
        song = random.choice(songlist)
        global lastfive
        if song not in lastfive:
            lastfive.append(song)
            if len(lastfive) >= 5:
                lastfive.pop(0)
            break
        else:
            pass

def prettytag(song):
    id3info = mutagen.id3.ID3(song)
    return "%s - %s" % (id3info['TPE1'].text[0], id3info['TIT2'].text[0])

def addtolog(prettydata):
    todaysdate = time.strftime("%m-%d-%Y")
    dircheck = os.path.isdir("logs")
    if not dircheck:
        os.mkdir("logs")
    with open("logs/songlog.%s.log" % todaysdate, "a+") as songlog:
        print time.strftime(logtimeformat) + ": " + prettydata
        songlog.write(time.strftime(logtimeformat) + ": " + prettydata + "\n")
    return

def play():
    instance = vlc.Instance()
    player = instance.media_player_new()
    idlist = glob.glob("%s/*.mp3" % idpath)
    timelastid = time.time()
    while True:
        if (time.time() - timelastid) <= idtime:
            genrecheck = istime()
            song = getsong(genrecheck)
            media = instance.media_new(song)
            timeinfo = mutagen.mp3.MP3(song)
            prettydata = prettytag(str(song))
        else:
            while True:
                stationid = random.choice(idlist)
                global lastid
                if stationid != lastid:
                    lastid = stationid
                    break
                else:
                    pass
            media = instance.media_new(stationid)
            timeinfo = mutagen.mp3.MP3(stationid)
            prettydata = prettytag(str(stationid))
            timelastid = time.time()
        player.set_media(media)
        addtolog(prettydata)
        player.play()
        time.sleep(timeinfo.info.length)

if __name__ == "__main__":
    play()