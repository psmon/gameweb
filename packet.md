# Game Packet - NoJson Simpe Plain/Text

## Note
req : request packet ( client -> server )
res : response packet ( server -> client )

### Error Msg
- res : error!!SomeError

### Join Table
- req : join!!1
- res : info!!conok!!

### Game Action
- req : pass!!me
- req : change!!2
- res : changed!!2!!1
- res : pass!!2
- res : yourcard!!3!!4
- res : bet!!2!!30

### Table Action
- req : seat!!me
- res : seat!!2!!psmon!!500
- res : dealer!!2

### Result
- res : showcard!!4!!2
- res : winner!!5!!300