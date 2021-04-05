package io.cubyz.rendering;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.GlyphVector;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.awt.Rectangle;

public class GraphicFont {
	
	public class Glyph{
		public int 	texture_left = 0,
					texture_top = 0,
					texture_width = 0,
					texture_height = 0;
	}
	
	//Graphical variables
	public Font font;
	private BufferedImage fontTexture = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
	private Texture texture = new Texture(fontTexture);
	public Graphics2D fontGraphics = fontTexture.createGraphics();

	//List of all already used
	private HashMap<Integer, Glyph> glyphs = new HashMap<Integer, Glyph>();
	
	/**
	 * Load the font from the standard Library
	 */
	public void loadFromAwt() {
		font = new Font("Calibri", Font.PLAIN, 144);
		fontGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		fontGraphics.setFont(font);
		
		
	}
	void loadFromFile() {
		//todo
	}
	/**
	 * Dispose the Font
	 */
	void dispose() {
		fontGraphics.dispose();
	}
	/**
	 * Get a Glyph
	 * @param letter
	 * @return the Glyph
	 */
	public Glyph getGlyph(GlyphVector source, int indexInGlyphVector) {
		//does the glyph already exist?
		int letterCode = source.getGlyphCode(indexInGlyphVector);
		if(glyphs.containsKey(letterCode))
			return glyphs.get(letterCode);
		//create texture if it doesnt exist
		
		System.out.println(letterCode);
		
		//letter metrics
		FontMetrics metrics = fontGraphics.getFontMetrics();

		Rectangle bounds = source.getGlyphPixelBounds(indexInGlyphVector, metrics.getFontRenderContext(), 0, 0);
		
		//create 1 Letter
		int charWidth = bounds.width;
		int charHeight = bounds.height;
		
		
		//make the fontTexture bigger
		BufferedImage newFontTexture = new BufferedImage(fontTexture.getWidth()+charWidth,metrics.getHeight(),BufferedImage.TYPE_INT_ARGB);
		Graphics2D newGraphic = newFontTexture.createGraphics();
		
		//drawing the old stuff
		newGraphic.drawImage(fontTexture,0,0,null);
		
		//drawing the new letter
		newGraphic.setFont(font);
		newGraphic.setColor(Color.red);
		newGraphic.setClip(fontTexture.getWidth(), 0, charWidth, charHeight);
		newGraphic.drawGlyphVector(source, newFontTexture.getWidth()-charWidth-bounds.x,-bounds.y);
		
		//replace the old by the new 
		fontTexture = newFontTexture;
		fontGraphics = newGraphic;
		fontGraphics.dispose();
		if(texture==null)
			texture = new Texture(fontTexture);
		else texture.set(fontTexture);
		
		
		//create the Glyph
		Glyph glyph = new Glyph();
		glyph.texture_left = newFontTexture.getWidth()-charWidth;
		glyph.texture_top = 0;
		glyph.texture_width = charWidth;
		glyph.texture_height = charHeight;
		
		glyphs.put(letterCode,glyph);
	    return glyph;
	}
	public Texture getTexture() {
		return texture;
	}
}
