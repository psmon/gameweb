/**
 * Created with JetBrains WebStorm.
 * User: psmon
 * Date: 13. 6. 26
 * Time: 오전 8:56
 * To change this template use File | Settings | File Templates.
 */

var BaseSprite = function(){
}
var _objRefCount=0;
var _workCount=0;

BaseSprite.prototype.addImage = function(url,x,y,hasLayer){
    _objRefCount++;
    var sprite = new cc.Sprite({ url: url })
    sprite.position = new cc.Point(x, y)
    var addObj = null;
    var isHasLayer = hasLayer!=undefined ? hasLayer:false;

    if(isHasLayer){
        var layer = new cc.Layer();
        layer.position = new cc.Point(x, y);
        sprite.position = new cc.Point(0, 0)
        layer.addChild(sprite,url+_objRefCount.toString());

        if(cc.Director.sharedDirector.isTouchScreen){
            layer.isTouchEnabled = true;
            layer.touchesEnded = function(event){
                if(layer.hitTestTouch(event,layer,sprite)){
                    layer.onMouseUp(event);
                    return true;
                }
            }
        }else{
            layer.isMouseEnabled=true;
            layer.mouseUp = function(event){
                if(layer.onMouseUp){
                    if(layer.hitTest(event,layer,sprite)){
                        layer.onMouseUp(event);
                        return true;
                    }
                }
                return false;
            }

            layer.mouseDragged = function(event){
                if(layer.onMouseDragged){
                    if(layer.hitTest(event,layer,sprite)){
                        layer.onMouseDragged(event);
                        return true;
                    }
                }
                return false;
            }
        }

        addObj = layer;
    }else{
        addObj = sprite;
    }

    this.addChild(addObj,url+_objRefCount.toString());
    return addObj;
}

BaseSprite.prototype.addSchedule= function(opt){
    if(!this._workList){
        this._workList = new Array();
    }
    _workCount++;
    this.schedule(opt);
    this._workList[ "workidx"+_workCount]=opt;
}

BaseSprite.prototype.addLabel = function(data){
    var label = new cc.Label(data);
    _objRefCount++;
    this.addChild(label,"label"+_objRefCount.toString());
    this.anchorPoint = cc.ccp(0,0);
    return label;
}

BaseSprite.prototype.hitTest = function(evt,obj,sprite){
    var mousex = evt.locationInCanvas.x;
    var mousey = evt.locationInCanvas.y;
    var myPos = obj.position;

    if( mousex > myPos.x - (sprite.contentSize.width/2) && (mousex < ( myPos.x - (sprite.contentSize.width/2) + sprite.contentSize.width) ) ){
        if( mousey > myPos.y - (sprite.contentSize.height/2)  && mousey < (myPos.y - (sprite.contentSize.height/2) + sprite.contentSize.height) ){
            return true;
        }
    }
    return false;
}

BaseSprite.prototype.hitTestTouch = function(evt,obj,sprite){
    var location = evt.touches[0].locationInCanvas;
    var mousex = location.x;
    var mousey = location.y;
    var myPos = this.position;
    var myPos = obj.position;

    if( mousex > myPos.x - (sprite.contentSize.width/2) && (mousex < ( myPos.x - (sprite.contentSize.width/2) + sprite.contentSize.width) ) ){
        if( mousey > myPos.y - (sprite.contentSize.height/2)  && mousey < (myPos.y - (sprite.contentSize.height/2) + sprite.contentSize.height) ){
            return true;
        }
    }
    return false;
}

BaseSprite.prototype.addChild = function(sprite,name){
    this._layer.addChild(sprite);
    if(name){
        this._childList[name] = sprite;
    }
}

BaseSprite.prototype.removeAll = function(){
    if(this._workList!=undefined){
        for( var i in this._workList){
            //스케줄 해제?
        }
    }
    if(this._childList!=undefined){
        for( var i in this._childList){
            this._childList[i].unscheduleAllSelectors();
            if(this._childList[i].removeAll){
                this._childList[i].removeAll();
            }
            this._layer.removeChild( this._childList[i] );

            this._layer.mouseDragged=null;

        }
    }
    this._childList = new Array();
    this._workList = new Array();
}

BaseSprite.prototype.runAni = function(x,y){
    var move               = new cc.MoveBy({ duration: 3, position: new cc.Point( x , y ) });
    var move_back = move.reverse();
    var delay              = new cc.DelayTime({ duration: 0.25 });
    var seq1 = new cc.Sequence({ actions: [move, delay, move_back, delay.copy()] });
    this.runAction(new cc.RepeatForever(seq1));
}

BaseSprite.prototype.seqTo = function(data){
    var seqData = new Array();
    for( var i in data){
        var curAni = data[i];
        var curAction = this.moveTo(curAni, false);
        seqData.push(curAction);
    }
    var seqAni = new cc.Sequence({ actions: seqData });
    this.runAction(seqAni);
}

BaseSprite.prototype.moveTo = function(data,isRun){
    var x=data.x;
    var y=data.y;
    var duration= data.duration ? data.duration:0;
    var delay= data.delay?data.delay:0;
    var complete = data.complete ? new cc.CallFunc({target:this,method:data.complete}) : null  ;
    var isRunAni = isRun!=undefined ? isRun : true;

    //var mainAni=null;
    //필수...
    var mainAni = new cc.MoveTo({ duration: duration, position: new cc.Point( x , y ) });
    if(data.angle){
        var rotateAni = new cc.RotateTo({duration: duration,angle:data.angle});
        mainAni = new cc.Spawn({one:mainAni,two:rotateAni});
    }

    if(data.opacity){
        var opacityAni = new cc.FadeTo({duration: duration,toOpacity:data.opacity});
        mainAni = new cc.Spawn({one:mainAni,two:opacityAni});
    }

    if(data.scale){
        var scaleAni = new cc.ScaleTo({duration: duration,scale : [data.scale ] });
        mainAni = new cc.Spawn({one:mainAni,two:scaleAni});
    }


    var easeAni=null;
    if(data.ease){
        switch(data.ease.type){
            case "EaseIn":
                easeAni = new cc.EaseIn({action:mainAni,rate:data.ease.rate});
                break;
            case "EaseInOut":
                easeAni = new cc.EaseInOut({action:mainAni,rate:data.ease.rate});
                break;
            case "EaseBounceOut":
                easeAni = new cc.EaseBounceOut({action:mainAni});
                break;
        }
        mainAni = easeAni;
    }

    var rValue = null;
    var delayani           = new cc.DelayTime({ duration: delay });
    //var seq1 = new cc.Sequence({ actions: [ delayani, move,complete  ] } );
    if(complete){
        rValue = new cc.Sequence({ actions: [ delayani, mainAni,  complete ] });
    }else{
        rValue = new cc.Sequence({ actions: [ delayani, mainAni  ] });
    }

    if(data.repeat){
        rValue = new cc.RepeatForever(rValue);
    }

    if(isRunAni)
        this.runAction(rValue);

    return rValue
}

var Application = function(divID,backcolor){
    BaseSprite.apply(this, arguments);
    for(i in BaseSprite.prototype) {
        cc.Sprite.prototype[i] = BaseSprite.prototype[i];
        cc.Label.prototype[i] = BaseSprite.prototype[i];
        if(i!="addChild") {
            cc.Layer.prototype[i] = BaseSprite.prototype[i];
        }
    }

    var director = cc.Director.sharedDirector;
    this._director = director;
    director.attachInView(document.getElementById(divID));
    director.displayFPS = false;
    director.backgroundColor=backcolor

    var size = director.winSize
    var scene = new cc.Scene
    this._scene = scene;
    var layer = new cc.Layer;
    layer.anchorPoint = cc.ccp(0, 0);

    this._layer = layer;
    layer.isMouseEnabled=true;
    scene.addChild(layer)

    this._childList = new Array();
}

Application.prototype = new BaseSprite();
Application.prototype.constructor = Application;
Application.prototype.run = function(){
    this._director.runWithScene( this._scene);
}

var SpriteAnimation = function(data){
    cc.Node.call(this);
    var fileName=data.fileName;
    var width=data.width;
    var height=data.height;
    var frameCount=data.frameCount;
    var oneDelay = data.oneDelay ? data.oneDelay : 0.2;
    var fileType = data.fileType ? data.fileType : "png";
    var repeat = data.repeat ? data.repeat : false;
    var animFrames = new Array();
    var sprite;
    for(var i=0; i<frameCount; i++){
        var texture = new cc.Texture2D({url:  fileName + (i+1) +'.' + fileType  });
        var frame0 = new cc.SpriteFrame({texture: texture, rect: cc.rectMake(0, 0, width, height)});
        animFrames.push(frame0);

        if(i==0)
            sprite = new cc.Sprite({frame: frame0});
    }

    var animation = new cc.Animation({frames: animFrames, delay: oneDelay });
    var animate =  new cc.Animate({animation: animation, restoreOriginalFrame: false});

    if(repeat){
        animate = new cc.RepeatForever(animate);

    }

    //sprite.position=new geo.Point(0, 0);
    //sprite.position=new Vpoint(0, 0 , 300);
    //sprite.anchorPoint=cc.ccp(0,0);
    sprite.scale= data.scale ? data.scale : 1.0;

    //this.addChild({child:sprite});
    this.addChild(sprite);

    var action = animate;
    this.Sprite = sprite;
    this.Action = action;
}

SpriteAnimation.prototype = new cc.Node();

SpriteAnimation.prototype.play = function(){
    this.Sprite.stopAllActions();
    this.Sprite.runAction(this.Action);
}

SpriteAnimation.prototype.stop = function(){
    this.Sprite.stopAllActions();
}

