importPackage(Packages.de.secondsystem.game01.model);
importPackage(Packages.de.secondsystem.game01.impl.map.physics);
importPackage(Packages.de.secondsystem.game01.impl.game.entities); 

println("Ich bin ein Script für die map "+mapId);

function touchFunc(owner, other) {
	owner.jump();
	println("PARTY HARD !!!");
}

for( var i=0; i<40; i++ ) {
	for( var j=0; j<2; j++ ) {
		entities.createControllable( "enemy", {"x": 450+i*80, "y": j*-200, "events": {"TOUCHED": "touchFunc"} } );
	}
}
    
function timerFunc(owner, timer) {  
	owner.moveVertically(IControllable.VDirection.UP);
	owner.jump();
	println("Works like a charm. " + timer[0].getTickCount());
}


var entity = entities.createControllable( "enemy", {"x": 150, "y": 200, "events": {"TIMER_TICK": "timerFunc", "TOUCHED": "touchFunc"} } );
entity.setInterval(5000);
println("So rufe ich z.B. die worldId von der entity auf " + entity.getWorldId());