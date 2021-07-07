package io.cubyz.renderUniverse;

import org.joml.Matrix3f;
import org.joml.Vector3f;

import io.cubyz.gui.rendering.Window;
import io.cubyz.utils.datastructures.BinaryMaxHeap;
import io.cubyz.world.Chunk;
import io.cubyz.world.ChunkVisibilityData;
import io.cubyz.world.Neighbor;

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
	static Vector3f cameraPos = new Vector3f(0, 48, 48);
	static Chunk visible = new Chunk(0, 0, 0, 1);
	static Chunk[] neighbors = new Chunk[6];
	static ChunkVisibilityData visDat;
	static ChunkMesh mesh;
	static {
		for(int i = 0; i < Neighbor.NEIGHBORS; i++) {
			neighbors[i] = new Chunk(visible.wx + Neighbor.REL_X[i]*Chunk.CHUNK_WIDTH, visible.wy + Neighbor.REL_Y[i]*Chunk.CHUNK_WIDTH, visible.wz + Neighbor.REL_Z[i]*Chunk.CHUNK_WIDTH, 1);
		}
		visDat = new ChunkVisibilityData(visible, neighbors);
		mesh = new ChunkMesh(visDat, 1);
		updateQueue.add(mesh);
		
	}
	
	public static void render() {
		// Create the chunk meshes:
		long endMeshing = System.nanoTime() + MAX_TIME_FOR_CHUNK_MESHING;
		while(updateQueue.notEmpty() && endMeshing > System.nanoTime()) {
			updateQueue.extractMax().generateMesh();
		}
		
		// Render the chunks:
		Matrix3f rotation = new Matrix3f().identity().rotateY(-((System.currentTimeMillis() & 65535)/10000.0f));
		Vector3f camera = cameraPos.mul(rotation, new Vector3f());
		rotation = new Matrix3f().identity().rotateX(1.0f).rotateY(((System.currentTimeMillis() & 65535)/10000.0f));

		ChunkMesh.bind(Window.projectionMatrix, rotation);
		mesh.render(camera);
		ChunkMesh.unbind();
		ChunkMesh.bindTransparent(Window.projectionMatrix, rotation);
		mesh.renderTransparent(camera);
		ChunkMesh.unbind();
	}
}
