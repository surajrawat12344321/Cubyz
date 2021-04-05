package io.cubyz.rendering;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform2f;
import static org.lwjgl.opengl.GL20.glUniform4f;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.nio.FloatBuffer;
import java.text.AttributedCharacterIterator;
import java.util.Map;

import org.lwjgl.system.MemoryUtil;

import io.cubyz.gui.Design;
import io.cubyz.rendering.GraphicFont.Glyph;

/**
 * Essentially just an interface for drawing with {@code Textlayout.draw}. Not much else of {@code Graphics2D} is supported.
 */

public class CubyzGraphics2D extends Graphics2D {
	// Static initialization:
	public static final CubyzGraphics2D instance = new CubyzGraphics2D();

	public static int textVBO = -1;
	public static Shader textShader = new Shader();
	
	static { // Init opengl stuff:
		// vertex buffer
		float rawdata[] = { 
				0,0,		0,0,
				0,-1,		0,1,
				1,0,		1,0,
				1,-1,		1,1
			};
		FloatBuffer buffer = MemoryUtil.memAllocFloat(rawdata.length);
		buffer.put(rawdata).flip();
		
		textVBO = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, textVBO);
		glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
		glVertexAttribPointer(0,2,GL_FLOAT,false,4*4,0);
		glVertexAttribPointer(1,2,GL_FLOAT,false,4*4,8);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		
		//Shader
		textShader.loadFromFile("assets/cubyz/shaders/Gui/GuiText.vs", "assets/cubyz/shaders/Gui/GuiText.fs");
		
		
	}
	
	public GraphicFont font;
	
	/**Design that {@code this} is using.*/
	public Design design;
	
	public float textHeight;

	@Override
	public void drawGlyphVector(GlyphVector glyphs, float left, float top) {
		textShader.bind();
		font.getTexture().bind();
		
		//vertex and shader
		int loc_texCoords = textShader.getUniformLocation("texture_rect");

		int loc_scene = textShader.getUniformLocation("scene");
		int loc_offset = textShader.getUniformLocation("offset");
		int loc_ratio = textShader.getUniformLocation("ratio");

		//fragment
		int loc_fontSize = textShader.getUniformLocation("font_size");

		glUniform2f(loc_scene, design.width, design.height);
		glUniform2f(loc_fontSize,font.getTexture().width, font.getTexture().height);
		glUniform1f(loc_ratio, (float)textHeight/font.getTexture().height);
		
		
		glBindBuffer(GL_ARRAY_BUFFER, textVBO);
		// Draw all the glyphs:
		for (int i = 0; i < glyphs.getNumGlyphs(); i++) {
			Glyph glyph = font.getGlyph(glyphs, i);
			
			Rectangle bounds = glyphs.getGlyphPixelBounds(i, font.fontGraphics.getFontRenderContext(), left, top);
			
			glUniform2f(loc_offset, bounds.x, bounds.y);
			glUniform4f(loc_texCoords, glyph.texture_left,glyph.texture_top,glyph.texture_width,glyph.texture_height);
			
			glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
		}
		
		font.getTexture().unbind();
		textShader.unbind();
	}

	@Override
	public Font getFont() {
		return font.font;
	}
	
	
	// Everything below is unsupported!

	@Deprecated
	@Override
	public void addRenderingHints(Map<?, ?> arg0) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public void clip(Shape arg0) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public void draw(Shape arg0) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public boolean drawImage(Image arg0, AffineTransform arg1, ImageObserver arg2) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public void drawImage(BufferedImage arg0, BufferedImageOp arg1, int arg2, int arg3) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public void drawRenderableImage(RenderableImage arg0, AffineTransform arg1) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public void drawRenderedImage(RenderedImage arg0, AffineTransform arg1) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public void drawString(String arg0, int arg1, int arg2) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public void drawString(String arg0, float arg1, float arg2) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public void drawString(AttributedCharacterIterator arg0, int arg1, int arg2) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public void drawString(AttributedCharacterIterator arg0, float arg1, float arg2) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public void fill(Shape arg0) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public Color getBackground() {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public Composite getComposite() {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public GraphicsConfiguration getDeviceConfiguration() {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public FontRenderContext getFontRenderContext() {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public Paint getPaint() {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public Object getRenderingHint(Key arg0) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public RenderingHints getRenderingHints() {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public Stroke getStroke() {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public AffineTransform getTransform() {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public boolean hit(Rectangle arg0, Shape arg1, boolean arg2) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public void rotate(double arg0) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public void rotate(double arg0, double arg1, double arg2) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public void scale(double arg0, double arg1) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public void setBackground(Color arg0) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public void setComposite(Composite arg0) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public void setPaint(Paint arg0) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public void setRenderingHint(Key arg0, Object arg1) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public void setRenderingHints(Map<?, ?> arg0) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public void setStroke(Stroke arg0) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public void setTransform(AffineTransform arg0) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public void shear(double arg0, double arg1) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public void transform(AffineTransform arg0) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public void translate(int arg0, int arg1) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public void translate(double arg0, double arg1) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public void clearRect(int arg0, int arg1, int arg2, int arg3) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public void clipRect(int arg0, int arg1, int arg2, int arg3) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public void copyArea(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public Graphics create() {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public void dispose() {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public void drawArc(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public boolean drawImage(Image arg0, int arg1, int arg2, ImageObserver arg3) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public boolean drawImage(Image arg0, int arg1, int arg2, Color arg3, ImageObserver arg4) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public boolean drawImage(Image arg0, int arg1, int arg2, int arg3, int arg4, ImageObserver arg5) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public boolean drawImage(Image arg0, int arg1, int arg2, int arg3, int arg4, Color arg5, ImageObserver arg6) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public boolean drawImage(Image arg0, int arg1, int arg2, int arg3, int arg4, int arg5, int arg6, int arg7, int arg8, ImageObserver arg9) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public boolean drawImage(Image arg0, int arg1, int arg2, int arg3, int arg4, int arg5, int arg6, int arg7, int arg8, Color arg9, ImageObserver arg10) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public void drawLine(int arg0, int arg1, int arg2, int arg3) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public void drawOval(int arg0, int arg1, int arg2, int arg3) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public void drawPolygon(int[] arg0, int[] arg1, int arg2) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public void drawPolyline(int[] arg0, int[] arg1, int arg2) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public void drawRoundRect(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public void fillArc(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public void fillOval(int arg0, int arg1, int arg2, int arg3) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public void fillPolygon(int[] arg0, int[] arg1, int arg2) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public void fillRect(int arg0, int arg1, int arg2, int arg3) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public void fillRoundRect(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public Shape getClip() {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public Rectangle getClipBounds() {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public Color getColor() {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public FontMetrics getFontMetrics(Font arg0) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public void setClip(Shape arg0) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public void setClip(int arg0, int arg1, int arg2, int arg3) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public void setColor(Color arg0) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public void setFont(Font font) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public void setPaintMode() {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public void setXORMode(Color arg0) {
		throw new UnsupportedOperationException();
	}

}
