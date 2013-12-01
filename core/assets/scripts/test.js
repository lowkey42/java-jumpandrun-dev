importPackage(Packages.de.secondsystem.game01.model);
importPackage(Packages.de.secondsystem.game01.impl.map.physics);
importPackage(Packages.de.secondsystem.game01.impl.game.entities); 
importPackage(Packages.de.secondsystem.game01.impl.timer)
println("Ich bin ein Script für die map "+mapId);

function touchFunc(entity, other) {
	entity.jump();
}


    
function timerFunc(timer, entity, args) {  
	entity.jump();
	println("Works like a charm. " + timer.getTickCount() + args[0] + args[1]); // is there a better solution than args[0], args[1]... ?
	
	// disable the timer after 5 onTick() calls
	if( timer.getTickCount() == 5 ) {
		timer.setEnabled(false);
		println("The timer is now disabled.");
	}
}

function anotherTimerFunc(timer) {
	
}

var entity = entities.createControllable( "enemy", {"x": 150, "y": 200, "events": {"TOUCHED": "touchFunc"} } );
map.getTimerManager().createTimer(1000, true, "timerFunc", entity, " test1 ", " test2 ");
