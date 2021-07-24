package cubyz.client.renderUniverse;

import java.awt.image.BufferedImage;

import cubyz.gui.rendering.Texture;
import cubyz.utils.Utils;

/**
 * Organizes the TextureAtlas. It uses a pretty simple algorithm, that just simply assumes most textures are 16×16. Textures are padded to fit that grid of resolution 16.
 * The texture atlas is also trying to be a square, because forcing a square makes it easier to prevent super wide(and thus inefficient) textures.
 */

public class TextureAtlas {
	/** Must be a power of 2. */
	public static final int GRID_SIZE = 16;
	/** Used to check if a textures follows that grid. */
	public static final int GRID_MASK = GRID_SIZE - 1;
	public static final TextureAtlas BLOCKS = new TextureAtlas(8);
	private Texture texture;
	/** The image backing the atlas. */
	private BufferedImage atlas = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
	/** Specifies how filled a given row is. */
	private int[] rows = new int[1];
	private int size = 0;
	private boolean textureUpdated = true;
	public TextureAtlas(int initialSize) {
		if((initialSize & (initialSize -1)) != 0) {
			throw new IllegalArgumentException("Texture atlas size should be a power of 2!");
		}
		resizeImage(initialSize);
	}
	private void resizeImage(int newSize) {
		BufferedImage newAtlas = new BufferedImage(newSize*GRID_SIZE, newSize*GRID_SIZE, BufferedImage.TYPE_INT_ARGB);
		newAtlas.getGraphics().drawImage(atlas, 0, 0, null);
		atlas = newAtlas;
		int[] newRows = new int[newSize];
		System.arraycopy(rows, 0, newRows, 0, size);
		rows = newRows;
		size = newSize;
	}
	/**
	 * Puts a new image onto the atlas. Returns the coordinates of the texture on the atlas, given as {x, y, width, height}.
	 * @param image
	 * @return {x, y, width, height}
	 */
	public int[] addTexture(BufferedImage image) {
		if(image == null) return new int[] {0, 0, 0, 0};
		// Get the sizes and make them fit to the grid size:
		int width = (image.getWidth() + GRID_MASK)/GRID_SIZE;
		int height = (image.getHeight() + GRID_MASK)/GRID_SIZE;
		while(width > size || height > size) {
			resizeImage(size*2);
		}
		int startX, startY;
		Check_for_the_next_flat_region_that_supports_adding_the_width_and_height_without_extra_padding:
		while(true) {
			for(int i = 0; i < size; i++) {
				if(size - rows[i] >= width) {
					// Sounds good. Let's see if it's steady enough for the texture.
					boolean lookingGood = false;
					for(int j = i; j < size; j++) {
						if(j - i == height) {
							lookingGood = true;
							break; // Just how I want it. Nice and clean.
						}
						if(rows[i] != rows[j]) break; // It's too rough.
					}
					if(lookingGood) {
						// Let's take it!
						startX = rows[i];
						startY = i;
						break Check_for_the_next_flat_region_that_supports_adding_the_width_and_height_without_extra_padding;
					}
				}
			}
			// Nothing found, so increase the size:
			resizeImage(size*2);
		}
		// The rest is easy. It just needs to be inserted:
		atlas.getGraphics().drawImage(image, startX*GRID_SIZE, startY*GRID_SIZE, null);
		// And lastly the rows need to be adjusted:
		for(int i = startY; i < startY + height; i++) {
			rows[i] += width;
		}
		textureUpdated = true;
		return new int[] {startX*GRID_SIZE, startY*GRID_SIZE, image.getWidth(), image.getHeight()};
	}
	
	public int size() {
		return size*GRID_SIZE;
	}
	
	public void write() {
		Utils.writeImage(atlas, "debug_atlas.png");
	}
	
	public void bindTexture() {
		if(textureUpdated) {
			if(texture == null) {
				texture = new Texture(atlas);
			}
			texture.set(atlas, 4);
			textureUpdated = false;
		}
		texture.bind();
	}
	
	public void unbindTexture() {
		texture.unbind();
	}
	
}
