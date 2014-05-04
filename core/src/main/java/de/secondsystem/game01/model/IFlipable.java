package de.secondsystem.game01.model;

public interface IFlipable {

	void flipHoriz();

	void setFlipHoriz(boolean flip);

	boolean isFlippedHoriz();

	void flipVert();

	void setFlipVert(boolean flip);

	boolean isFlippedVert();

}