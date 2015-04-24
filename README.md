# Live Group Chat

Live Group Chat can broadcast the group chat records of WeChat (or other Instant Messaging apps) to a web page.

Live Group Chat contains an Android app and a web site. The former hooks to the Android notification system and send the chat records to the web site via WebSocket; the latter receives the chat records and shows them in a web page.

The web site is developed by using [Meteor](https://www.meteor.com).

For a live demonstration, please visit [NGOS live discussion site](http://ngsos.fullstackengineer.net/).

## Usage
### Android app
* Install Live Group Chat app.
* Enable Notification service (Settings->Acessibility->Live Group Chat).
* Input correct websocket url, username and password.
* Click Start button.

### Web site
#### Build meteor application bundle
``` shell
$ cd live-group-chat/server
$ meteor build dest_dir # the output directory will contain just a tarball that includes everything necessary to run the application server. (server.tar.gz)
```
#### Deploy application on the internet
* External dependency:[Node.js 0.10.36](http://nodejs.org/dist/v0.10.36/),  [MongoDB](http://www.mongodb.org/downloads).
* To run the application:
``` shell
$ (cd programs/server && npm install)
$ export PORT="3000"
$ export MONGO_URL='mongodb://user:password@host:port/databasename'
$ export ROOT_URL='http://example.com'
$ export MAIL_URL='smtp://user:password@mailhost:port/'
$ node main.js  # PORT="80", need root privileges
```



