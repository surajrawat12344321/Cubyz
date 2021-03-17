package io.cubyz.blocks.rotation;

import org.joml.RayAabIntersection;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector4f;

import io.cubyz.api.Resource;
import io.cubyz.blocks.Block;
import io.cubyz.blocks.BlockInstance;
import io.cubyz.client.Meshes;
import io.cubyz.entity.Entity;
import io.cubyz.util.ByteWrapper;
import io.cubyz.util.FloatFastList;
import io.cubyz.util.IntFastList;
import io.cubyz.world.NormalChunk;
import io.cubyz.world.Surface;

/**
 * The default RotationMode that places the block in the grid without translation or rotation.
 */

public class ClientNoRotation extends NoRotation implements ClientRotationMode {

	@Override
	public float getRayIntersection(RayAabIntersection arg0, BlockInstance arg1, Vector3f min, Vector3f max, Vector3f transformedPosition) {
		return 0;
	}
	
	@Override
	public int generateChunkMesh(BlockInstance bi, FloatFastList vertices, FloatFastList normals, IntFastList faces, IntFastList lighting, FloatFastList texture, IntFastList renderIndices, int renderIndex) {
		Meshes.blockMeshes.get(bi.getBlock()).model.addToChunkMesh(bi.x & NormalChunk.chunkMask, bi.y & NormalChunk.chunkMask, bi.z & NormalChunk.chunkMask, bi.getBlock().atlasX, bi.getBlock().atlasY, bi.light, bi.getNeighbors(), vertices, normals, faces, lighting, texture, renderIndices, renderIndex);
		return renderIndex + 1;
	}
}
