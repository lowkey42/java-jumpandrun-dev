package de.secondsystem.game01.impl.map;

import java.util.List;

import org.jsfml.system.Vector2f;

import de.secondsystem.game01.model.IUpdateable;

public interface ILayer extends IWorldDrawable, IUpdateable {

	void addNode(ILayerObject obj);

	void replaceNode(ILayerObject obj, ILayerObject nObj);

	void remove(ILayerObject s);

	boolean isVisible();

	boolean setVisible(boolean visible);

	List<ILayerObject> listAll();

	List<ILayerObject> findNodes(Vector2f point);
	
}