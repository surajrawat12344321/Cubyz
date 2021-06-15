package io.cubyz.gui.text;

import java.awt.Color;
import java.awt.font.TextHitInfo;
import java.awt.font.TextLayout;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Parses and shows text in a pretty format with colors and stuff.
 */
public class PrettyText {
	TextLayout layout;
	TextMarker[] markers = new TextMarker[0];
	LineSegment[] lines = new LineSegment[0];
	String actualText;

	public PrettyText(String text) {
		parse(text);
	}

	/**
	 * Parses the formatting hints of the text.
	 * @param text
	 */
	public void parse(String text) {
		char[] chars = text.toCharArray();
		StringBuilder reducedString = new StringBuilder();
		ArrayList<TextMarker> markers = new ArrayList<>();
		for(int i = 0; i < chars.length; i++) {
			if(i+1 == chars.length) {
				// The last character is at most closing a given effect:
				if(chars[i] != '*' && chars[i] != '_')
					reducedString.append(chars[i]);
				break;
			}
			switch(chars[i]) {
				case '\\':
					// An escape sequence will just simply append the following character.
					reducedString.append(chars[++i]);
					break;
				case '*':
					// 1 makes things italic, 2 make things bold:
					if(chars[i+1] == '*') {
						markers.add(new TextMarker(TextMarker.TYPE_BOLD, reducedString.length()));
						i++;
					} else {
						markers.add(new TextMarker(TextMarker.TYPE_ITALIC, reducedString.length()));
					}
					break;
				case '_':
					// 1 makes things underlined, 2 make overlined:
					if(chars[i+1] == '_') {
						markers.add(new TextMarker(TextMarker.TYPE_OVERLINE, reducedString.length()));
						i++;
					} else {
						markers.add(new TextMarker(TextMarker.TYPE_UNDERLINE, reducedString.length()));
					}
					break;
				case '#':
					// 1 specifies a single color, 2 starts an animation. Syntax errors for colors will be interpreted as '0'.
					if(chars[i+1] == '#') {
						int[] index = new int[]{i+2};
						Animation animation = new Animation(index, chars);
						markers.add(new TextMarker(TextMarker.TYPE_COLOR_ANIMATION, reducedString.length(), animation));
						i = index[0]-1;
					} else {
						int[] index = new int[]{i+1};
						int color = parseColor(index, chars);
						markers.add(new TextMarker(TextMarker.TYPE_COLOR, reducedString.length(), color));
						i = index[0]-1;
					}
					break;
				default:
					reducedString.append(chars[i]);
			}
		}
		actualText = reducedString.toString();
		if(actualText.length() == 0)
			actualText = " ";
		layout = new TextLayout(actualText, Text.font.font, Text.font.fontGraphics.getFontRenderContext());
		sortMarkers(markers);
		prepareLines(markers);
		this.markers = markers.toArray(new TextMarker[0]);
	}
	
	private float getMarkerX(TextHitInfo cursorPosition) {
		if(cursorPosition == null || layout == null) return 0;
		Point2D.Float cursorPos = new Point2D.Float();
		layout.hitToPoint(cursorPosition, cursorPos);
		return cursorPos.x;
	}

	/**
	 * Sorts the markers by x-position based on the new layout.
	 * On such small sizes insert sort should be the fastest. Especially considering that for western languages the array is already sorted.
	 */
	private void sortMarkers(ArrayList<TextMarker> markers) {
		for(int i = 0; i < markers.size(); i++) {
			// Markers are bound to the following character, so they should always be stored by leading edge.
			int xPosition = (int)getMarkerX(TextHitInfo.leading(markers.get(i).charPosition));
			markers.get(i).charPosition = xPosition; // Reuse the variable.
			// Insert it:
			for(int j = i-1; j >= 0; j--) {
				if(xPosition >= markers.get(j).charPosition)
					break;
				// Swap them:
				TextMarker local = markers.get(j);
				markers.set(j, markers.get(j+1));
				markers.set(j+1, local);
			}
		}
	}

	/**
	 * Splits the under-/overlines into segments of equal color and removes all line-related markers.
	 */
	private void prepareLines(ArrayList<TextMarker> markers) {
		ArrayList<LineSegment> lines = new ArrayList<>();
		boolean isBold = false;
		float overlineStart = -1;
		float underlineStart = -1;
		float position;
		TextMarker colorInfo = null;
		for(int i = 0; i < markers.size(); i++) {
			switch(markers.get(i).type) {
				case TextMarker.TYPE_BOLD:
					// Finish started lines:
					position = markers.get(i).charPosition;
					if(overlineStart != -1) {
						lines.add(new LineSegment(overlineStart, position-overlineStart, true, isBold, colorInfo));
						overlineStart = position;
					}
					if(underlineStart != -1) {
						lines.add(new LineSegment(underlineStart, position-underlineStart, false, isBold, colorInfo));
						underlineStart = position;
					}
					isBold = !isBold;
					break;
				case TextMarker.TYPE_COLOR:
				case TextMarker.TYPE_COLOR_ANIMATION:
					// Finish started lines:
					position = markers.get(i).charPosition;
					if(overlineStart != -1) {
						lines.add(new LineSegment(overlineStart, position-overlineStart, true, isBold, colorInfo));
						overlineStart = position;
					}
					if(underlineStart != -1) {
						lines.add(new LineSegment(underlineStart, position-underlineStart, false, isBold, colorInfo));
						underlineStart = position;
					}
					colorInfo = markers.get(i);
					break;
				case TextMarker.TYPE_OVERLINE:
					position = markers.get(i).charPosition;
					if(overlineStart != -1) {
						lines.add(new LineSegment(overlineStart, position-overlineStart, true, isBold, colorInfo));
						overlineStart = -1;
					} else {
						overlineStart = position;
					}
					markers.remove(i);
					i--;
					break;
				case TextMarker.TYPE_UNDERLINE:
					position = markers.get(i).charPosition;
					if(underlineStart != -1) {
						lines.add(new LineSegment(underlineStart, position-underlineStart, false, isBold, colorInfo));
						underlineStart = -1;
					} else {
						underlineStart = position;
					}
					markers.remove(i);
					i--;
					break;
			}
		}
		// Finish started lines:
		position = (int)layout.getBounds().getWidth();
		if(overlineStart != -1) {
			lines.add(new LineSegment(overlineStart, position-overlineStart, true, isBold, colorInfo));
			overlineStart = position;
		}
		if(underlineStart != -1) {
			lines.add(new LineSegment(underlineStart, position-underlineStart, false, isBold, colorInfo));
			underlineStart = position;
		}
		this.lines = lines.toArray(new LineSegment[0]);
	}

	public void draw(float ratio, CubyzGraphics2D g2d, float x, float y) {
		// Draw the text:
		g2d.textMarkingInfo = markers;
		g2d.preDrawGlyphVector(x);
		layout.draw(g2d, x, y);
		g2d.textMarkingInfo = null;

		// Draw the lines:
		for(LineSegment line : lines) {
			line.draw(g2d, ratio, x, y);
		}
	}

	/**
	 * A simple hexadecimal parser for 6 hexdigits.
	 * @param index position in hex string
	 * @param chars hex string
	 * @return binary value
	 */
	static int parseColor(int[] index, char[] chars) {
		int i = index[0];
		int result = 0;
		for(int i2 = 0; i2 < 6 && i + i2 < chars.length; i2++) {
			switch(chars[i+i2]) {
				case '1':
				case '2':
				case '3':
				case '4':
				case '5':
				case '6':
				case '7':
				case '8':
				case '9':
					result |= chars[i+i2] - '0';
					break;
				case 'a':
				case 'b':
				case 'c':
				case 'd':
				case 'e':
				case 'f':
					result |= chars[i+i2] - 'a' + 10;
					break;
				case 'A':
				case 'B':
				case 'C':
				case 'D':
				case 'E':
				case 'F':
					result |= chars[i+i2] - 'A' + 10;
					break;
			}
			if(i2 < 5)
				result <<= 4;
		}
		index[0] += 6;
		return result;
	}
}

/**
 * Stores the needed data for a line segment.
 */
class LineSegment {
	final float x, width;
	final boolean isOverline;
	final boolean isBold;
	final TextMarker colorInfo;
	public LineSegment(float x, float width, boolean isOverline, boolean isBold, TextMarker colorInfo) {
		this.x = x;
		this.width = width;
		this.isOverline = isOverline;
		this.isBold = isBold;
		this.colorInfo = colorInfo;
	}

	public void draw(CubyzGraphics2D g2d, float ratio, float left, float top) {
		int color = 0;
		if(colorInfo != null) {
			if(colorInfo.type == TextMarker.TYPE_COLOR) {
				color = colorInfo.color;
			} else if(colorInfo.type == TextMarker.TYPE_COLOR_ANIMATION) {
				color = colorInfo.animation.getColor();
			}
		}
		g2d.setColor(color | 0xff000000);
		if(isOverline) {
			top -= Text.font.font.getSize();
		}
		if(isBold) {
			g2d.fillRect((left + x)*ratio, (top - 0.375f)*ratio, width*ratio, ratio*1.5f);
		} else {
			g2d.fillRect((left + x)*ratio, top*ratio, width*ratio, ratio*0.75f);
		}
	}
}

/**
 * Animates a color change.
 */
class Animation {
	/** Time that each color is shown in the animation. */
	int time;
	/** Colors in HSB format. */
	float[][] colors;
	public Animation(int[] index, char[] chars) {
		int i = index[0];
		// Find the end of the timer:
		for(; i < chars.length; i++) {
			if(chars[i] == '#') {
				i--;
				break;
			}
		}
		if(i == chars.length) {
			return;
		}
		// Parse the timer:
		try {
			time = Integer.parseInt(new String(chars, index[0], i-index[0]+1));
		} catch(Exception e) {
			return; // Just a syntax error. Nothing to complain.
		}
		// Parse the colors:
		index[0] = i+1;
		ArrayList<float[]> colors = new ArrayList<>();
		while(chars[index[0]] == '#') {
			index[0]++;
			int color = PrettyText.parseColor(index, chars);
			colors.add(Color.RGBtoHSB((color & 0xff0000)>>>16, (color & 0xff00)>>>8, color & 0xff, new float[3]));
		}
		this.colors = colors.toArray(new float[0][3]);
	}

	public int getColor() {
		// TODO: Use a colorspace that has a better interpolation, such as HCL.
		int mainColor = (int)((System.currentTimeMillis()/time)%colors.length);
		if(mainColor < 0) mainColor += colors.length; // Make sure it is always positive even in case the time was negative.
		float interpolation = (float)(System.currentTimeMillis()%time)/time;
		float h = colors[mainColor][0];
		float s = colors[mainColor][1];
		float b = colors[mainColor][2];
		mainColor = (mainColor + 1)%colors.length;
		float h2 = colors[mainColor][0];
		// Hue is in modulo space, so the shortest distance might go around the circle.
		if(Math.abs(h2-h) < 0.5f) {
			h = h*(1-interpolation) + h2*interpolation;
		} else {
			if(h < h2) {
				h++;
			} else {
				h2++;
			}
			h = h*(1-interpolation) + h2*interpolation;
			h %= 1;
		}
		s = s*(1-interpolation) + colors[mainColor][1]*interpolation;
		b = b*(1-interpolation) + colors[mainColor][2]*interpolation;
		return Color.HSBtoRGB(h, s, b) & 0x00ffffff;
	}
}

/**
 * Marks a style change in a given text.
 */
class TextMarker {
	static final byte TYPE_BOLD = 1;
	static final byte TYPE_ITALIC = 2;
	static final byte TYPE_UNDERLINE = 4;
	static final byte TYPE_OVERLINE = 8;
	static final byte TYPE_COLOR = 16;
	static final byte TYPE_COLOR_ANIMATION = 32;
	byte type;
	int charPosition;
	/** Color in HSB format. */
	int color;
	Animation animation;
	public TextMarker(byte type, int charPosition) {
		this.type = type;
		this.charPosition = charPosition;
	}
	public TextMarker(byte type, int charPosition, int color) {
		this(type, charPosition);
		this.color = color;
	}
	public TextMarker(byte type, int charPosition, Animation animation) {
		this(type, charPosition);
		this.animation = animation;
	}
}