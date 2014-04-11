package de.secondsystem.game01.impl.editor.curser;

import org.jsfml.graphics.Color;

import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.impl.map.ILayerObject;
import de.secondsystem.game01.impl.map.LayerType;
import de.secondsystem.game01.impl.map.objects.CollisionObject;
import de.secondsystem.game01.impl.map.objects.EntityLayerObject;
import de.secondsystem.game01.impl.map.objects.LightLayerObject;
import de.secondsystem.game01.impl.map.objects.ParticleEmitterLayerObject;
import de.secondsystem.game01.impl.map.objects.SpriteLayerObject;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.Attributes.Attribute;

final class BrushPalette {

	public static interface IBrush {
		ILayerObject cirlce(boolean up);
		
		void onDestroy();
		
		void setAttributes(Attributes attributes);
		
		ILayerObject getObject();
	}
	
	public IBrush getBrush( IGameMap map, LayerType layer ) {
		switch(layer) {
			case LIGHTS:
				return new LightLayerBrush(map, layer);
				
			case OBJECTS:
				return new EntityLayerBrush(map, layer);
				
			case PHYSICS:
				return new PhysicsLayerBrush(map, layer);
				

			case BACKGROUND_0:
			case BACKGROUND_1:
			case BACKGROUND_2:
			case FOREGROUND_0:
			case FOREGROUND_1:
			default:
				return new GraphicLayerBrush(map, layer);
		}
	}

	private static abstract class AbstractBrush<T extends ILayerObject> implements IBrush {
		protected IGameMap map;
		protected T obj;
		
		public AbstractBrush(IGameMap map, LayerType layer, T obj) {
			this.map = map;
			this.obj = obj;
			map.addNode(layer, obj);
		}
		
		protected int circleIndex( int cIndex, boolean up, int minIndex, int maxIndex ) {
			if( up )
				cIndex++;
			else
				cIndex--;
			
			if( cIndex<minIndex )
				return maxIndex;
			else if( cIndex>maxIndex )
				return minIndex;
			else
				return cIndex;
		}
		
		@Override
		public void onDestroy() {
			map.remove(obj.getLayerType(), obj);
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public void setAttributes(Attributes attributes) {
			obj = (T) map.updateNode(obj, attributes);
		}
		
		@SuppressWarnings("unchecked")
		protected ILayerObject update(ILayerObject nObj) {
			map.replaceNode(obj, nObj);
			obj = (T) nObj;
			return obj;
		}

		@Override
		public ILayerObject getObject() {
			return obj;
		}
		
	}
	
	private static final class GraphicLayerBrush extends AbstractBrush<ILayerObject> {
		
		public GraphicLayerBrush(IGameMap map, LayerType layer) {
			super(map, layer, new SpriteLayerObject(map.getTileset(), map.getActiveWorldId().id, 0, 0, 0, 0));
		}
		
		@Override
		public ILayerObject cirlce(boolean up) {
			final int tileIndex = circleIndex(obj instanceof SpriteLayerObject ? ((SpriteLayerObject)obj).getTile() : -1, up, -1, map.getTileset().size()-1);
			
			return tileIndex>=0 ?
					update(new SpriteLayerObject(map.getTileset(), map.getActiveWorldId().id, tileIndex, obj.getPosition().x, obj.getPosition().y, obj.getRotation()))
				: //else
					update(new ParticleEmitterLayerObject("", 100, map.getActiveWorldId().id, obj.getPosition().x, obj.getPosition().y, 100, 100, null, 1000, 1000, 
							0, 0, 0, 0, 0, 0, 0, 0, Color.WHITE, Color.WHITE, 5, 5));
		}
	}
	
	private static final class PhysicsLayerBrush extends AbstractBrush<CollisionObject> {
		
		public PhysicsLayerBrush(IGameMap map, LayerType layer) {
			super(map, layer, new CollisionObject(map, map.getActiveWorldId().id, CollisionObject.CollisionType.NORMAL, 0, 0, 200, 200, 0));
		}
		
		@Override
		public ILayerObject cirlce(boolean up) {
			obj.setType(CollisionObject.CollisionType.values()[circleIndex(obj.getType().ordinal(), up, 0, CollisionObject.CollisionType.values().length-1)]);
			return obj;
		}
	}
	
	private static final class LightLayerBrush extends AbstractBrush<LightLayerObject> {

		private static final int DEGREE_STEPS = 8;
		private static final float DEGREE_STEP_SIZE = 360.f/DEGREE_STEPS;
		
		public LightLayerBrush(IGameMap map, LayerType layer) {
			super(map, layer, new LightLayerObject(map.getLightMap(), map.getActiveWorldId().id, 0, 0, 0, 200, 360, Color.WHITE));
		}
		
		@Override
		public ILayerObject cirlce(boolean up) {
			obj.setDegree(circleIndex((int)(obj.getDegree()/DEGREE_STEP_SIZE), up, 0, (int)DEGREE_STEPS)*DEGREE_STEP_SIZE);
			return obj;
		}
	}
	
	private static final class EntityLayerBrush extends AbstractBrush<EntityLayerObject> {
		
		private int index = 0;
		
		public EntityLayerBrush(IGameMap map, LayerType layer) {
			super(map, layer, new EntityLayerObject(map, map.getEntityManager().listArchetypes().get(0), 
					new Attributes(new Attribute("x", 0), new Attribute("y", 0))
			));
		}
		
		@Override
		public ILayerObject cirlce(boolean up) {
			index = circleIndex(index, up, 0, map.getEntityManager().listArchetypes().size()-1);
			
			EntityLayerObject e = new EntityLayerObject(map, map.getEntityManager().listArchetypes().get(index), 
					new Attributes(new Attribute("x", obj.getPosition().x), new Attribute("y", obj.getPosition().y))
			);
			update( e );
			return obj;
		}
	}
	
}
