package cubyz.world.terrain.structures;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import cubyz.utils.log.Log;
import cubyz.world.Chunk;
import cubyz.world.blocks.Blocks;

/**
 * Stores the structure of all blocks close to the surface.
 */
public class BlockStructure {
	private static class BlockStack {
		final int block;
		final int min;
		final int max;
		final int minFill;
		BlockStack(int block, int min, int max, int minFill) {
			this.block = block;
			this.min = min;
			this.max = max;
			this.minFill = minFill;
		}
	}
	
	final BlockStack[] underground;
	
	public BlockStructure(String data, int lineNumber, File file) {
		ArrayList<BlockStack> underground = new ArrayList<BlockStack>();
		String[] blocks = data.split(",");
		try {
			for(int i = 0; i < blocks.length; i++) {
				String[] parts = blocks[i].trim().split("\\s+");
				int min = 1;
				int max = 1;
				int minFill = 0;
				String blockString = parts[parts.length - 1];
				if(parts.length >= 2) {
					min = max = Integer.parseInt(parts[0]);
					if(parts[1].equalsIgnoreCase("to") && parts.length >= 4) {
						min = Integer.parseInt(parts[0]);
						max = Integer.parseInt(parts[2]);
						blockString = parts[3];
						if(parts.length == 6 && parts[3].equalsIgnoreCase("fill")) {
							minFill = Integer.parseInt(parts[4]);
						}
					} else if(parts.length == 4 && parts[1].equalsIgnoreCase("fill")) {
						minFill = Integer.parseInt(parts[2]);
					}
				}
				int block = Blocks.getByID(blockString);
				if(block != 0) {
					underground.add(new BlockStack(block, min, max, minFill));
				}
			}
		} catch(Exception e) {
			Log.severe("Invalid syntax in line " + lineNumber + " in file " + file.getPath());
		}
		this.underground = underground.toArray(new BlockStack[0]);
	}
	
	public int generateColumn(Chunk chunk, int x, int y, int z, Random rand) {
		int depth = 0;
		for(BlockStack stack : underground) {
			int num = Math.max(stack.min, stack.minFill - depth);
			if(stack.max - num > 0) {
				num += rand.nextInt(stack.max - num);
			}
			for(int i = 0; i < num && y >= 0; i++) {
				if(y < Chunk.CHUNK_WIDTH)
					chunk.blocks[Chunk.getIndex(x, y, z)] = stack.block;
				depth++;
				y--;
			}
		}
		return y;
	}
}
