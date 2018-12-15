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

function runDemo(app,demoType){
    var director = cc.Director.sharedDirector;
    var canvasWidth=800;
    var canvasHeight=600;
    cc.Director.sharedDirector.transparent=false;
    cocoApp.removeAll();

    cocoApp._layer.mouseDragged = null;

    switch(demoType){
        case "seqTo":
            _seqCount=0;
            director.backgroundColor="gray";
            var   pathList = new Array();

            var easeOptList =[{ type:"EaseBounceOut"} , { type:"EaseInOut", rate:3 },{ type:"EaseInOut", rate:3 } ]

            var img1 = cocoApp.addImage('assets/frogy1.png', 120,100, false);
            var img2 = cocoApp.addImage('assets/ball1.png', 120,100, false);

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

        case "cardani":
            director.backgroundColor="gray";
            for(var i=0;i<52;i++){
                var imgRandom = cocoApp.addImage('assets/cardback.png', 50,100);
                imgRandom.moveTo({x:548,y:200-i*0.2,duration:2,delay:i*0.1,angle:90,opacity:50,scale:0.5});
            }
            break;

        case "EasyIn":
            director.backgroundColor="gray";
            for(var i=0;i<52;i++){
                var imgRandom = cocoApp.addImage('assets/cardback.png', 50,100);
                var easeOpt = { type:"EaseIn", rate:3 };
                imgRandom.moveTo({x:548,y:250-i*0.2,duration:2,delay:i*0.1,angle:90,opacity:50,scale:0.5,ease:easeOpt});
            }
            break;

        case "EaseInOut":
            director.backgroundColor="gray";
            for(var i=0;i<52;i++){
                var imgRandom = cocoApp.addImage('assets/cardback.png', 50,100);
                var easeOpt = { type:"EaseInOut", rate:3 };
                imgRandom.moveTo({x:548,y:300-i*0.2,duration:2,delay:i*0.1,angle:90,opacity:50,scale:0.5,ease:easeOpt});
            }
            break;

        case "EaseBounceOut":
            director.backgroundColor="gray";
            for(var i=0;i<52;i++){
                var imgRandom = cocoApp.addImage('assets/cardback.png', 50,100);
                var easeOpt = { type:"EaseBounceOut"};
                imgRandom.moveTo({x:548,y:350-i*0.2,duration:5,delay:i*0.1,angle:90,opacity:50,scale:0.5,ease:easeOpt});
            }
            break;

        case "clickEvent":
            director.backgroundColor="gray";
            var img3 = cocoApp.addImage('assets/cardback.png', 120,200, true);
            var img4 = cocoApp.addImage('assets/cardback.png', 200,200, true);
            img3.onMouseUp = function(event){
                img3.position.y=img3.position.y+5;
            }
            img4.onMouseUp = function(event){
                img4.position.y=img4.position.y-5;
            }
            break;
        case "paticle":
            director.backgroundColor="black";
            var ballList = new Array();
            var imgList = ["bubble1.png","dust1.png"];
            var curBackGround = imgList[ Math.round(Math.random() * 1 ) ];

            //怨듭쓽 �앹꽦..
            for(var i=0;i<50;i++){
                //var ballImg = cocoApp.addImage('assets/paricle1.png',200,200,false);
                var ballImg = cocoApp.addImage('assets/effect/' + curBackGround ,200,200,false);

                ballList.push(ballImg);
                var circleSpeed = 2;
                var randomDir = Math.random()*2*Math.PI;
                ballImg.xSpeed=circleSpeed*Math.cos(randomDir);
                ballImg.ySpeed=circleSpeed*Math.sin(randomDir);
            }
            //泥ル쾲吏� 怨듭뿉寃�!! 紐⑤뱺怨듭쓽 �뚮뜑瑜� �대떦..
            ballList[0].addSchedule({method:function(){
                    for(var i=0; i<ballList.length;i++){
                        var curBall = ballList[i];
                        curBall.position = cc.ccp(curBall.position.x+curBall.xSpeed, curBall.position.y+curBall.ySpeed);
                        if(curBall.position.x>canvasWidth){
                            curBall.position=cc.ccp(curBall.position.x-canvasWidth, this.position.y);
                        }
                        if(curBall.position.x<0){
                            curBall.position=cc.ccp( curBall.position+canvasWidth, this.position.y);
                        }
                        if(curBall.position.y>canvasHeight){
                            curBall.position = cc.ccp( curBall.position.x, curBall.position.y-canvasHeight);
                        }
                        if(curBall.position.y<0){
                            curBall.position = cc.ccp( curBall.position.x, curBall.position+canvasHeight);
                        }
                    }
                },target:null,interval:0,pause:false});
            //�먮쾲吏� 怨듭뿉寃�..二쇨린�곸씤 珥덇린�붾� �대떦..
            ballList[1].addSchedule({method:function(){
                    var ranX = Math.round(Math.random() * canvasWidth );
                    var ranY = Math.round(Math.random() * canvasHeight );
                    for(var i=0; i<ballList.length;i++){
                        var curBall = ballList[i];
                        curBall.position = cc.ccp( ranX,ranY );
                        var circleSpeed = 2;
                        var randomDir = Math.random()*2*Math.PI;
                        ballImg.xSpeed=circleSpeed*Math.cos(randomDir);
                        ballImg.ySpeed=circleSpeed*Math.sin(randomDir);
                    }
                },target:null,interval:3,pause:false});
            break;
        case "greenBall":
            for(var i=0;i<100;i++){
                var ballImg = cocoApp.addImage('assets/greencircle.png',0,-10,false);
                ballImg.position.x = Math.round(Math.random() * canvasWidth );
                ballImg.position.y = Math.round(Math.random() * canvasHeight );
                ballImg.drift = Math.random();
                ballImg.speed = Math.round(Math.random() * 5) + 1;
                ballImg.addSchedule({method:function(){
                        this.speed = Math.round(Math.random() * 3) + 1;
                        if(this.position.y <= canvasHeight){
                            this.position.y =  this.position.y+this.speed;
                            if(this.position.y > canvasHeight){
                                this.position.y = -5;
                            }
                            this.position.x+=this.drift;
                            if(this.position.x > canvasWidth){
                                this.position.x=0
                            }
                        }
                    },target:ballImg,interval:0,paused:false});
            }
            break;

        case "rainy":
            _demoRefCount++;
            cc.Director.sharedDirector.transparent=true;
            var back = cocoApp.addImage('assets/background/mybelo2.jpg', 0,0, false);
            back.anchorPoint.x=0;
            back.anchorPoint.y=0;

            var belotxt = cocoApp.addImage('assets/effect/belotxt.png', 180,430, false);
            belotxt.anchorPoint.x=0;
            belotxt.anchorPoint.y=0;
            //belotxt.angle=90;
            //belotxt.rotation=7;
            var ease = { type:"EaseBounceOut"};
            belotxt.moveTo({x:180+30,y:430,duration:0.5,delay:2,angle:7 ,ease:ease ,repeat:false , complete:function(){
                    //belotxt.position.x=180;
                    //belotxt.position.y=430;
                    //belotxt.rotation=0;
                }
            });

            var lineAni = new SpriteAnimation({fileName:"assets/effect/line",scale:0.6,width:1198,height:1081,repeat:true,frameCount:4,oneDelay:0.5  });
            lineAni.position.x=0;
            lineAni.position.y=0;
            lineAni.Sprite.anchorPoint.x=0;
            lineAni.Sprite.anchorPoint.y=0;
            //lineAni.Sprite.opacity=95;
            lineAni.play();
            cocoApp.addChild(lineAni,"lineAni"+ _demoRefCount );


            canvasWidth-=100;
            back.scale=0.6
            for(var i=0;i<50;i++){
                var ballImg = cocoApp.addImage('assets/effect/bubble1.png',0,-10,false);
                ballImg.position.x = Math.round(Math.random() * canvasWidth );
                ballImg.position.y = Math.round(Math.random() * canvasHeight );
                ballImg.scaleX=0.3;
                ballImg.scaleY=Math.round(Math.random() * 10) + 5;
                ballImg.drift = Math.random();
                ballImg.opacity=80
                ballImg.speed = Math.round(Math.random() * 15) + 10;
                ballImg.addSchedule({method:function(){
                        //this.speed = Math.round(Math.random() * 20) + 1;
                        if(this.position.y <= canvasHeight){
                            this.position.y =  this.position.y+this.speed;
                            if(this.position.y > canvasHeight){
                                this.position.y = -5;
                            }
                            this.position.x+=this.drift;
                            if(this.position.x > canvasWidth){
                                this.position.x=0
                            }
                        }
                    },target:ballImg,interval:0,paused:false});
            }
            break;

        case "fallball":
            director.backgroundColor="gray";
            var back = cocoApp.addImage('assets/background/streetgirl.jpg', 400,300, false);
            back.scale=1.2
            for(var i=0; i<200 ; i++){
                var x = Math.floor(Math.random() * canvasWidth) + 1;
                var tx = Math.floor(Math.random() * canvasWidth) + 1;
                var ty = canvasHeight- Math.floor(Math.random() * 10);
                var startScale=Math.floor(Math.random() + 0.3 );
                var tAngle = Math.floor(Math.random() * 90) + 1;
                var ball = cocoApp.addImage('assets/ball1.png', x,-100, false);
                ball.scale=startScale;
                ball.moveTo({x:tx,y:ty,duration:3,delay:i*0.1,angle:tAngle,opacity:50,scale:0.5});
            }
            break;

        case "drawBall":
            director.backgroundColor="gray";
            var label = cocoApp.addLabel({string:"留덉슦�ㅻ� �댁슜�댁꽌 洹몃┝�� 洹몃젮蹂댁꽭��",fontName:"Arial",fontSize:16,fontColor:"WHITE" });
            label.position.x=200;
            label.position.y=50;
            cocoApp._layer.mouseDragged  = function(evt){
                var ball = cocoApp.addImage('assets/ball1.png', evt.x , evt.y-20 , false);
                ball.scale=0.5;
            }
            break;

        case "SpriteAnimation":
            director.backgroundColor="red";
            for(var i=0 ; i< 10 ; i++){
                _demoRefCount++;
                var frogAni = new SpriteAnimation({fileName:"assets/ani/flog/frogy",width:700,height:1000,scale:0.3,repeat:true,frameCount:15,oneDelay:0.03 * (i+1)  });
                frogAni.position.x=70 * i;
                frogAni.position.y=150;
                cocoApp.addChild(frogAni,"frog"+ _demoRefCount );
                frogAni.play();
            }
            var label = cocoApp.addLabel({string:"�붽��쒕떎",fontName:"Arial",fontSize:26,fontColor:"WHITE" });
            label.position.x=200;
            label.position.y=300;
            label.moveTo({x:400,y:350,duration:5,delay:0.5,angle:0,scale:3.0});
            break;

        case "DinoPlay":
            director.backgroundColor="white";
            _demoRefCount++;
            var dinoAni = new SpriteAnimation({fileName:"assets/ani/dino/d",fileType:"jpg" ,scale:0.7, width:657,height:429,frameCount:71,oneDelay:0.005, repeat:false  });
            dinoAni.position.x=180;
            dinoAni.position.y=180;
            dinoAni.runCount = 0;
            cocoApp.addChild(dinoAni,"dinoani"+ _demoRefCount );
            var back = cocoApp.addImage('assets/tv.png', 0,0, true);
            var label = cocoApp.addLabel({string:"Click Me",fontName:"Arial",fontSize:26,fontColor:"White" });
            label.position.x=180;
            label.position.y=285;
            back.scale=0.4;

            dinoAni.isAlive = true;
            back.onMouseUp=function(evt){
                if(dinoAni.isAlive){
                    dinoAni.position.x-=5;
                    dinoAni.play();
                    dinoAni.runCount++;
                    label.string = "Click " + dinoAni.runCount;
                }
            }
            back.addSchedule({method:function(){
                    dinoAni.speed = 2;
                    dinoAni.position.x+=dinoAni.speed;
                    if(dinoAni.position.x > 450){
                        label.string = "Dino Die";
                        dinoAni.isAlive = false;
                        back.unscheduleAllSelectors();
                    }
                },target:back,interval:0.15,paused:false});
            break;

        case "Dino":
            director.backgroundColor="white";
            _demoRefCount++;
            var dinoAni = new SpriteAnimation({fileName:"assets/ani/dino/d",fileType:"jpg" ,scale:0.5, width:657,height:429,frameCount:71,oneDelay:0.2, repeat:true  });
            dinoAni.position.x=500;
            dinoAni.position.y=100;
            cocoApp.addChild(dinoAni,"dinoani"+ _demoRefCount );
            dinoAni.play();

            _demoRefCount++;
            var dinoAni = new SpriteAnimation({fileName:"assets/ani/dino/d",fileType:"jpg" ,scale:0.5, width:657,height:429,frameCount:71,oneDelay:0.15, repeat:true  });
            dinoAni.position.x=450;
            dinoAni.position.y=350;
            cocoApp.addChild(dinoAni,"dinoani"+ _demoRefCount );
            dinoAni.play();

            _demoRefCount++;
            var dinoAni = new SpriteAnimation({fileName:"assets/ani/dino/d",fileType:"jpg" ,scale:0.5, width:657,height:429,frameCount:71,oneDelay:0.01, repeat:true  });
            dinoAni.position.x=150;
            dinoAni.position.y=400;
            cocoApp.addChild(dinoAni,"dinoani"+ _demoRefCount );
            dinoAni.play();

            _demoRefCount++;
            var dinoAni = new SpriteAnimation({fileName:"assets/ani/dino/d",fileType:"jpg" ,scale:0.7, width:657,height:429,frameCount:71,oneDelay:0.05, repeat:true  });
            dinoAni.position.x=180;
            dinoAni.position.y=130;
            cocoApp.addChild(dinoAni,"dinoani"+ _demoRefCount );
            dinoAni.play();

            var back = cocoApp.addImage('assets/tv.png', 200,135, false);
            back.scale=0.4;

            var back = cocoApp.addImage('assets/tv.png', 490,118, false);
            back.scale=0.28;

            var back = cocoApp.addImage('assets/tv.png', 440,118+240, false);
            back.scale=0.28;

            var back = cocoApp.addImage('assets/tv.png', 440-295,118+290, false);
            back.scale=0.28;

            var label = cocoApp.addLabel({string:"Design By YOBI",fontName:"Arial",fontSize:26,fontColor:"White" });
            label.position.x=180;
            label.position.y=235;
            break;

        case "createLabel":
            director.backgroundColor="black";

            var get_random_color=function() {
                var letters = 'ABCDE'.split('');
                var color = '#';
                for (var i=0; i<3; i++ ) {
                    color += letters[Math.floor(Math.random() * letters.length)];
                }
                return color;
            }

            var colorList = ["black","maroon","green","olive","navy","purple","coral","#A9A9A9","#E9967A","#2F4F4F"]
            for(var i=0;i<20;i++){
                var curRanColor = get_random_color();
                var label = cocoApp.addLabel({string:"Html5 Canvas Label-" + curRanColor  ,fontName:"Arial",fontSize:26,fontColor:curRanColor });

                var firstPos = {x:150 + (i*30) , y:100 + (i*20) }
                var secondPos = {x:150 + ( (30*20) -  i*30) , y:100 + (i*20) }

                label.position.x=firstPos.x;
                label.position.y=firstPos.y;

                var label2 = cocoApp.addLabel({string:"Html5 Canvas Label-" + curRanColor  ,fontName:"Arial",fontSize:26,fontColor:curRanColor });
                label2.position.x= secondPos.x;
                label2.position.y= secondPos.y;

                var easeOptList =[{ type:"EaseBounceOut"} , { type:"EaseInOut", rate:3 },{ type:"EaseInOut", rate:3 } ];
                var easeOpt = easeOptList[0];

                label.moveTo({x:secondPos.x,y:secondPos.y,duration:3,delay:0.5 ,ease:easeOpt  });
                label2.moveTo({x:firstPos.x,y:firstPos.y,duration:3,delay:0.5 ,ease:easeOpt  });

            }
            break;
    }
}