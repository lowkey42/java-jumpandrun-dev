package de.secondsystem.game01.impl.editor.curser;

import java.util.ArrayList;
import java.util.Arrays;
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

final class BrushPalette {

	public static interface IBrush {
		ILayerObject cirlce(boolean up);
		
		void onDestroy();
		
		void setAttributes(Attributes attributes);
		
		ILayerObject getObject();
		
		List<ThumbnailButton.ThumbnailData> generateThumbnails();

		ILayerObject set(int index);
		int getIndex();
		int getMax();
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
		
		protected int circleIndex( boolean up ) {
			int cIndex = getIndex();
			
			if( up )
				cIndex++;
			else
				cIndex--;
			
			if( cIndex<0 )
				return getMax();
			else if( cIndex>getMax() )
				return 0;
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
		public int getIndex() {
			return obj instanceof SpriteLayerObject ? ((SpriteLayerObject)obj).getTile() : map.getTileset().size();
		}
		@Override
		public int getMax() {
			return map.getTileset().size();
		}
		
		@Override
		public ILayerObject cirlce(boolean up) {
			return set( circleIndex(up) );
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
			
			td.add(new ThumbnailData("Particles", ResourceManager.texture_gui.get("icon_particles.png")));
			
			return td;
		}
	}
	
	private static final class PhysicsLayerBrush extends AbstractBrush<CollisionObject> {
		
		public PhysicsLayerBrush(IGameMap map, LayerType layer) {
			super(map, layer, new CollisionObject(map, map.getActiveWorldId().id, CollisionObject.CollisionType.NORMAL, 0, 0, 200, 200, 0));
		}
		
		@Override
		public int getIndex() {
			return obj.getType().ordinal();
		}
		@Override
		public int getMax() {
			return CollisionObject.CollisionType.values().length-1;
		}
		
		@Override
		public ILayerObject cirlce(boolean up) {
			return set( circleIndex(up) );
		}
		
		@Override
		public ILayerObject set(int index) {
			obj.setType(CollisionObject.CollisionType.values()[index]);
			return obj;
		}

		@Override
		public List<ThumbnailData> generateThumbnails() {
			return Arrays.asList(
				new ThumbnailData("Normal", ResourceManager.texture_gui.get("icon_grav_full_color.png")),
				new ThumbnailData("Half", ResourceManager.texture_gui.get("icon_grav_half_color.png")),
				new ThumbnailData("Climb", ResourceManager.texture_gui.get("icon_grav_climb_color.png"))
			);
		}
	}
	
	private static final class LightLayerBrush extends AbstractBrush<LightLayerObject> {

		private static final int DEGREE_STEPS = 8;
		private static final float DEGREE_STEP_SIZE = 360.f/DEGREE_STEPS;
		
		public LightLayerBrush(IGameMap map, LayerType layer) {
			super(map, layer, new LightLayerObject(map.getLightMap(), map.getActiveWorldId().id, 0, 0, 0, 200, 360, Color.WHITE));
		}
		
		@Override
		public int getIndex() {
			return (int) (obj.getDegree()/DEGREE_STEP_SIZE);
		}
		@Override
		public int getMax() {
			return DEGREE_STEPS-1;
		}
		
		@Override
		public ILayerObject cirlce(boolean up) {
			return set( circleIndex(up) );
		}

		@Override
		public ILayerObject set(int index) {
			obj.setDegree((index+1)*DEGREE_STEP_SIZE);
			return obj;
		}
		
		@Override
		public List<ThumbnailData> generateThumbnails() {
			return Arrays.asList(
				new ThumbnailData("12%",  ResourceManager.texture_gui.get("icon_light_012.png")),
				new ThumbnailData("25%",  ResourceManager.texture_gui.get("icon_light_025.png")),
				new ThumbnailData("37%",  ResourceManager.texture_gui.get("icon_light_037.png")),
				new ThumbnailData("50%",  ResourceManager.texture_gui.get("icon_light_050.png")),
				new ThumbnailData("62%",  ResourceManager.texture_gui.get("icon_light_062.png")),
				new ThumbnailData("75%",  ResourceManager.texture_gui.get("icon_light_075.png")),
				new ThumbnailData("87%",  ResourceManager.texture_gui.get("icon_light_087.png")),
				new ThumbnailData("100%", ResourceManager.texture_gui.get("icon_light_100.png"))
			);
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
		public int getIndex() {
			return index;
		}
		@Override
		public int getMax() {
			return map.getEntityManager().listArchetypes().size()-1;
		}
		
		@Override
		public ILayerObject cirlce(boolean up) {
			return set( circleIndex(up) );
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
