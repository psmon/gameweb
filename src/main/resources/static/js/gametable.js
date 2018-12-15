
var _demoRefCount = 0;

var _seqCount=0;
function displayPoint(x,y){
    _seqCount++;
    var label = cocoApp.addLabel({string: _seqCount , fontName:"Arial",fontSize:16,fontColor:"red" });
    label.position.x=x;
    label.position.y=y;
}

var medalPos =new Array({x:50,y:400},{x:150,y:450},{x:270,y:500},{x:420,y:500},{x:550,y:500},{x:650,y:450},{x:750,y:400});

var playerinfos = new Array();

function createDelaers() {
    playerinfos.push({'name':'ply1','chips':100 });
    playerinfos.push({'name':'ply2','chips':100 });
    playerinfos.push({'name':'ply3','chips':100 });
    playerinfos.push({'name':'ply4','chips':100 });
    playerinfos.push({'name':'ply5','chips':100 });
    playerinfos.push({'name':'ply6','chips':100 });
    playerinfos.push({'name':'ply7','chips':100 });

    var dealer = cocoApp.addImage('img/table/dealer-button.png', medalPos[0].x, medalPos[0].y-100);

    var idx=0;
    playerinfos.forEach(function(plyinfo) {
        var imgUrl='img/avartar/'+ Number(idx+1).toString() +'-crop.png';
        var img = cocoApp.addImage(imgUrl, medalPos[idx].x, medalPos[idx].y);
        img.scale=0.5
        idx++;
    });

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
            back.scale = 0.7
            break;

        case "gameinit":
            createDelaers();
            break;

    }
}