package io.cubyz.blocks.rotation;

import org.joml.RayAabIntersection;
import org.joml.Vector3f;

import io.cubyz.blocks.BlockInstance;
import io.cubyz.blocks.RotationMode;
import io.cubyz.util.FloatFastList;
import io.cubyz.util.IntFastList;

public interface ClientRotationMode extends RotationMode {
	
	/**
	 * Called when generating the chunk mesh.
	 * @param bi
	 * @param vertices
	 * @param normals
	 * @param faces
	 * @param lighting
	 * @param texture
	 * @param renderIndex
	 * @return incremented renderIndex
	 */
	public int generateChunkMesh(BlockInstance bi, FloatFastList vertices, FloatFastList normals, IntFastList faces, IntFastList lighting, FloatFastList texture, IntFastList renderIndices, int renderIndex);
	
	/**
	 * 
	 * @param intersection
	 * @param bi
	 * @param min minimal point of the surrounding block. May be overwritten.
	 * @param max maximal point of the surrounding block. May be overwritten.
	 * @return
	 */
	public float getRayIntersection(RayAabIntersection intersection, BlockInstance bi, Vector3f min, Vector3f max, Vector3f transformedPosition);

}
