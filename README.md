# Shufflizer
**Shufflizer** is a simple radio automator written for [KHDX 93.1](http://khdx.fm). 

Shufflizer was coded by [Jacob Turner](http://jacobturner.me), who releases this code under the MIT license.

**NOTE: Some features are dependent on [Nicecast](https://www.rogueamoeba.com/nicecast/) by Rogue Amoeba Software.

This project uses the following libraries:
* [mp3agic](http://github.com/mpatric/mp3agic), released under the MIT license

## Schedule Syntax
If a schedule.csv file is present, then it will be scanned to see if a certain genre should be played at certain times.
Genres are in folders under the songpath variable (for example, if songpath is C:\Music, the genre folder is C:\Music\Genre).
The syntax for it is as follows:  
Example: ***Sunday,00:00,05:00,Jazz***
* ***Sunday:*** Full name of the day of the week
* ***00:00:*** Beginning time (in 24-hour time)
* ***05:00:*** Ending time (in 24-hour time)
* ***Jazz:*** Genre to play in between times
