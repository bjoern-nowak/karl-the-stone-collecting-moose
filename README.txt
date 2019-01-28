### Reset exmaples ###
In order to remove all examples you may delete the folder miniworldDB in the application folder when not running it.

### Using this application in a tutor/student environment ###
A specific property file will be created in the user-home directory under directory .miniworld, if not already present.
This property file defines the connection details for java RMI and in which role the application should start.
It has to be changed according to your environment per machine.
This file gets loaded once on start.
For testing purposes you may start the app, edit property file and start a second instance.
Window title is changing based on which role you have.


### known errors ###
none


### incomplete exercises ###
exercise sheet 2 - exercise 4 - build a console interface to test the inner model
exercise sheet 7 - exercise 14 - start/pause/stop of thread
exercise sheet 7 - exercise 15 - make application thread-safe
(exercise sheet 9 - exercise 19 - world xml loading by SAX / DOM / StAXCursor / StAXIterator) <-- used java.beans.XMLDecoder