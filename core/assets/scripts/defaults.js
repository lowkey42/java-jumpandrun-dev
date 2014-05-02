function include(fn) {
	System.load(fn);
}

var collectedIcons = {};
var iconOffset = 0;
function createCollectable(id, iconName, x, y) {
	
	collectedIcons[id] = API.createSprite(iconName+".png", 10 + 100*iconOffset,10);
	collectedIcons[id].setScale(80.0/collectedIcons[id].getTexture().getSize().x, 80.0/collectedIcons[id].getTexture().getSize().x);
	iconOffset++;
	API.updateSpriteColor(collectedIcons[id], 255, 255, 255, 200);
	if( !API.exists("_collected_"+id) ) {
		API.updateSpriteTex(collectedIcons[id], iconName+"_disabled.png");
	API.updateSpriteColor(collectedIcons[id], 255, 255, 255, 100);
		entities.create("collectable_page", {"representation":iconName+".png", "x":x, "y":y, "onTOUCHED":{
			"factory":".CondEHF", "if":"IS_PLAYER", "arg":1, "then":{"factory":".BlockEHF", "subs":[
				{"factory":".ScriptEHF", "body":"API.store('_collected_"+id+"', true); API.updateSpriteTex(collectedIcons['"+id+"'], '"+iconName+".png');API.updateSpriteColor(collectedIcons['"+id+"'], 255, 255, 255, 200)" },
				{"factory":".DeleteEHF"}
			]}
		}});
	}
}

