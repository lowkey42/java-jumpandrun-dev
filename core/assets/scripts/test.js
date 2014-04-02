importPackage(Packages.de.secondsystem.game01.model);
importPackage(Packages.de.secondsystem.game01.impl.game.controller);
importPackage(Packages.de.secondsystem.game01.impl.map.physics);
importPackage(Packages.de.secondsystem.game01.impl.game.entities); 
importPackage(Packages.de.secondsystem.game01.impl.timer);
importPackage(Packages.de.secondsystem.game01.impl.game.entities.events);

function init() {
	println("Ich bin ein Script für die map "+mapId);
}


var page_01_collected_icon = API.createSprite("page_chaos.png", 10,10);
API.updateSpriteColor(page_01_collected_icon, 255, 255, 255, 100);
if( !API.exists("page_01_collected") ) {
	API.updateSpriteTex(page_01_collected_icon, "page_chaos_disabled.png");
	entities.create("collectable_page", {"representation":"page_chaos.png", "x":1600, "y":250, "onTOUCHED":{
		"factory":".CondEHF", "if":"IS_PLAYER", "arg":1, "then":{"factory":".BlockEHF", "subs":[
			{"factory":".ScriptEHF", "body":"API.store('page_01_collected', true); API.updateSpriteTex(page_01_collected_icon, 'page_chaos.png');API.updateSpriteColor(page_01_collected_icon, 255, 255, 255, 200)" },
			{"factory":".DeleteEHF"}
		]}
	}});
}

function touchFunc(entity) {
	entity.jump();
}

    
function timerFunc(timer, entity, arg1, arg2) {  
	entity.jump();
	println("Works like a charm. " + timer.getTickCount() + arg1 + arg2);
	
	// disable the timer after 5 onTick() calls
	if( timer.getTickCount() == 5 ) {
		timer.setEnabled(false);
		println("The timer is now disabled.");
	}
}

function anotherTimerFunc(timer) {
	
}

function liftedFunc() {
	println("Let me down...");
}

function unliftedFunc() {
	println("Thanks.");
}

function onJump() {
	println("Woooohoooo !");
}

//var entity = entities.createControllable( "enemy", {"x": 150, "y": 200} );
//entity.setEventHandler( events.createScriptedEvents( {"LIFTED": "liftedFunc", "UNLIFTED": "unliftedFunc"} ) );
//map.getTimerManager().createTimer(1000, true, "timerFunc", entity, " test1 ", " test2 ");

//var movingPlatform = entities.createControllable( "moving platform", {"x": 150, "y": 100} );
//var movingPlatformCon = new PatrollingController(movingPlatform, true);
//movingPlatformCon.addTargetPoint(300, 100);
//movingPlatformCon.addTargetPoint(150, 100);
//movingPlatformCon.addTargetPoint(150, -100);
//movingPlatformCon.play();

entities.create("box", {"x": 300, "y": 0, "liftable":false} );
// entities.create("box", {"x": 150, "y": -100, "density":2} );

entities.create("light", {"x": 150, "y": -100, "worldId":3} );

for( var i=0; i<10; i++ )	
	entities.create("light", {"x": 500+i*200, "y": 200, "worldId":1} );
	
for( var i=0; i<10; i++ )	
	entities.create("light", {"x": i*100, "y": 100, "worldId":2} );

//API.playMonologue("test-mon");

timer.createTimer(1000, false, function(){
	API.playMonologue("test-mon");
});

	