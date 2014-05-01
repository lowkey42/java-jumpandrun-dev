package de.secondsystem.game01.model.collections;

import java.util.HashSet;
import java.util.Set;

import org.jsfml.graphics.FloatRect;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.model.IDimensioned;
import de.secondsystem.game01.model.IMoveable;
import de.secondsystem.game01.model.collections.AutoJanusList.EntryFactory;

public class SpatialGrid<T extends IMoveable> implements ISpatialIndex<T> {
	
	private final EntryFactory<AutoJanusList<Set<T>>> OUTTER_FACTORY = new EntryFactory<AutoJanusList<Set<T>>>() {
		@Override public AutoJanusList<Set<T>> create(int index) {
			return new AutoJanusList<>(INNER_FACTORY, estimatedMinYSize, estimatedMaxYSize);
		}
	};

	private final EntryFactory<Set<T>> INNER_FACTORY = new EntryFactory<Set<T>>() {
		@Override public Set<T> create(int index) {
			return new HashSet<>();
		}
	};
			
	private final AutoJanusList<AutoJanusList<Set<T>>> roots;
	private final float topLevelSize;
	private final int estimatedMinYSize;
	private final int estimatedMaxYSize;
	private float maxObjectSize = 10;
	
	public SpatialGrid(Vector2f estimatedMinSize, Vector2f estimatedMaxSize) {
		this(1000, estimatedMinSize, estimatedMaxSize);
	}
	
	public SpatialGrid(float topLevelSize, Vector2f estimatedMinSize, Vector2f estimatedMaxSize) {
		this.topLevelSize = topLevelSize;
		this.estimatedMinYSize = posToIndex(estimatedMinSize.y);
		this.estimatedMaxYSize = posToIndex(estimatedMaxSize.y);
		this.roots = new AutoJanusList<AutoJanusList<Set<T>>>(OUTTER_FACTORY, posToIndex(estimatedMinSize.x), posToIndex(estimatedMaxSize.x));
	}

	private int posToIndex(float p) {
		return (int) (p/topLevelSize); 
	}
	private Set<T> getRoot( float x, float y, boolean create ) {
		return getRootByIndex(posToIndex(x), posToIndex(y), create);
	}
	
	private Set<T> getRootByIndex( int x, int y, boolean create ) {
		AutoJanusList<Set<T>> xl = roots.get(x, create);
		return xl!=null ? xl.get(y, create) : null;
	}
	
	public void updateMaxObjectSize(IDimensioned obj) {
		maxObjectSize = Math.max(maxObjectSize, (float) Math.sqrt( 
				( ((IDimensioned) obj).getHeight()*((IDimensioned) obj).getHeight()
				+ ((IDimensioned) obj).getWidth() *((IDimensioned) obj).getWidth() )/2) );
	}
	
	/* (non-Javadoc)
	 * @see de.secondsystem.game01.model.SpatialIndex#add(T)
	 */
	@Override
	public void add(T obj) {
		if( obj instanceof IDimensioned )
			updateMaxObjectSize((IDimensioned) obj);
		
		if( obj instanceof IndexedMoveable )
			((IndexedMoveable) obj).setIndex(this);
		
		getRoot(obj.getPosition().x, obj.getPosition().y, true).add(obj);
	}
	/* (non-Javadoc)
	 * @see de.secondsystem.game01.model.SpatialIndex#remove(T)
	 */
	@Override
	public void remove(T obj) {
		if( obj instanceof IndexedMoveable )
			((IndexedMoveable) obj).setIndex(null);
		
		getRoot(obj.getPosition().x, obj.getPosition().y, true).remove(obj);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.secondsystem.game01.model.SpatialIndex#update(de.secondsystem.game01.model.IMoveable, org.jsfml.system.Vector2f)
	 */
	@Override
	public void update(T obj, Vector2f lastPosition) {
		final int lxi = posToIndex(lastPosition.x);
		final int lyi = posToIndex(lastPosition.y);
		final int nxi = posToIndex(obj.getPosition().x);
		final int nyi = posToIndex(obj.getPosition().y);
		
		if( lxi!=nxi || lyi!=nyi ) {
			getRootByIndex(lxi, lyi, true).remove(obj);
			getRootByIndex(nxi, nyi, true).add(obj);
		}
	}
	
	/* (non-Javadoc)
	 * @see de.secondsystem.game01.model.SpatialIndex#query(org.jsfml.graphics.FloatRect, de.secondsystem.game01.model.QuadTree.EntryWalker)
	 */
	@Override
	public void query(FloatRect area, EntryWalker<T> walker) {
		int numFound = 0;
		for(int x=posToIndex(area.left-maxObjectSize); x<=posToIndex(area.left+area.width+maxObjectSize); ++x )
			for(int y=posToIndex(area.top-maxObjectSize); y<=posToIndex(area.top+area.height+maxObjectSize); ++y ) {
				Set<T> entries = getRootByIndex(x, y, false);
				if( entries!=null )
					for( T e : entries ) {
						walker.walk(e);
						numFound++;
					}
			}
		
		walker.finished(numFound);
	}
	
	/* (non-Javadoc)
	 * @see de.secondsystem.game01.model.SpatialIndex#query(org.jsfml.system.Vector2f, float, de.secondsystem.game01.model.QuadTree.EntryWalker)
	 */
	@Override
	public void query(Vector2f point, float range, EntryWalker<T> walker) {
		int numFound = 0;
		for(int x=posToIndex(point.x-maxObjectSize-range); x<=posToIndex(point.x+maxObjectSize+range); ++x )
			for(int y=posToIndex(point.y-maxObjectSize-range); y<=posToIndex(point.y+maxObjectSize+range); ++y ) {
				Set<T> entries = getRootByIndex(x, y, false);
				if( entries!=null )
					for( T e : entries ) {
						walker.walk(e);
						numFound++;
					}
			}
		
		walker.finished(numFound);
	}

}
