package io.cubyz.blocks.rotation;

import org.joml.Matrix3f;
import org.joml.RayAabIntersection;
import org.joml.Vector3f;

import io.cubyz.blocks.BlockInstance;
import io.cubyz.client.Meshes;
import io.cubyz.models.Model;
import io.cubyz.util.FloatFastList;
import io.cubyz.util.IntFastList;
import io.cubyz.world.NormalChunk;

/**
 * Rotates and translates the model, so it hangs on the wall or stands on the ground like a torch.<br>
 * It also allows the player to place multiple torches of the same type in different rotation in the same block.
 */

public class ClientTorchRotation extends TorchRotation implements ClientRotationMode {
	// Rotation/translation matrices for torches on the wall:
	private static final Matrix3f POS_X = new Matrix3f().identity().rotateXYZ(0, 0, 0.3f);
	private static final Matrix3f NEG_X = new Matrix3f().identity().rotateXYZ(0, 0, -0.3f);
	private static final Matrix3f POS_Z = new Matrix3f().identity().rotateXYZ(-0.3f, 0, 0);
	private static final Matrix3f NEG_Z = new Matrix3f().identity().rotateXYZ(0.3f, 0, 0);

	@Override
	public float getRayIntersection(RayAabIntersection arg0, BlockInstance arg1, Vector3f min, Vector3f max, Vector3f transformedPosition) {
		return 0;
	}
	
	@Override
	public int generateChunkMesh(BlockInstance bi, FloatFastList vertices, FloatFastList normals, IntFastList faces, IntFastList lighting, FloatFastList texture, IntFastList renderIndices, int renderIndex) {
		byte data = bi.getData();
		Model model = Meshes.blockMeshes.get(bi.getBlock()).model;
		if((data & 0b1) != 0) {
			model.addToChunkMeshRotation((bi.x & NormalChunk.chunkMask) + 0.9f, (bi.y & NormalChunk.chunkMask) + 0.7f, (bi.z & NormalChunk.chunkMask) + 0.5f, POS_X, bi.getBlock().atlasX, bi.getBlock().atlasY, bi.light, bi.getNeighbors(), vertices, normals, faces, lighting, texture, renderIndices, renderIndex);
			renderIndex++;
		}
		if((data & 0b10) != 0) {
			model.addToChunkMeshRotation((bi.x & NormalChunk.chunkMask) + 0.1f, (bi.y & NormalChunk.chunkMask) + 0.7f, (bi.z & NormalChunk.chunkMask) + 0.5f, NEG_X, bi.getBlock().atlasX, bi.getBlock().atlasY, bi.light, bi.getNeighbors(), vertices, normals, faces, lighting, texture, renderIndices, renderIndex);
			renderIndex++;
		}
		if((data & 0b100) != 0) {
			model.addToChunkMeshRotation((bi.x & NormalChunk.chunkMask) + 0.5f, (bi.y & NormalChunk.chunkMask) + 0.7f, (bi.z & NormalChunk.chunkMask) + 0.9f, POS_Z, bi.getBlock().atlasX, bi.getBlock().atlasY, bi.light, bi.getNeighbors(), vertices, normals, faces, lighting, texture, renderIndices, renderIndex);
			renderIndex++;
		}
		if((data & 0b1000) != 0) {
			model.addToChunkMeshRotation((bi.x & NormalChunk.chunkMask) + 0.5f, (bi.y & NormalChunk.chunkMask) + 0.7f, (bi.z & NormalChunk.chunkMask) + 0.1f, NEG_Z, bi.getBlock().atlasX, bi.getBlock().atlasY, bi.light, bi.getNeighbors(), vertices, normals, faces, lighting, texture, renderIndices, renderIndex);
			renderIndex++;
		}
		if((data & 0b10000) != 0) {
			model.addToChunkMeshRotation((bi.x & NormalChunk.chunkMask) + 0.5f, (bi.y & NormalChunk.chunkMask) + 0.5f, (bi.z & NormalChunk.chunkMask) + 0.5f, null, bi.getBlock().atlasX, bi.getBlock().atlasY, bi.light, bi.getNeighbors(), vertices, normals, faces, lighting, texture, renderIndices, renderIndex);
			renderIndex++;
		}
		return renderIndex;
	}
}
