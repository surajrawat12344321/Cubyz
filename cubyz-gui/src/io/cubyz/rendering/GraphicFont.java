package io.cubyz.rendering;

import static java.awt.Font.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.awt.Rectangle;

import javax.imageio.ImageIO;

public class GraphicFont {
	
	public class Glyph{
		
		public GraphicFont font;
		public int 	texture_left = 0,
					texture_top = 0,
					texture_width = 0,
					texture_height = 0,
					xOffset = 0,
					effectiveWidth = 0;
		public BufferedImage getImage() {
			return font.fontTexture.getSubimage(texture_left, texture_top, texture_width, texture_height);
		}

		public String toString() {
			return "left: "+texture_left+", top: "+texture_top+", width: "+texture_width+", height: "+texture_height+", offset: "+xOffset+", render width: "+effectiveWidth;
		}
	}
	
	//Graphical variables
	private Font font;
	private BufferedImage fontTexture = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
	private Texture texture;
	private Graphics2D fontGraphics = fontTexture.createGraphics();

	//List of all already used
	private HashMap<Character, Glyph> glyphs = new HashMap<Character, Glyph>();
	
	/**
	 * Load the font from the standard Library
	 */
	public void loadFromAwt() {
		font = new Font("Arial", Font.PLAIN, 144);
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
	public Glyph getGlyph(char letter) {
		//does the glyph already exist?
		if(glyphs.containsKey(letter))
			return glyphs.get(letter);
		//create texture if it doesnt exist
		
		//letter metrics
		FontMetrics metrics = fontGraphics.getFontMetrics();

		Rectangle bounds = font.createGlyphVector(metrics.getFontRenderContext(), new char[]{letter}).getGlyphPixelBounds(0, metrics.getFontRenderContext(), 0, 0);
		
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
		newGraphic.drawString(""+letter, newFontTexture.getWidth()-charWidth-bounds.x,metrics.getAscent());
		
		//replace the old by the new 
		fontTexture = newFontTexture;
		fontGraphics = newGraphic;
		fontGraphics.dispose();
		if(texture==null)
			texture = new Texture(fontTexture);
		else texture.set(fontTexture);
		
		
		//create the Glyph
		Glyph glyph = new Glyph();
		glyph.font = this;
		glyph.texture_left = newFontTexture.getWidth()-charWidth;
		glyph.texture_top = 0;
		glyph.texture_width = charWidth;
		glyph.texture_height = metrics.getHeight();
		glyph.effectiveWidth = metrics.charWidth(letter);
		glyph.xOffset = bounds.x;
		
		glyphs.put(letter,glyph);
	    return glyph;
	}
	public Texture getTexture() {
		if(texture == null)
			getGlyph(' ');
		return texture;
	}
}
