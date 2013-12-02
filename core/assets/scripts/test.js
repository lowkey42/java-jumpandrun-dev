importPackage(Packages.de.secondsystem.game01.model);
importPackage(Packages.de.secondsystem.game01.impl.map.physics);
importPackage(Packages.de.secondsystem.game01.impl.game.entities); 
importPackage(Packages.de.secondsystem.game01.impl.timer)
println("Ich bin ein Script für die map "+mapId);

function touchFunc(entity, other) {
	entity.jump();
}

for( var i=0; i<40; i++ ) {
	for( var j=0; j<2; j++ ) {
		entities.createControllable( "enemy", {"x": 450+i*80, "y": j*-200 } );
	}
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

var entity = entities.createControllable( "enemy", {"x": 150, "y": 200, "events": {"TOUCHED": "touchFunc"} } );
map.getTimerManager().createTimer(1000, true, "timerFunc", entity, " test1 ", " test2 ");
