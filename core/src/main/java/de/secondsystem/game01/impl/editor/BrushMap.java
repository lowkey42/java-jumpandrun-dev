package de.secondsystem.game01.impl.editor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jsfml.graphics.Color;

import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.impl.map.ILayerObject;
import de.secondsystem.game01.impl.map.LayerType;
import de.secondsystem.game01.impl.map.objects.CollisionObject;
import de.secondsystem.game01.impl.map.objects.LayerObjectType;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.Attributes.Attribute;
import de.secondsystem.game01.util.SerializationUtil;

final class BrushMap extends HashMap<LayerType, BrushMap.Brush> {

	public BrushMap() {
		super(DEFAULT_BRUSHES);
	}
	
	public static final class Brush {
		public LayerObjectType type;
		public Attributes data;
		public float originalWidth;
		public float originalHeight;
		public float zoom = 1.0f;
		
		public Brush() {}
		public Brush( LayerObjectType type, Attributes data ) {
			this.type = type;
			this.data = data;
		}
		public ILayerObject create(IGameMap map) {
			ILayerObject obj = map.createNode(type, data);
			if( originalHeight<=0 || originalWidth<=0 ) {
				originalWidth = obj.getWidth();
				originalHeight = obj.getHeight();
			}
			
			return obj;
		}
	}
	
	private static final Map<LayerType, Brush> DEFAULT_BRUSHES;
	
	static {
		Map<LayerType, Brush> db = new HashMap<>();
		
		for( LayerType layer : LayerType.values() ) {
			final Brush brush;
			
			switch (layer) {
				case OBJECTS:
					brush = new Brush(LayerObjectType.ENTITY, new Attributes(
							new Attribute("archetype", "box"),
							new Attribute("x", 0),
							new Attribute("y", 0)
					) );
					break;
					
				case PHYSICS:
					brush = new Brush(LayerObjectType.LIGHT, new Attributes(
							new Attribute("$type", LayerObjectType.LIGHT.shortId),
							new Attribute("world", 0),
							new Attribute("type", CollisionObject.CollisionType.NORMAL.name()),
							new Attribute("x", 0),
							new Attribute("y", 0),
							new Attribute("rotation", 0),
							new Attribute("width", 200),
							new Attribute("height", 200)
					) );
					break;
					
				case LIGHTS:
					brush = new Brush(LayerObjectType.LIGHT, new Attributes(
							new Attribute("$type", LayerObjectType.LIGHT.shortId),
							new Attribute("world", 0),
							new Attribute("color", SerializationUtil.encodeColor(Color.WHITE)),
							new Attribute("x", 0),
							new Attribute("y", 0),
							new Attribute("rotation", 0),
							new Attribute("radius", 200),
							new Attribute("sizeDegree", 360)
					) );
					break;
	
				default:
					brush = new Brush(LayerObjectType.SPRITE, new Attributes(
							new Attribute("$type", LayerObjectType.SPRITE.shortId),
							new Attribute("world", 0),
							new Attribute("tile", 0),
							new Attribute("x", 0),
							new Attribute("y", 0),
							new Attribute("rotation", 0),
							new Attribute("width", -1),
							new Attribute("height", -1)
					) );
					break;
			}
			
			db.put(layer, brush);
		}
		
		DEFAULT_BRUSHES = Collections.unmodifiableMap(db);
	}
	
}
