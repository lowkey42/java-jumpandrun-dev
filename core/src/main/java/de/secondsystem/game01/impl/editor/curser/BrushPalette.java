package de.secondsystem.game01.impl.editor.curser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsfml.graphics.Color;

import de.secondsystem.game01.impl.ResourceManager;
import de.secondsystem.game01.impl.gui.ThumbnailButton;
import de.secondsystem.game01.impl.gui.ThumbnailButton.ThumbnailData;
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
import de.secondsystem.game01.model.GameException;

final class BrushPalette {

	public static interface IBrush {
		ILayerObject cirlce(boolean up);
		
		void onDestroy();
		
		void setAttributes(Attributes attributes);
		
		ILayerObject getObject();
		
		List<ThumbnailButton.ThumbnailData> generateThumbnails();

		ILayerObject set(int index);
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
			return set( circleIndex(obj instanceof SpriteLayerObject ? ((SpriteLayerObject)obj).getTile() : map.getTileset().size(), up, 0, map.getTileset().size()) );
		}
		
		@Override
		public ILayerObject set(int index) {
			return index<map.getTileset().size() ?
					update(new SpriteLayerObject(map.getTileset(), map.getActiveWorldId().id, index, obj.getPosition().x, obj.getPosition().y, obj.getRotation()))
				: //else
					update(new ParticleEmitterLayerObject("", 100, map.getActiveWorldId().id, obj.getPosition().x, obj.getPosition().y, 100, 100, null, 1000, 1000, 
							0, 0, 0, 0, 0, 0, 0, 0, Color.WHITE, Color.WHITE, 5, 5));
		}

		@Override
		public List<ThumbnailData> generateThumbnails() {
			List<ThumbnailData> td = new ArrayList<>(map.getTileset().size()+1);
			for(int i=0; i<map.getTileset().size(); ++i ) {
				td.add(new ThumbnailData(map.getTileset().getName(i), map.getTileset().get(i), map.getTileset().getClip(i)));
			}
			
			try {
				td.add(new ThumbnailData("Particles", ResourceManager.texture_gui.get("icon_particles.png")));
			} catch (IOException e) {
				throw new GameException(e);
			}
			
			return td;
		}
	}
	
	private static final class PhysicsLayerBrush extends AbstractBrush<CollisionObject> {
		
		public PhysicsLayerBrush(IGameMap map, LayerType layer) {
			super(map, layer, new CollisionObject(map, map.getActiveWorldId().id, CollisionObject.CollisionType.NORMAL, 0, 0, 200, 200, 0));
		}
		
		@Override
		public ILayerObject cirlce(boolean up) {
			return set( circleIndex(obj.getType().ordinal(), up, 0, CollisionObject.CollisionType.values().length-1) );
		}
		
		@Override
		public ILayerObject set(int index) {
			obj.setType(CollisionObject.CollisionType.values()[index]);
			return obj;
		}

		@Override
		public List<ThumbnailData> generateThumbnails() {
			List<ThumbnailData> td = new ArrayList<>(3);
			
			try {
				td.add(new ThumbnailData("Normal", ResourceManager.texture_gui.get("icon_grav_full_color.png")));
				td.add(new ThumbnailData("Half", ResourceManager.texture_gui.get("icon_grav_half_color.png")));
				td.add(new ThumbnailData("Climb", ResourceManager.texture_gui.get("icon_grav_climb_color.png")));
				
			} catch (IOException e) {
				throw new GameException(e);
			}
			
			return td;
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
			return set( circleIndex((int)(obj.getDegree()/DEGREE_STEP_SIZE), up, 1, (int)DEGREE_STEPS)-1 );
		}

		@Override
		public ILayerObject set(int index) {
			obj.setDegree((index+1)*DEGREE_STEP_SIZE);
			return obj;
		}
		
		@Override
		public List<ThumbnailData> generateThumbnails() {
			List<ThumbnailData> td = new ArrayList<>(DEGREE_STEPS);
			try {
				td.add(new ThumbnailData("12%",  ResourceManager.texture_gui.get("icon_light_012.png")));
				td.add(new ThumbnailData("25%",  ResourceManager.texture_gui.get("icon_light_025.png")));
				td.add(new ThumbnailData("37%",  ResourceManager.texture_gui.get("icon_light_037.png")));
				td.add(new ThumbnailData("50%",  ResourceManager.texture_gui.get("icon_light_050.png")));
				td.add(new ThumbnailData("62%",  ResourceManager.texture_gui.get("icon_light_062.png")));
				td.add(new ThumbnailData("75%",  ResourceManager.texture_gui.get("icon_light_075.png")));
				td.add(new ThumbnailData("87%",  ResourceManager.texture_gui.get("icon_light_087.png")));
				td.add(new ThumbnailData("100%", ResourceManager.texture_gui.get("icon_light_100.png")));
				
			} catch (IOException e) {
				throw new GameException(e);
			}
			
			return td;
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
			return set( circleIndex(index, up, 0, map.getEntityManager().listArchetypes().size()-1) );
		}
		
		@Override
		public ILayerObject set(int index) {
			this.index = index;
			
			EntityLayerObject e = new EntityLayerObject(map, map.getEntityManager().listArchetypes().get(index), 
					new Attributes(new Attribute("x", obj.getPosition().x), new Attribute("y", obj.getPosition().y))
			);
			update( e );
			return obj;
		}

		@Override
		public List<ThumbnailData> generateThumbnails() {
			return map.getEntityManager().generateThumbnails();
		}
	}
	
}
