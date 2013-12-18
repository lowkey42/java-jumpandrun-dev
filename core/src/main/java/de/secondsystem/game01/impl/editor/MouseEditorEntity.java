package de.secondsystem.game01.impl.editor;

import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.Attributes.Attribute;
import de.secondsystem.game01.model.IDimensioned;

public class MouseEditorEntity extends EditorEntity {
	
	public void createEntity(IGameMap map, String type) {
		setEntity( map.getEntityManager().create(type, new Attributes(new Attribute("x",0), new Attribute("y",0))) );
		map.getEntityManager().destroy(entity.uuid());
		
		rotation = 0.f;
		zoom = 1.f;
		width  = ((IDimensioned) entityRepresentation).getWidth();
		height = ((IDimensioned) entityRepresentation).getHeight();		
	}
}
