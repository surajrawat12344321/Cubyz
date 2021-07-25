package cubyz.rendering;

import java.awt.image.BufferedImage;
import java.util.HashMap;

public final class TextureManager {
	private static HashMap<String, Texture > textures = new HashMap<String,Texture>();
	public static Texture require(String path) {
		if(textures.containsKey(path)) {
			return textures.get(path);
		}else {
			Texture rv = new Texture(path);
			textures.put(path, rv);
			return rv;
		}
	}
	
	private static Texture whiteTexture = null;
	public static Texture white() {
		if(whiteTexture==null) {
			BufferedImage bufferedWhite = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
			bufferedWhite.setRGB(0, 0, 0xffffffff);
			whiteTexture = new Texture(bufferedWhite);
		}
		return whiteTexture;
	}
}
