
function init() {
	println("Ich bin ein Script für die map "+mapId);
}

createCollectable("page_01", "page_chaos", 1600, 250);
createCollectable("page_02", "page_chaos", 600, 250);

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


entities.create("box", {"x": 300, "y": 0} );

entities.create("light", {"x": 150, "y": -100, "worldId":3} );

for( var i=0; i<10; i++ ) {
	entities.create("light", {"x": 500+i*200, "y": 200, "worldId":1} );
	entities.create("light", {"x": i*100, "y": 100, "worldId":2} );
}

timer.createTimer(1000, false, function(){
	API.playMonologue("test-mon");
});

	