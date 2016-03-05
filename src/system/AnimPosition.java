package system;

import java.awt.Color;

/**
 * An inner class that encapsulates location information for animation
 */

public class AnimPosition {

	private double x, y; // current position
	private double dx, dy; // amount to move each frame (double precision)
	private int framesLeft; // frames left in animation
	private Color color; // color of thing we're animating (if relevant)
	public boolean buff = false;

	/**
	 * Creates a new AnimPosition at the given coordinates
	 *
	 * @param x
	 * @param y
	 */
	public AnimPosition(int x, int y) {
		this.setX(x);
		this.setY(y);
	}

	/**
	 * Moves (translates) the animation position by a single frame
	 */
	public void step() {
		setX(getX() + dx);
		setY(getY() + dy);
		setFramesLeft(getFramesLeft() - 1);
	}

	/**
	 * Calculates the animation movements to get to the given position from
	 * the current position in the specified number of frames
	 *
	 * @param nx
	 *            Target x
	 * @param ny
	 *            Target y
	 * @param frames
	 *            Number of frames to move in
	 */
	public void animateTo(int nx, int ny, int frames) {
		setFramesLeft(frames); // reset number of frames to move
		dx = (nx - getX()) / getFramesLeft(); // delta is distance between divided by
									// num frames
		dy = (ny - getY()) / getFramesLeft();
	}

	@Override
	public String toString() {
		return "AnimPosition[x=" + getX() + ",y=" + getY() + ",dx=" + dx + ",dy=" + dy + ",framesLeft=" + getFramesLeft() + "]";
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public int getFramesLeft() {
		return framesLeft;
	}

	public void setFramesLeft(int framesLeft) {
		this.framesLeft = framesLeft;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}
}