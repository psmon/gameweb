# PSMON's LightWeight Game Kit

- launchurl : http://localhost:8080/

## Application Layout
- controller : endpoint for ws
- entity : local db
- game : game logic
- message : realtime game message
- fakegame : test object (without process,just obj for websock)
- static : html5 client resources

## Server

spring boot 2 + websocket

We configured only the minimum web socket connection network. Game logic can be filled in by you.

![...](doc/ws-server.png)

## Client

pscocos : A long time ago, a legacy canvas library that modified cocos 2d.js

doc : http://psmon.x-y.net/pscoco/sample.html
