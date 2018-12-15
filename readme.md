# PSMON's LightWeight WebGame Kit

- git : https://github.com/psmon/gameweb
- launchurl : http://localhost:8080/

## Application Layout
- controller : endpoint for ws
- entity : local db
- game : game logic
- message : realtime game message
- fakegame : test object (without process,just obj for websock)
- static : html5 client resources

master repository does not have game logic. If you have a minimal kit for multiplayer and you have a game idea, you can start off with the master branch


## Server

spring boot 2 + websocket

We configured only the minimum web socket connection network. Game logic can be filled in by you.

![...](doc/ws-server.png)

## Client

pscocos : A long time ago, a legacy canvas library that modified cocos 2d.js by psmon

If you want to create more complex and colorful games. Convert html5 canvas and jquery to modern development

doc : http://psmon.x-y.net/pscoco/sample.html

## Large capacity distributed processing system

Prototypes created here can be mass-processed using the following techniques.

url : https://github.com/psmon/springcloud