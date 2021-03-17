package io.cubyz.blocks.rotation;

import org.joml.RayAabIntersection;
import org.joml.Vector3f;

import io.cubyz.CubyzLogger;
import io.cubyz.blocks.BlockInstance;
import io.cubyz.client.Meshes;
import io.cubyz.models.CubeModel;
import io.cubyz.models.Model;
import io.cubyz.util.FloatFastList;
import io.cubyz.util.IntFastList;
import io.cubyz.world.NormalChunk;

/**
 * For stackable partial blocks, like snow.
 */

public class ClientStackableRotation extends StackableRotation implements ClientRotationMode {

	@Override
	public float getRayIntersection(RayAabIntersection intersection, BlockInstance bi, Vector3f min, Vector3f max, Vector3f transformedPosition) {
		max.add(0, bi.getData()/16.0f - 1.0f, 0);
		// Because of the huge number of different BlockInstances that will be tested, it is more efficient to use RayAabIntersection and determine the distance seperately:
		if (intersection.test(min.x, min.y, min.z, max.x, max.y, max.z)) {
			return min.add(0.5f, bi.getData()/32.0f, 0.5f).sub(transformedPosition).length();
		} else {
			return Float.MAX_VALUE;
		}
	}
	
	@Override
	public int generateChunkMesh(BlockInstance bi, FloatFastList vertices, FloatFastList normals, IntFastList faces, IntFastList lighting, FloatFastList texture, IntFastList renderIndices, int renderIndex) {
		Model model = Meshes.blockMeshes.get(bi.getBlock()).model;
		if(!(model instanceof CubeModel)) {
			CubyzLogger.logger.severe("Unsupported model "+model.getRegistryID()+" in block "+bi.getBlock().getRegistryID()+" for stackable block type. Skipping block.");
			return renderIndex;
		}
		int x = bi.getX() & NormalChunk.chunkMask;
		int y = bi.getY() & NormalChunk.chunkMask;
		int z = bi.getZ() & NormalChunk.chunkMask;
		boolean[] neighbors = bi.getNeighbors();
		int[] light = bi.light;
		int offsetX = bi.getBlock().atlasX;
		int offsetY = bi.getBlock().atlasY;
		
		// Copies code from CubeModel and applies height transformation to it:
		int indexOffset = vertices.size/3;
		int size = model.positions.length/3;
		float factor = bi.getData()/16.0f;
		IntFastList indexesAdded = new IntFastList(24);
		for(int i = 0; i < size; i++) {
			int i2 = i*2;
			int i3 = i*3;
			float nx = model.normals[i3];
			float ny = model.normals[i3+1];
			float nz = model.normals[i3+2];
			if(nx == -1 && neighbors[0] ||
			   nx == 1 && neighbors[1] ||
			   nz == -1 && neighbors[2] ||
			   nz == 1 && neighbors[3] ||
			   ny == -1 && (neighbors[4] || factor == 1) ||
			   ny == 1 && neighbors[5]) {
				vertices.add(model.positions[i3] + x);
				if(ny != -1)
					vertices.add(model.positions[i3+1]*factor + y);
				else
					vertices.add(model.positions[i3+1] + y);
				vertices.add(model.positions[i3+2] + z);
				normals.add(nx);
				normals.add(ny);
				normals.add(nz);
				
				lighting.add(Model.interpolateLight(model.positions[i3], ny != -1 ? model.positions[i3+1]*factor : model.positions[i3+1], model.positions[i3+2], model.normals[i3], model.normals[i3+1], model.normals[i3+2], light));
				renderIndices.add(renderIndex);

				texture.add((model.textCoords[i2] + offsetX)/Meshes.atlasSize);
				if(ny == 0)
					texture.add((model.textCoords[i2+1]*factor + offsetY)/Meshes.atlasSize);
				else
					texture.add((model.textCoords[i2+1] + offsetY)/Meshes.atlasSize);
				indexesAdded.add(i);
			}
		}
		
		for(int i = 0; i < model.indices.length; i += 3) {
			if(indexesAdded.contains(model.indices[i]) && indexesAdded.contains(model.indices[i+1]) && indexesAdded.contains(model.indices[i+2])) {
				faces.add(indexesAdded.indexOf(model.indices[i]) + indexOffset, indexesAdded.indexOf(model.indices[i+1]) + indexOffset, indexesAdded.indexOf(model.indices[i+2]) + indexOffset);
			}
		}
		return renderIndex + 1;
	}
}
