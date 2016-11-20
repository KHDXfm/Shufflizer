###
# Shufflizer
# A Python script designed to simply automate a radio station
# Written by Jacob Turner
# Released under the MIT License
###

import glob
import random
import time

import mutagen.id3
import mutagen.mp3
import vlc

### CHANGE THESE VARIABLES ###
songpath = ""
idpath = ""
idtime = 0.0 # THIS IS IN SECONDS IN FLOAT FORM

### DO NOT CHANGE THESE VARIABLES ###
lastsong = ""

def prettytag(song):
    id3info = mutagen.id3.ID3(song)
    return "%s - %s" % (id3info['TPE1'].text[0], id3info['TIT2'].text[0])

def addtolog(prettydata):
    todaysdate = time.strftime("%m-%d-%Y")
    with open("songlog.%s.log" % todaysdate, "a+") as songlog:
        print time.strftime("%m-%d-%Y %I:%M:%S%p") + ": " + prettydata
        songlog.write(time.strftime("%m-%d-%Y %I:%M:%S%p") + ": " + prettydata + "\n")
    return

def play():
    instance = vlc.Instance()
    player = instance.media_player_new()
    songlist = glob.glob("%s/*.mp3" % songpath)
    idlist = glob.glob("%s/*.mp3" % idpath)
    timelastid = time.time()
    while True:
        if (time.time() - timelastid) <= idtime:
            while True:
                song = random.choice(songlist)
                global lastsong
                if song != lastsong:
                    lastsong = song
                    break
                else:
                    pass
            media = instance.media_new(song)
            timeinfo = mutagen.mp3.MP3(song)
            prettydata = prettytag(str(song))
        else:
            stationid = random.choice(idlist)
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