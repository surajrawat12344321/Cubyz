package io.cubyz.blocks.rotation;

import org.joml.RayAabIntersection;
import org.joml.Vector3f;

import io.cubyz.blocks.BlockInstance;
import io.cubyz.client.Meshes;
import io.cubyz.util.FloatFastList;
import io.cubyz.util.IntFastList;
import io.cubyz.world.NormalChunk;

/**
 * Rotates the block based on the direction the player is placing it.
 */

public class ClientLogRotation extends LogRotation implements ClientRotationMode {
	@Override
	public float getRayIntersection(RayAabIntersection arg0, BlockInstance arg1, Vector3f min, Vector3f max, Vector3f transformedPosition) {
		return 0;
	}
	
	@Override
	public int generateChunkMesh(BlockInstance bi, FloatFastList vertices, FloatFastList normals, IntFastList faces, IntFastList lighting, FloatFastList texture, IntFastList renderIndices, int renderIndex) {
		
		boolean[] directionInversion;
		int[] directionMap;
		switch(bi.getData()) {
			default:{
				directionInversion = new boolean[] {false, false, false};
				directionMap = new int[] {0, 1, 2};
				break;
			}
			case 1: {
				directionInversion = new boolean[] {true, true, false};
				directionMap = new int[] {0, 1, 2};
				break;
			}
			case 2: {
				directionInversion = new boolean[] {true, false, false};
				directionMap = new int[] {1, 0, 2};
				break;
			}
			case 3: {
				directionInversion = new boolean[] {false, true, false};
				directionMap = new int[] {1, 0, 2};
				break;
			}
			case 4: {
				directionInversion = new boolean[] {false, false, true};
				directionMap = new int[] {0, 2, 1};
				break;
			}
			case 5: {
				directionInversion = new boolean[] {false, true, false};
				directionMap = new int[] {0, 2, 1};
				break;
			}
		}
		
		Meshes.blockMeshes.get(bi.getBlock()).model.addToChunkMeshSimpleRotation(bi.x & NormalChunk.chunkMask, bi.y & NormalChunk.chunkMask, bi.z & NormalChunk.chunkMask, directionMap, directionInversion, bi.getBlock().atlasX, bi.getBlock().atlasY, bi.light, bi.getNeighbors(), vertices, normals, faces, lighting, texture, renderIndices, renderIndex);
		return renderIndex + 1;
	}
}
