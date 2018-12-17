var _demoRefCount = 0;
var dealer;
var _seqCount=0;
var medalPos =new Array({x:50,y:400},{x:150,y:450},{x:270,y:500},{x:420,y:500},{x:550,y:500},{x:650,y:450},{x:750,y:400});
var playerinfos = new Array();
var userCards = new Array();

function displayPoint(x,y){
    _seqCount++;
    var label = cocoApp.addLabel({string: _seqCount , fontName:"Arial",fontSize:16,fontColor:"red" });
    label.position.x=x;
    label.position.y=y;
}


function clearStage() {
}

function testShowCard(seatno) {
;
    var cardimg=['c1_001.jpg','c1_003.jpg','c1_003.jpg','c1_005.jpg','c1_005.jpg'];
    var backcard=cocoApp.addImage('img/cards/back.png', medalPos[seatno].x, medalPos[seatno].y-100);
    var cardshape=cocoApp.addImage('img/cards/'+cardimg[seatno], medalPos[seatno].x, medalPos[seatno].y-100);
    cardshape.scale=0.3;
    userCards[seatno].opacity=0;
}

function testCardChange() {
    var easeOptList = [{type: "EaseBounceOut"}, {type: "EaseInOut", rate: 3}, {type: "EaseInOut", rate: 3}];
    var easeOpt = easeOptList[0]
    var easeOpt2 = easeOptList[1]
    userCards[0].moveTo({x: medalPos[3].x, y: medalPos[3].y-100, duration: 2, delay: 0.5,ease:easeOpt });
    userCards[3].moveTo({x: medalPos[0].x, y: medalPos[0].y-100, duration: 2, delay: 1.0,ease:easeOpt2 });
    userCards[1].moveTo({x: medalPos[4].x, y: medalPos[4].y-100, duration: 2, delay: 2.5,ease:easeOpt });
    userCards[4].moveTo({x: medalPos[1].x, y: medalPos[1].y-100, duration: 2, delay: 3.0,ease:easeOpt2 });
}


function seatInPlayer(gameMessage) {
    var seatno = gameMessage.seatno;
    var imgUrl='img/avartar/'+ Number(seatno+1).toString() +'-crop.png';
    var img = cocoApp.addImage(imgUrl, medalPos[seatno].x, medalPos[seatno].y);
    img.scale=0.5
}

function processTableMessage(gameMessage) {
    var aniDelay = gameMessage.delay;
    var seatno = gameMessage.seatno;
    switch(gameMessage.content){
        case "readytable":
            renderTable('background');
            dealer = cocoApp.addImage('img/table/dealer-button.png', medalPos[0].x, medalPos[0].y-100);
            break;
        case "seat":
            seatInPlayer(gameMessage);
            break;
        case "dealer":
            //dealer.moveTo({x:medalPos[0].x,y:medalPos[0].y-100,duration:2,delay:aniDelay*0.1,angle:90,opacity:50,scale:0.5});
            dealer.moveTo({x:medalPos[seatno].x,y:medalPos[seatno].y-200,duration:1,delay:aniDelay*0.1 });
            setTimeout(function(){
                testCardChange()
            }, 4000);
            break;
        case "card":
            var card = cocoApp.addImage('img/cards/casino-back.png', 720, 100);
            card.moveTo({x:medalPos[seatno].x,y:medalPos[seatno].y-100,duration:0.5,delay:aniDelay*0.5,angle:180 });
            userCards.push(card);
            setTimeout(function(){
                testShowCard(seatno)
            }, 9000 +(seatno+1*500));
            break;
    }
}

function renderTable(cmd1,cmd2) {

    var director = cc.Director.sharedDirector;
    var canvasWidth = 800;
    var canvasHeight = 600;
    cc.Director.sharedDirector.transparent = false;

    switch(cmd1) {
        case "intro":
            cocoApp.removeAll();
            cocoApp._layer.mouseDragged = null;
            director.backgroundColor = "#2ECCFA";
            var back = cocoApp.addImage('img/introback.png', 330, 450);
            back.scale = 1.2

            var get_random_color = function () {
                var letters = 'ABCDE'.split('');
                var color = '#';
                for (var i = 0; i < 3; i++) {
                    color += letters[Math.floor(Math.random() * letters.length)];
                }
                return color;
            }

            var colorList = ["black", "maroon", "green", "olive", "navy", "purple", "coral", "#A9A9A9", "#E9967A", "#2F4F4F"]
            for (var i = 0; i < 10; i++) {
                var curRanColor = get_random_color();
                var label = cocoApp.addLabel({
                    string: "Friends",
                    fontName: "Arial",
                    fontSize: 26,
                    fontColor: curRanColor
                });

                var firstPos = {x: 70 + (i * 30), y: 90 + (i * 20)}
                var secondPos = {x: 70 + ((30 * 20) - i * 30), y: 90 + (i * 20)}

                label.position.x = firstPos.x;
                label.position.y = firstPos.y;

                var label2 = cocoApp.addLabel({
                    string: "CARD",
                    fontName: "Arial",
                    fontSize: 26,
                    fontColor: curRanColor
                });
                label2.position.x = secondPos.x;
                label2.position.y = secondPos.y;

                var easeOptList = [{type: "EaseBounceOut"}, {type: "EaseInOut", rate: 3}, {type: "EaseInOut", rate: 3}];
                var easeOpt = easeOptList[0];
                label.moveTo({x: secondPos.x, y: secondPos.y, duration: 3, delay: 0.5, ease: easeOpt});
                label2.moveTo({x: firstPos.x, y: firstPos.y, duration: 3, delay: 0.5, ease: easeOpt});
            }
            break;
        case "background":
            cocoApp.removeAll();
            cocoApp._layer.mouseDragged = null;
            director.backgroundColor = "black";
            var back = cocoApp.addImage('img/table/casino-green.png', 390, 300);
            back.scale = 0.7;
            userCards = new Array();
            break;

        case "gameinit":
            createDelaers();
            break;

    }
}