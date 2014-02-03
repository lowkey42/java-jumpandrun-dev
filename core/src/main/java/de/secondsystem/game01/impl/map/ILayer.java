package de.secondsystem.game01.impl.map;

import org.jsfml.system.Vector2f;

import de.secondsystem.game01.model.IDrawable;
import de.secondsystem.game01.model.ISerializable;
import de.secondsystem.game01.model.IUpdateable;

public interface ILayer extends ISerializable, IDrawable, IUpdateable {

	void addNode(ILayerObject obj);

	ILayerObject findNode(Vector2f point);

	void remove(ILayerObject s);

	boolean isVisible();

	boolean setVisible(boolean visible);

}