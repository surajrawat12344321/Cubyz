package io.cubyz.renderUniverse;

import org.joml.Matrix3f;
import org.joml.Vector3f;

import io.cubyz.gui.rendering.Window;
import io.cubyz.utils.datastructures.BinaryMaxHeap;
import io.cubyz.world.Chunk;
import io.cubyz.world.ChunkCache;
import io.cubyz.world.ChunkVisibilityData;
import io.cubyz.world.World;

/**
 * Stores all the chunks and data from the currently loaded planet.
 * On the client there can only be one planet loaded at a time(except for stellar bodies) therefore its all static.
 */

public final class RenderPlanet {
	/** Maximum time the meshing phase is allowed to steal from the render thread. */
	public static final long MAX_TIME_FOR_CHUNK_MESHING = 2000000;
	/** Contains all meshes that need a mesh update. */
	public static final BinaryMaxHeap<ChunkMesh> updateQueue = new BinaryMaxHeap<ChunkMesh>();
	
	// Some stuff for testing:
	static Vector3f cameraPos = new Vector3f(0, 148, 148);
	static ChunkVisibilityData[] visDat;
	static ChunkMesh[] meshes;
	static {
		World world = new World();
		visDat = new ChunkVisibilityData[8*8*8];
		meshes = new ChunkMesh[visDat.length];
		for(int x = 0; x < 8; x++) {
			for(int y = 0; y < 8; y++) {
				for(int z = 0; z < 8; z++) {
					visDat[x + y*8 + z*64] = ChunkCache.getVisibilityData(world, Chunk.CHUNK_WIDTH*(x-4), Chunk.CHUNK_WIDTH*(y-4), Chunk.CHUNK_WIDTH*(z-4), 1);
					meshes[x + y*8 + z*64] = new ChunkMesh(visDat[x + y*8 + z*64], -(x-4)*(x-4) - (y-4)*(y-4) - (z-4)*(z-4));
					updateQueue.add(meshes[x + y*8 + z*64]);
				}
			}
		}
		System.out.println("Cache misses: "+ChunkCache.cacheMisses+"/"+ChunkCache.cacheRequests);
	}
	
	public static void render() {
		// Create the chunk meshes:
		long endMeshing = System.nanoTime() + MAX_TIME_FOR_CHUNK_MESHING;
		while(updateQueue.notEmpty() && endMeshing > System.nanoTime()) {
			ChunkMesh max = updateQueue.extractMax();
			max.generateMesh();
		}
		
		// Render the chunks:
		Matrix3f rotation = new Matrix3f().identity().rotateY(-((System.currentTimeMillis() & 65535)/10000.0f));
		Vector3f camera = cameraPos.mul(rotation, new Vector3f());
		rotation = new Matrix3f().identity().rotateX(1.0f).rotateY(((System.currentTimeMillis() & 65535)/10000.0f));

		ChunkMesh.bind(Window.projectionMatrix, rotation);
		for(ChunkMesh mesh : meshes) {
			mesh.render(camera);
		}
		ChunkMesh.unbind();
		ChunkMesh.bindTransparent(Window.projectionMatrix, rotation);
		for(ChunkMesh mesh : meshes) {
			mesh.renderTransparent(camera);
		}
		ChunkMesh.unbind();
	}
}
