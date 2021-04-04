package io.cubyz.rendering;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.*;

import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;
import io.cubyz.utils.log.Log;

public class Texture {

	int id = -1;
	ByteBuffer byteBuffer;
	public int width ,height;

	public Texture(String path) {
		try {
			//loading from file
			PNGDecoder decoder = new PNGDecoder(new FileInputStream(path));
		
			//data into byteBuffer
			byteBuffer = ByteBuffer.allocateDirect(4 * decoder.getWidth() * decoder.getHeight());	
			decoder.decode(byteBuffer, decoder.getWidth() * 4, Format.RGBA);
			byteBuffer.flip();
			//creating
			create(byteBuffer,decoder.getWidth(), decoder.getHeight());
		} catch (IOException e) {
			Log.severe(e);
		}

	}
	

	public Texture(BufferedImage image) {
		set(image);
	}
	public void set(BufferedImage image) {
		// creating ByteBuffer
		width = image.getWidth();
		height = image.getHeight();

		// Load texture contents into a byte buffer
		byteBuffer = ByteBuffer.allocateDirect(4 * width * height);

		// decode image
		// ARGB format to -> RGBA
		for (int h = 0; h < height; h++)
			for (int w = 0; w < width; w++) {
				int argb = image.getRGB(w, h);
				byteBuffer.put((byte) (0xFF & (argb >> 16)));
				byteBuffer.put((byte) (0xFF & (argb >> 8)));
				byteBuffer.put((byte) (0xFF & (argb)));
				byteBuffer.put((byte) (0xFF & (argb >> 24)));
			}
		byteBuffer.flip();
		
		create(byteBuffer,image.getWidth(),image.getHeight());
	}
	private void create(ByteBuffer bytes,int width,int height) {

		glActiveTexture(GL_TEXTURE0);

		byteBuffer = bytes;

		// create and select the texture
		if(id==-1)
			id = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, id);

		// size of the components
		glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

		// load picture
		// byteBuffer = bufferedImagetoByteBuffer(image);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width,height, 0, GL_RGBA, GL_UNSIGNED_BYTE,
				byteBuffer);

	}

	public int getId() {
		return id;
	}

	public void bind() {
		glBindTexture(GL_TEXTURE_2D, id);
	}

	public void unbind() {
		glBindTexture(GL_TEXTURE_2D, 0);
	}
}
