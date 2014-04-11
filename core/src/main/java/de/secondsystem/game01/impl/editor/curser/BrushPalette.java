package de.secondsystem.game01.impl.editor.curser;

import org.jsfml.graphics.Color;

import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.impl.map.ILayerObject;
import de.secondsystem.game01.impl.map.LayerType;
import de.secondsystem.game01.impl.map.Tileset;
import de.secondsystem.game01.impl.map.objects.ParticleEmitterLayerObject;
import de.secondsystem.game01.impl.map.objects.SpriteLayerObject;
import de.secondsystem.game01.model.Attributes;

final class BrushPalette {

	public static interface IBrush {
		ILayerObject cirlce(boolean up);
		
		void onDestroy();
		
		void setAttributes(Attributes attributes);
		
		ILayerObject getObject();
	}
	
	public IBrush getBrush( IGameMap map, LayerType layer ) {
		return new GraphicLayerBrush(map, layer);
	}

	private static final class GraphicLayerBrush implements IBrush {

		private Tileset tileset;
		private IGameMap map;
		private ILayerObject obj;
		
		public GraphicLayerBrush(IGameMap map, LayerType layer) {
			this.map = map;
			this.tileset = map.getTileset();
			this.obj = new SpriteLayerObject(tileset, map.getActiveWorldId().id, 0, 0, 0, 0);
			map.addNode(layer, obj);
		}
		
		@Override
		public ILayerObject cirlce(boolean up) {
			int tileIndex = -1;
			
			if( obj instanceof SpriteLayerObject )
				tileIndex = ((SpriteLayerObject)obj).getTile();
			
			if( up )
				tileIndex++;
			else
				tileIndex--;
			
			if( tileIndex<-1 )
				tileIndex = tileset.size()-1;
			else if( tileIndex>=tileset.size() )
				tileIndex=-1;
			
			if( tileIndex>=0 ) {
				if( obj instanceof SpriteLayerObject )
					((SpriteLayerObject)obj).setTile(tileset, tileIndex);
				
				else
					obj = update(new SpriteLayerObject(tileset, map.getActiveWorldId().id, tileIndex, obj.getPosition().x, obj.getPosition().y, obj.getRotation()));
				
			} else {
				obj = update(new ParticleEmitterLayerObject("", 100, map.getActiveWorldId().id, obj.getPosition().x, obj.getPosition().y, 100, 100, null, 1000, 1000, 
						0, 0, 0, 0, 0, 0, 0, 0, Color.WHITE, Color.WHITE, 5, 5));
			}
			
			return obj;
		}
		
		@Override
		public void onDestroy() {
			map.remove(obj.getLayerType(), obj);
		}
		
		@Override
		public void setAttributes(Attributes attributes) {
			map.updateNode(obj, attributes);
		}
		
		private ILayerObject update(ILayerObject nObj) {
			map.replaceNode(obj, nObj);
			obj = nObj;
			return obj;
		}

		@Override
		public ILayerObject getObject() {
			return obj;
		}
		
	}
	
}
