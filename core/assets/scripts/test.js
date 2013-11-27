importPackage(Packages.de.secondsystem.game01.model);

println("Ich bin ein Script für die map "+mapId);

for( var i=0; i<40; i++ ) {
	for( var j=0; j<2; j++ ) {
		entities.createControllable( "enemy", {"x": 450+i*80, "y": j*-200} );
	}
}