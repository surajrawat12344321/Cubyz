package io.cubyz.renderUniverse;

import org.joml.Matrix3f;
import org.joml.Vector3f;

import io.cubyz.gui.rendering.Window;
import io.cubyz.world.Chunk;
import io.cubyz.world.ChunkVisibilityData;
import io.cubyz.world.Neighbor;
import io.cubyz.world.UniverseInterface;

/**
 * 
 * Renders the Universe
 *
 */
public abstract class RenderUniverse {
	public static  UniverseInterface universe;
	
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
		mesh = new ChunkMesh(visDat);
		
	}

	//data comes from Game.connection
	public static void draw() {
		//if(universe==null)
		//	return;
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
