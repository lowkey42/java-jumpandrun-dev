importPackage(Packages.de.secondsystem.game01.model);
importPackage(Packages.de.secondsystem.game01.impl.map.physics);
importPackage(Packages.de.secondsystem.game01.impl.game.entities);

println("Ich bin ein Script für die map "+mapId);

for( var i=0; i<40; i++ ) {
	for( var j=0; j<2; j++ ) {
		entities.createControllable( "enemy", {"x": 450+i*80, "y": j*-200} );
	}
}

function testFunc(owner, timer) {
	//owner.moveVertically(VDirection.UP); // doesn't work TODO: Find out how to use enum in JavaScript
	//owner.jump; // works
	//owner.switchWorlds(); // works
	println("Works like a charm. " + timer.getTickCount());
	//println(timer.getTickCount()); // doesn't work // TODO: Find out why...
}

var entity = entities.createControllable( "enemy", {"x": 150, "y": 200, "events": {"TIMER_TICK": "testFunc"} } );
entity.setTimerInterval(500);
println("So rufe ich z.B. die worldId von der entity auf " + entity.getWorldId());