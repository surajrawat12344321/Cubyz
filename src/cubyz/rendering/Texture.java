package cubyz.rendering;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.*;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import cubyz.utils.log.Log;
import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;

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
		// ARGB format to RGBA
		for (int h = 0; h < height; h++) {
			for (int w = 0; w < width; w++) {
				int argb = image.getRGB(w, h);
				byteBuffer.put((byte) (0xFF & (argb >> 16)));
				byteBuffer.put((byte) (0xFF & (argb >> 8)));
				byteBuffer.put((byte) (0xFF & (argb)));
				byteBuffer.put((byte) (0xFF & (argb >> 24)));
			}
		}

		byteBuffer.flip();
		
		create(byteBuffer, width, height);
	}
	public void set(BufferedImage image, int mipmapLevels) {
		// Make the opengl stuff:
		glActiveTexture(GL_TEXTURE0);

		// create and select the texture
		if(id==-1)
			id = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, id);

		// size of the components
		glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST_MIPMAP_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST_MIPMAP_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAX_LOD, mipmapLevels);
		// creating ByteBuffer
		width = image.getWidth();
		height = image.getHeight();

		// Load texture contents into a byte buffer
		byteBuffer = ByteBuffer.allocateDirect(4 * width * height);
		
		// decode image
		// ARGB format to RGBA
		for (int h = 0; h < height; h++) {
			for (int w = 0; w < width; w++) {
				int argb = image.getRGB(w, h);
				byteBuffer.put((byte) (0xFF & (argb >> 16)));
				byteBuffer.put((byte) (0xFF & (argb >> 8)));
				byteBuffer.put((byte) (0xFF & (argb)));
				byteBuffer.put((byte) (0xFF & (argb >> 24)));
			}
		}

		byteBuffer.flip();

		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE,
				byteBuffer);
		
		// Calculate the mipmap levels:
		for(int level = 1; level <= mipmapLevels; level++) {
			byteBuffer.limit(byteBuffer.capacity());
			byteBuffer.rewind();
			int power = 1 << level;
			ByteBuffer newByteBuffer = ByteBuffer.allocateDirect(4 * width * height/power/power);
			for (int h = 0; h < height/power; h++) {
				for (int w = 0; w < width/power; w++) {
					int r = 0;
					int g = 0;
					int b = 0;
					int a = 0;
					int indexOld = w*2 + h*2*width/power*2;
					for(int hOffset = 0; hOffset <= 2*width/power*2; hOffset += 2*width/power*2) {
						for(int wOffset = 0; wOffset <= 2; wOffset += 2) {
							r += byteBuffer.get(indexOld + hOffset + wOffset) & 255;
							g += byteBuffer.get(indexOld + hOffset + wOffset + 1) & 255;
							b += byteBuffer.get(indexOld + hOffset + wOffset + 2) & 255;
							a += byteBuffer.get(indexOld + hOffset + wOffset + 3) & 255;
						}
					}
					r /= 4;
					g /= 4;
					b /= 4;
					a /= 4;
					byteBuffer.put((byte)r);
					byteBuffer.put((byte)g);
					byteBuffer.put((byte)b);
					byteBuffer.put((byte)a);
				}
			}
			newByteBuffer.flip();
			byteBuffer = newByteBuffer;
			glTexImage2D(GL_TEXTURE_2D, level, GL_RGBA, width/power, height/power, 0, GL_RGBA, GL_UNSIGNED_BYTE,
					byteBuffer);
			//create(newByteBuffer, width/power, height/power, level-1);
		}
		glGenerateMipmap(GL_TEXTURE_2D);
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
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE,
				byteBuffer);

	}
	
	public void cleanup() {
		glDeleteTextures(id);
		id = -1;
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
