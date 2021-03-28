package io.cubyz.gui;

public abstract class Component {
	/**How the coordinates are given relative to bounding frame.*/
	public static final byte	ALIGN_TOP_LEFT		= 0b0101,
								ALIGN_TOP			= 0b0100,
								ALIGN_TOP_RIGHT		= 0b0110,
								ALIGN_LEFT			= 0b0001,
								ALIGN_CENTER		= 0b0000,
								ALIGN_RIGHT			= 0b0010,
								ALIGN_BOTTOM_LEFT	= 0b1001,
								ALIGN_BOTTOM		= 0b1000,
								ALIGN_BOTTOM_RIGHT	= 0b1010;
	/**The x and y coordinates relative to the alignment position.*/
	protected int x, y;
	protected int width, height;
	/**The x and y coordinates relative to the top left corner of the window.*/
	protected int trueX, trueY;
	protected byte align;
	
	public Component() {
		
	}
	public Component(int x, int y, int width, int height, byte align) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.align = align;
	}
	
	/**
	 * Absolute coordinates relative to the screen.
	 * @return x
	 */
	public int getX() {
		return trueX;
	}
	
	/**
	 * Absolute coordinates relative to the screen.
	 * @return y
	 */
	public int getY() {
		return trueY;
	}
	
	/**
	 * Checks if the given coordinate is inside the bounds of this component.
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean isInside(int x, int y) {
		return x >= this.x
				&& x < this.x + width
				&& y >= this.y
				&& y < this.y + height;
	}
	
	public void render(long nvg, Container parent) {
		int x0 = parent.getX();
		int y0 = parent.getY();
		if((align & ALIGN_LEFT) != 0) {
			x0 += x;
		} else if((align & ALIGN_RIGHT) != 0) {
			x0 += parent.width - x;
		} else {
			x0 += parent.height/2 + x;
		}
		if((align & ALIGN_TOP) != 0) {
			y0 += y;
		} else if((align & ALIGN_BOTTOM) != 0) {
			y0 += height - y;
		} else {
			y0 += height/2 + y;
		}
		trueX = x0;
		trueY = y0;
	}
	
	/**
	 * Renders directly on the screen, without further considering alignment. Only call, if you know what you are doing.
	 * @param nvg
	 * @param src
	 * @param x coordinate with alignment considered.
	 * @param y coordinate with alignment considered.
	 */
	public abstract void render(long nvg, Container parent, int x, int y);
}
