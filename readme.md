# PSMON's LightWeight WebGame Kit

- run : mvn spring-boot:run
- launchurl : http://localhost:8080/

## Sample Demo ( Multiplayer)

![](http://wiki.webnori.com/download/attachments/17727533/image2018-12-24_23-12-40.png?version=1&modificationDate=1545660760701&api=v2)
movie  : [link](http://wiki.webnori.com/display/devbegin/multiplayer-cardgame?preview=/17727533/17727532/play-demo.mp4)

## Application Layout
- controller : endpoint for ws
- fakegame : sample server logic
- message : realtime game message
- static : html5 client resources ( pure javascript )

## Server

spring boot 2 + websocket

This allows you to learn basic web socket handles for multiplayer.

There is a simple code to work with, but it is a sample code that multiplayer works. Fill in your ideas in a more advanced way.

![...](doc/ws-server.png)

## Client

pscocos : legacy canvas library that modified cocos 2d.js by psmon -http://psmon.x-y.net/pscoco/sample.html

    This game demo is written in the old Cansvas module and pure JavaScript.
    If you want to create more complex and colorful games. 
    We recommend using the TypeScript or better Canvas module.

## Large capacity distributed processing system

The server module is written in a simple threaded model. If you want distributed applications, 
use more advanced messages and Dispatcher

Large capacity processing is being prepared in the next corner.
url : https://github.com/psmon/springcloud

## Expansion

Topic : Evolution of message patterns in server modules

How to: 
- Block all shared objects
- Each object can only talk through messages

Detailed:
- Thread Model(Share Object) - > Actor Model






