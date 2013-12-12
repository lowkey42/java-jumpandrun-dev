importPackage(Packages.de.secondsystem.game01.model);
importPackage(Packages.de.secondsystem.game01.impl.game.controller);
importPackage(Packages.de.secondsystem.game01.impl.map.physics);
importPackage(Packages.de.secondsystem.game01.impl.game.entities); 
importPackage(Packages.de.secondsystem.game01.impl.timer);
importPackage(Packages.de.secondsystem.game01.impl.game.entities.events);

println("Ich bin ein Script für die map "+mapId);

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

//var entity = entities.createControllable( "enemy", {"x": 150, "y": 200} );
//entity.setEventHandler( events.createScriptedEvents( {"LIFTED": "liftedFunc", "UNLIFTED": "unliftedFunc"} ) );
//map.getTimerManager().createTimer(1000, true, "timerFunc", entity, " test1 ", " test2 ");

//var movingPlatform = entities.createControllable( "moving platform", {"x": 150, "y": 100} );
//var movingPlatformCon = new PatrollingController(movingPlatform, true);
//movingPlatformCon.addTargetPoint(300, 100);
//movingPlatformCon.addTargetPoint(150, 100);
//movingPlatformCon.addTargetPoint(150, -100);
//movingPlatformCon.play();

// entities.create("box", {"x": 150, "y": -100} );
// entities.create("box", {"x": 150, "y": -100, "density":2} );