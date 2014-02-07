package de.secondsystem.game01.impl.map;

import java.util.List;

import org.jsfml.system.Vector2f;

import de.secondsystem.game01.model.IDrawable;
import de.secondsystem.game01.model.IUpdateable;

public interface ILayer extends IDrawable, IUpdateable {

	void addNode(ILayerObject obj);

	ILayerObject findNode(Vector2f point);

	void remove(ILayerObject s);

	boolean isVisible();

	boolean setVisible(boolean visible);

	List<ILayerObject> listAll();
	
}