package de.secondsystem.game01.impl.editor.objects;

import org.jsfml.graphics.Color;

import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.impl.map.objects.EntityLayerObject;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.Attributes.Attribute;

public class SelectedEditorEntity extends SelectedEditorObject {

	public SelectedEditorEntity(Color outlineColor, float outlineThickness, Color fillColor, IGameMap map) {
		super(outlineColor, outlineThickness, fillColor, map);
	}
	
	private void recreate() {
		Attribute width = new Attribute("width", this.width*zoom);
		Attribute height = new Attribute("height", this.height*zoom);
		Attribute rotation = new Attribute("rotation", this.rotation);
		Attribute x = new Attribute("x", pos.x);
		Attribute y = new Attribute("y", pos.y);
		Attribute worldId = new Attribute("worldId", map.getActiveWorldId().id);
		
		if( layerObject instanceof EntityLayerObject ) {
			IGameEntity entity = ((EntityLayerObject) layerObject).getEntity();
			map.getEntityManager().create(entity.uuid(), entity.getEditableState().getArchetype(), new Attributes(width, height, rotation, x, y, worldId));
		}
		else
			throw new RuntimeException("layerObject is not instance of EntityLayerObject");
	}
	
	@Override
	public void deselect() {
		if( layerObject != null )
			recreate();
	}

}
