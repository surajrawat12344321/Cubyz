package io.cubyz.gui.text;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.font.GlyphVector;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import io.cubyz.gui.rendering.Texture;

public class GraphicFont {
	
	//Graphical variables
	public Font font;
	private BufferedImage fontTexture = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
	private Texture texture = new Texture(fontTexture);
	public Graphics2D fontGraphics = fontTexture.createGraphics();

	//List of all already used
	private HashMap<Integer, Rectangle> glyphs = new HashMap<Integer, Rectangle>();
	
	/**
	 * Load the font from the standard Library
	 */
	public void loadFromAwt() {
		font = new Font("Calibri", Font.PLAIN, 16);
		fontGraphics.setFont(font);
	}
	void loadFromFile() {
		// TODO
	}
	/**
	 * Dispose the Font
	 */
	void dispose() {
		fontGraphics.dispose();
	}
	/**
	 * Get the glyph position inside the texture.
	 * @param letter
	 * @return the rectangle bounds of the glyph
	 */
	public Rectangle getGlyph(GlyphVector source, int indexInGlyphVector) {
		//does the glyph already exist?
		int letterCode = source.getGlyphCode(indexInGlyphVector);
		if(glyphs.containsKey(letterCode))
			return glyphs.get(letterCode);

		//letter metrics
		FontMetrics metrics = fontGraphics.getFontMetrics();
		Rectangle bounds = source.getGlyphPixelBounds(indexInGlyphVector, metrics.getFontRenderContext(), 0, 0);

		//create the Glyph
		Rectangle glyph = new Rectangle();
		glyph.x = fontTexture.getWidth();
		glyph.width = bounds.width;
		glyph.height = bounds.height;
		
		// Paint the new glyph in the texture:
		
		//make the fontTexture bigger.
		BufferedImage newFontTexture = new BufferedImage(fontTexture.getWidth()+glyph.width,metrics.getHeight(),BufferedImage.TYPE_INT_ARGB);
		Graphics2D newGraphic = newFontTexture.createGraphics();
		metrics = fontGraphics.getFontMetrics();
		
		//drawing the old stuff
		newGraphic.drawImage(fontTexture,0,0,null);
		//drawing the new letter
		newGraphic.setFont(font);
		newGraphic.setColor(Color.white);
		newGraphic.setClip(fontTexture.getWidth(), 0, glyph.width, glyph.height);
		newGraphic.drawGlyphVector(source, glyph.x-bounds.x,-bounds.y);
		
		//replace the old by the new 
		fontTexture = newFontTexture;
		fontGraphics = newGraphic;
		fontGraphics.dispose();
		if(texture==null)
			texture = new Texture(fontTexture);
		else texture.set(fontTexture);
		
		
		
		glyphs.put(letterCode, glyph);
	    return glyph;
	}
	public Texture getTexture() {
		return texture;
	}
}
