var _demoRefCount = 0;
var dealer;
var _seqCount=0;
var medalPos =new Array({x:50,y:400},{x:150,y:450},{x:270,y:500},{x:420,y:500},{x:550,y:500},{x:650,y:450},{x:750,y:400});
var playerinfos = new Array();
var userCards = new Array();
var yourCard;
var needSelectCard=false;
var mySeatNo=0;
var betPotList=[];
var backGround;
var lblActionInfo;

var avableSeat=[false,false,false,false,false,false,false]

var UserBox = new Array(
    {avatar:null,titile:null,chip:null,card:null,scard:null,cardrect:null},{avatar:null,titile:null,chip:null,card:null,scard:null,cardrect:null},
        {avatar:null,titile:null,chip:null,card:null,scard:null,cardrect:null},{avatar:null,titile:null,chip:null,card:null,scard:null,cardrect:null},
        {avatar:null,titile:null,chip:null,card:null,scard:null,cardrect:null},{avatar:null,titile:null,chip:null,card:null,scard:null,cardrect:null},
        {avatar:null,titile:null,chip:null,card:null,scard:null,cardrect:null}
    );

function displayPoint(x,y){
    _seqCount++;
    var label = cocoApp.addLabel({string: _seqCount , fontName:"Arial",fontSize:16,fontColor:"red" });
    label.position.x=x;
    label.position.y=y;
}

// ### UI Update By Message
function userBoxVisible(seatno,opacity) {
    UserBox[seatno].avatar.opacity=opacity;
    UserBox[seatno].titile.opacity=opacity;
    UserBox[seatno].chip.opacity=opacity;
    UserBox[seatno].card.opacity=opacity;
    UserBox[seatno].scard.opacity=opacity;
}

function seatIn(gameMessage) {
    var seatno=gameMessage.seatno;
    var username=gameMessage.sender;
    var chip=gameMessage.num1;
    UserBox[seatno].avatar.opacity=100;
    UserBox[seatno].titile.opacity=300;
    UserBox[seatno].chip.opacity=300;
    UserBox[seatno].card.opacity=0;
    UserBox[seatno].scard.opacity=0;
    UserBox[seatno].titile.string=username;
    UserBox[seatno].chip.string=chip;
    avableSeat[seatno]=true;
}

function indicator(gameMessage) {
    var seatno=gameMessage.seatno;
    var idx=0;
    avableSeat.forEach(function(isSeat) {
        if(isSeat){
            if(idx==seatno){
                UserBox[seatno].avatar.opacity=300;
            }else{
                UserBox[seatno].avatar.opacity=100;
            }
        }
        idx=id+1;
    });

}

function winner(gameMessage) {
    //TODO : using servervalue
    var seatno=1;
    if(betPotList!=null){
        //TODO : Remove Chips
        var idx=0;
        betPotList.forEach(function(chipimg) {
            var easeOptList = [{type: "EaseInOut", rate: 3}, {type: "EaseInOut", rate: 3},{type: "EaseBounceOut"}];
            var easeOpt2 = easeOptList[1];
            var tx = medalPos[seatno].x - 30  + Math.floor((Math.random() * 10) + 10);
            var ty = medalPos[seatno].y - 20  + Math.floor((Math.random() * 10) + 10);;
            chipimg.opacity=100
            chipimg.moveTo({x: tx, y: ty, duration: 1 ,delay:0.2,ease:easeOpt2,angle:360*3,opacity:0 });
            idx++;
        });
    }
}

function betting(gameMessage) {
    var seatno=gameMessage.seatno;
    var chip=gameMessage.num1;
    var total=gameMessage.num2;
    var delay=gameMessage.delay*2;
    UserBox[seatno].chip.string=total;

    var chipCnt =  chip/5;
    var delayAny=0.0;
    for(var idx=0;idx<chipCnt;idx++){
        var chipurl='img/chips/chip5.png';
        var chipimg = cocoApp.addImage(chipurl, medalPos[seatno].x, medalPos[seatno].y);
        var easeOptList = [{type: "EaseInOut", rate: 3}, {type: "EaseInOut", rate: 3},{type: "EaseBounceOut"}];
        var easeOpt = easeOptList[3];
        chipimg.scale=0.5;
        var easeOpt2 = easeOptList[ idx%3 ];
        var tx = Math.floor((Math.random() * 100) + 350);
        var ty = Math.floor((Math.random() * 100) + 250);
        var angle1 = Math.floor((Math.random() * 100) + 50)
        var angle2 = Math.floor((Math.random() * 500) + 50)
        chipimg.moveTo({x: tx, y: ty, duration: 0.5, delay: delay+delayAny,ease:easeOpt2,angle:angle2 });
        betPotList.push(chipimg);
        delayAny+=0.2;
    }
}


function seatOut(seatno) {
    UserBox[seatno].avatar.opacity=0;
    UserBox[seatno].titile.opacity=0;
    UserBox[seatno].chip.opacity=0;
    UserBox[seatno].card.opacity=0;
    UserBox[seatno].scard.opacity=0;
    avableSeat[seatno]=false;
}

// ### GameAction ( Server -> Client )
function action(gameMessage) {
    needSelectCard=true;
    lblActionInfo.string="Seclect Your Card(don't chage) Or Opernet Card"
}

// ## SendGameAction ( Client -> Server )



// ### First Load at Connected

function itItStage() {
    for(var seatno=0;seatno<7;seatno++){
        UserBox[seatno].card.opacity=0;
    }
    needSelectCard=false;
    if(betPotList!=null){
        //TODO : Remove Chips
        betPotList.forEach(function(element) {
            element.visible=false
        });
        betPotList=[];
    }
}

function textInfoLoad() {
    var lblInfo1 = cocoApp.addLabel({string:"YourCard",fontName:"Arial",fontSize:26,fontColor:"WHITE" });
    lblActionInfo = cocoApp.addLabel({string:"...",fontName:"Arial",fontSize:26,fontColor:"WHITE" });
    lblInfo1.position.x=50;
    lblInfo1.position.y=30;
    lblActionInfo.position.x=100;
    lblActionInfo.position.y=200;

}

function swapcard(gameMessage){
    var sSeatNo=gameMessage.num1;
    var tSeatNo=gameMessage.num2;
    var myPos={x:medalPos[sSeatNo].x,y:medalPos[sSeatNo].y-100 };
    var targetPos={x:medalPos[tSeatNo].x,y:medalPos[tSeatNo].y-100 };
    var easeOptList = [{type: "EaseBounceOut"}, {type: "EaseInOut", rate: 3}, {type: "EaseInOut", rate: 3}];
    var easeOpt = easeOptList[0];
    var easeOpt2 = easeOptList[1];
    var tmpcard=UserBox[sSeatNo].card;
    UserBox[sSeatNo].card.moveTo({x: targetPos.x, y: targetPos.y, duration: 2, delay: 0.5,ease:easeOpt });
    UserBox[tSeatNo].card.moveTo({x: myPos.x, y: myPos.y, duration: 2, delay: 1.0,ease:easeOpt2 });
    UserBox[sSeatNo].card=UserBox[tSeatNo].card;
    UserBox[tSeatNo].card=tmpcard;
}

function changeCard(targetSeatNo) {
    if(avableSeat[targetSeatNo]==false) return;

    if(needSelectCard){
        if(targetSeatNo==mySeatNo){
            sendGameAction({content:"nochange",num1:0,num2:0})
        }else{
            //UserBox[mySeatNo].cardrect.seatno=targetSeatNo;
            //UserBox[targetSeatNo].cardrect.seatno=mySeatNo;
            sendGameAction({content:"change",num1:targetSeatNo,num2:0})
        }
    }
    needSelectCard=false;
}

function userBoxLoad() {
    for(var seatno=0;seatno<7;seatno++){
        var imgUrl='img/avartar/'+ Number(seatno+1).toString() +'-crop.png';
        var avatar = cocoApp.addImage(imgUrl, medalPos[seatno].x, medalPos[seatno].y);
        avatar.scale=0.5;

        var card = cocoApp.addImage('img/cards/casino-back.png', medalPos[seatno].x, medalPos[seatno].y-100);

        var scard = cocoApp.addImage('img/cards/back.png', medalPos[seatno].x, medalPos[seatno].y-100);

        var lblName = cocoApp.addLabel({string:"noname",fontName:"Arial",fontSize:26,fontColor:"WHITE" });
        lblName.position.x=medalPos[seatno].x;
        lblName.position.y=medalPos[seatno].y+60;

        var lblChip = cocoApp.addLabel({string:"0",fontName:"Arial",fontSize:26,fontColor:"WHITE" });
        lblChip.position.x=medalPos[seatno].x;
        lblChip.position.y=medalPos[seatno].y+90;

        var clickArea = cocoApp.addImage('img/cards/casino-back.png', medalPos[seatno].x, medalPos[seatno].y-100,true);
        clickArea.seatno=seatno;
        clickArea.children[0].opacity=1;
        UserBox[seatno].cardrect=clickArea;

        //clickArea.onMouseUp = function(event){
        //}
        UserBox[seatno].cardrect.onMouseUp = (function(event) {
            return function(event) {
                console.log("Clicked:"+this.seatno)
                changeCard(this.seatno)
            }
        })();


        UserBox[seatno].avatar=avatar;
        UserBox[seatno].card=card;
        UserBox[seatno].titile=lblName;
        UserBox[seatno].chip=lblChip;
        UserBox[seatno].scard=scard;
        userBoxVisible(seatno,0);
    }
}

function inItTable() {
    userBoxLoad();
    textInfoLoad();
}


// #### UI TestCode
function testShowCard(seatno) {
    var cardimg=['c1.jpg','c2.jpg','c3.jpg','c4.jpg','c5.jpg','c6.jpg','c7.jpg','c8.jpg'];
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


function messageControler(gameMessage) {
    var aniDelay = gameMessage.delay;
    var seatno = gameMessage.seatno;
    switch(gameMessage.content){
        case "readytable":
            sceneControler('background');
            dealer = cocoApp.addImage('img/table/dealer-button.png', medalPos[0].x, medalPos[0].y-100);
            inItTable();
            break;
        case "stagestart":
            itItStage();
            lblActionInfo.string="stage start"
            break;
        case "seat":
            seatIn(gameMessage);
            break;
        case "seatout":
            seatOut(seatno);
            break;
        case "bet":
            betting(gameMessage);
            lblActionInfo.string="AutoBet:10"
            break;
        case "indicator":
            indicator(gameMessage)
            break;
        case "action":
            action(gameMessage);
            break;
        case "dealer":
            //dealer.moveTo({x:medalPos[0].x,y:medalPos[0].y-100,duration:2,delay:aniDelay*0.1,angle:90,opacity:50,scale:0.5});
            dealer.moveTo({x:medalPos[seatno].x,y:medalPos[seatno].y-200,duration:1,delay:aniDelay*0.1 });
            break;
        case "card":
            lblActionInfo.string="Card...";
            UserBox[seatno].card.opacity=300;
            UserBox[seatno].card.position.x=720;
            UserBox[seatno].card.position.y=100;
            UserBox[seatno].card.angle=70;
            UserBox[seatno].card.moveTo({x:medalPos[seatno].x,y:medalPos[seatno].y-100,duration:0.5,delay:aniDelay,angle:360*3 });
            //UserBox[seatno].cardrect.seatno=seatno;
            break;
        case "swapcard":
            swapcard(gameMessage);
            break;

        case "showcard":
        case "changed":
            if(gameMessage.content=="changed")
                lblActionInfo.string="CardChange";
            else{
                lblActionInfo.string="CardSetting";
                mySeatNo=seatno;
            }
            var cardimg=['c1.jpg','c2.jpg','c3.jpg','c4.jpg','c5.jpg','c6.jpg','c7.jpg','c8.jpg'];
            if(yourCard!=null){
                //Todo : Remove Instance
                yourCard.visible=false;
            }
            var cardshape=cocoApp.addImage('img/cards/'+ cardimg[gameMessage.num1], 50, 100);
            cardshape.scale=0.3;
            yourCard=cardshape;
            break;
        case "actionend":
            lblActionInfo.string="";
            break;
        case "gameresult":
            lblActionInfo.string="GameResult";
            winner(gameMessage);
            break;

    }
}

function sceneControler(cmd1, cmd2) {

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
            back.scale = 1.2;
            backGround=back;

            var get_random_color = function () {
                var letters = 'ABCDE'.split('');
                var color = '#';
                for (var i = 0; i < 3; i++) {
                    color += letters[Math.floor(Math.random() * letters.length)];
                }
                return color;
            }

            var colorList = ["black", "maroon", "green", "olive", "navy", "purple", "coral", "#A9A9A9", "#E9967A", "#2F4F4F"]
            var intoTexts = "G^e^n^i^u^s^C^a^r^d".split("^");
            for (var i = 0; i < intoTexts.length; i++) {
                var curRanColor = get_random_color();
                var label = cocoApp.addLabel({
                    string: intoTexts[i],
                    fontName: "Arial",
                    fontSize: 50,
                    fontColor: curRanColor
                });
                //var firstPos = {x: 70 + ((30 * 20) - i * 30), y: 90 + (i * 20)}
                var firstPos = {x:Math.floor((Math.random() * 300) + 50),y:Math.floor((Math.random() * 300) + 50)}
                var secondPos = {x: 70 + (i * 50), y: 90 + (i * 20)}
                label.position.x = firstPos.x;
                label.position.y = firstPos.y;
                label.angle=  Math.floor((Math.random() * 100) + 50)

                var easeOptList = [{type: "EaseBounceOut"}, {type: "EaseInOut", rate: 3}, {type: "EaseInOut", rate: 3}];
                var easeOpt = easeOptList[i%3];
                label.moveTo({x: secondPos.x, y: 200, duration: 3, delay: 0.1, ease: easeOpt,angle:720});


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