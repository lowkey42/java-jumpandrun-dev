package de.secondsystem.game01.impl.graphic;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.ConstView;
import org.jsfml.graphics.Drawable;
import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.PrimitiveType;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Vertex;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;

public abstract class RenderTargetWrapper implements RenderTarget {

	protected RenderTarget renderTarget;
	
	public RenderTargetWrapper(RenderTarget renderTarget) {
		this.renderTarget = renderTarget;
	}

	@Override
	public void clear(Color color) {
		renderTarget.clear(color);
	}
		
	@Override
	public void draw(Drawable drawable) {
		renderTarget.draw(drawable);
	}

	@Override
	public void draw(Drawable drawable, RenderStates renderStates) {
		renderTarget.draw(drawable, renderStates);
	}

	@Override
	public void draw(Vertex[] vertices, PrimitiveType type) {
		renderTarget.draw(vertices, type);
	}

	@Override
	public void draw(Vertex[] vertices, PrimitiveType type, RenderStates states) {
		renderTarget.draw(vertices, type, states);
	}

	@Override
	public ConstView getDefaultView() {
		return renderTarget.getDefaultView();
	}

	@Override
	public Vector2i getSize() {
		return renderTarget.getSize();
	}

	@Override
	public ConstView getView() {
		return renderTarget.getView();
	}

	@Override
	public IntRect getViewport(ConstView view) {
		return renderTarget.getViewport(view);
	}

	@Override
	public Vector2i mapCoordsToPixel(Vector2f point) {
		return renderTarget.mapCoordsToPixel(point);
	}

	@Override
	public Vector2i mapCoordsToPixel(Vector2f point, ConstView view) {
		return renderTarget.mapCoordsToPixel(point, view);
	}

	@Override
	public Vector2f mapPixelToCoords(Vector2i point) {
		return renderTarget.mapPixelToCoords(point);
	}

	@Override
	public Vector2f mapPixelToCoords(Vector2i point, ConstView view) {
		return renderTarget.mapPixelToCoords(point, view);
	}

	@Override
	public void popGLStates() {
		renderTarget.popGLStates();
	}

	@Override
	public void pushGLStates() {
		renderTarget.pushGLStates();
	}

	@Override
	public void resetGLStates() {
		renderTarget.resetGLStates();
	}

	@Override
	public void setView(ConstView view) {
		renderTarget.setView(view);
	}

	public void setTarget(RenderTarget renderTarget) {
		this.renderTarget = renderTarget;
	}
}
