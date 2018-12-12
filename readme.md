# StandAlone for HTML5 Multiplayer MiniGame

- url : http://localhost:8080/

## Application Layout
- controller : endpoint for ws
- entity : local db
- game : game logic
- message : realtime game message
- fakegame : test object (without process,just obj for websock)
- static : html5 client resources

## Server

We configured only the minimum web socket connection network. Game logic can be filled in by you.

![...](doc/ws-server.png)

## Client

pscocos : A long time ago, a legacy canvas library that modified cocos 2d.js

![...](doc/canvas.gif)