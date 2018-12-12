/**
 * Created with JetBrains WebStorm.
 * User: psmon
 * Date: 13. 6. 26
 * Time: �ㅼ쟾 8:56
 * To change this template use File | Settings | File Templates.
 */
var _demoRefCount = 0;

var _seqCount=0;
function displayPoint(x,y){
    _seqCount++;
    var label = cocoApp.addLabel({string: _seqCount , fontName:"Arial",fontSize:16,fontColor:"red" });
    label.position.x=x;
    label.position.y=y;
}

function runDemo(app,demoType) {
    var director = cc.Director.sharedDirector;
    var canvasWidth = 800;
    var canvasHeight = 600;
    cc.Director.sharedDirector.transparent = false;
    cocoApp.removeAll();

    cocoApp._layer.mouseDragged = null;
    switch(demoType){
        case "seqTo":
            _seqCount=0;
            director.backgroundColor="gray";
            var   pathList = new Array();

            var easeOptList =[{ type:"EaseBounceOut"} , { type:"EaseInOut", rate:3 },{ type:"EaseInOut", rate:3 } ]

            var img1 = cocoApp.addImage('img/frogy1.png', 120,100, false);
            var img2 = cocoApp.addImage('img/ball1.png', 120,100, false);

            for(var i=0 ; i< 25 ; i++){
                var obj={};
                obj.x = Math.round(Math.random() * canvasWidth );
                obj.y = Math.round(Math.random() * canvasHeight );
                obj.duration = Math.random() + 0.2;
                obj.angle = Math.round(Math.random() * 360 );
                //obj.opacity = Math.round(Math.random() * 10  );
                obj.scale = Math.random() + 0.5;
                obj.ease =easeOptList[ Math.round(Math.random() * 3 )  ]
                pathList.push(obj);
            }

            for(var i=0 ;i<pathList.length;i++){
                var curInfo = pathList[i];
                displayPoint(curInfo.x,curInfo.y);
            }
            img2.seqTo(pathList);
            pathList.reverse();
            img1.seqTo(pathList);
            break;
        case "EasyIn":
            director.backgroundColor="gray";
            for(var i=0;i<52;i++){
                var imgRandom = cocoApp.addImage('img/cardback.png', 50,100);
                var easeOpt = { type:"EaseIn", rate:3 };
                imgRandom.moveTo({x:548,y:250-i*0.2,duration:2,delay:i*0.1,angle:90,opacity:50,scale:0.5,ease:easeOpt});
            }
            break;

        case "EaseInOut":
            director.backgroundColor="gray";
            for(var i=0;i<52;i++){
                var imgRandom = cocoApp.addImage('img/cardback.png', 50,100);
                var easeOpt = { type:"EaseInOut", rate:3 };
                imgRandom.moveTo({x:548,y:300-i*0.2,duration:2,delay:i*0.1,angle:90,opacity:50,scale:0.5,ease:easeOpt});
            }
            break;

        case "EaseBounceOut":
            director.backgroundColor="gray";
            for(var i=0;i<52;i++){
                var imgRandom = cocoApp.addImage('img/cardback.png', 50,100);
                var easeOpt = { type:"EaseBounceOut"};
                imgRandom.moveTo({x:548,y:350-i*0.2,duration:5,delay:i*0.1,angle:90,opacity:50,scale:0.5,ease:easeOpt});
            }
            break;

        case "clickEvent":
            director.backgroundColor="gray";
            var img3 = cocoApp.addImage('img/cardback.png', 120,200, true);
            var img4 = cocoApp.addImage('img/cardback.png', 200,200, true);
            img3.onMouseUp = function(event){
                img3.position.y=img3.position.y+5;
            }
            img4.onMouseUp = function(event){
                img4.position.y=img4.position.y-5;
            }
            break;

    }
}