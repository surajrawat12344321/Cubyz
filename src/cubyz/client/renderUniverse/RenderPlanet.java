package cubyz.client.renderUniverse;

import java.util.Arrays;

import org.joml.Matrix3f;
import org.joml.Vector3f;

import cubyz.gui.rendering.Window;
import cubyz.utils.datastructures.BinaryMaxHeap;
import cubyz.utils.datastructures.simple_list.SimpleList;
import cubyz.world.Chunk;
import cubyz.world.ChunkCache;
import cubyz.world.ChunkData;

/**
 * Stores all the chunks and data from the currently loaded planet.
 * On the client there can only be one planet loaded at a time(except for stellar bodies) therefore its all static.
 */

public final class RenderPlanet {
	/**
	 * The chunks on the client are ordered using an array of OctTrees. This allows
	 */
	private static class ChunkOctTreeNode extends ChunkData {
		final ChunkMesh mesh;
		ChunkOctTreeNode[] children = null;
		
		public ChunkOctTreeNode(int wx, int wy, int wz, int resolution) {
			super(null, wx, wy, wz, resolution);
			mesh = new ChunkMesh();
			float priority = (wx - playerPosition.x)*(wx - playerPosition.x)
					+ (wy - playerPosition.y)*(wy - playerPosition.y)
					+ (wz - playerPosition.z)*(wz - playerPosition.z);
			RenderUniverse.universe.generateVisibilityData(null, wx, wy, wz, resolution, -priority/resolution, (visDat) -> {
				if(!mesh.isDead) {
					mesh.visibilityData = visDat;
					updateQueue.add(mesh);
				}
			});
		}
		
		/**
		 * Updates the Tree, by checking on each layer if the resolution should be increased or decreased.
		 * Also selects the meshes that can be rendered.
		 * 
		 * @param renderDistanceSquare
		 * @param shouldDraw
		 */
		public void update(float renderDistanceSquare, boolean shouldDraw) {
			if(resolution == 1) {
				if(shouldDraw) {
					swapMeshes.add(mesh);
				}
				return;
			}
			// Check if there is room for children:
			float dx = Math.abs(wx + Chunk.CHUNK_WIDTH*resolution/2 - playerPosition.x);
			float dy = Math.abs(wy + Chunk.CHUNK_WIDTH*resolution/2 - playerPosition.y);
			float dz = Math.abs(wz + Chunk.CHUNK_WIDTH*resolution/2 - playerPosition.z);
			float nextRenderDistanceSquare = renderDistanceSquare/lodFactor/lodFactor;
			if(dx*dx + dy*dy + dz*dz < nextRenderDistanceSquare) {
				if(children == null) { // Load higher detail chunks.
					children = new ChunkOctTreeNode[8];
					for(int x = 0; x <= 1; x++) {
						for(int y = 0; y <= 1; y++) {
							for(int z = 0; z <= 1; z++) {
								children[x*2 + y + z*4] = new ChunkOctTreeNode(
										wx + x*Chunk.CHUNK_WIDTH*resolution/2,
										wy + y*Chunk.CHUNK_WIDTH*resolution/2,
										wz + z*Chunk.CHUNK_WIDTH*resolution/2,
										resolution/2);
							}
						}
					}
				}
				// Check if the children can be drawn:
				if(shouldDraw) {
					for(ChunkOctTreeNode node : children) {
						if(node.mesh.needsUpdate) {
							shouldDraw = false;
							break;
						}
					}
					// If the children cannot be drawn, draw itself:
					if(!shouldDraw) {
						swapMeshes.add(mesh);
					}
				}
				for(int i = 0; i < 8; i++) {
					children[i].update(nextRenderDistanceSquare, shouldDraw);
				}
			} else {
				if(children != null) { // Unload higher detail chunks.
					for(int i = 0; i < 8; i++) {
						children[i].clean();
					}
					children = null;
				}
				if(shouldDraw) {
					swapMeshes.add(mesh);
				}
			}
		}
		
		public void clean() {
			if(children != null) {
				for(int i = 0; i < 8; i++) {
					children[i].clean();
				}
			}
			synchronized(mesh) {
				mesh.isDead = true;
				cleanupQueue.add(mesh);
			}
			children = null;
		}
	}
	/** Maximum time (in ns) the meshing phase is allowed to steal from the render thread. */
	public static final long MAX_TIME_FOR_CHUNK_MESHING = 2000000;
	public static final int MAX_LOD_SHIFT = 4;
	public static final int MAX_LOD = 1 << MAX_LOD_SHIFT;
	/** Width of the highest LOD chunk. */
	public static final int MAX_LOD_WIDTH = MAX_LOD*Chunk.CHUNK_WIDTH;
	public static final int MAX_LOD_WIDTH_MASK = MAX_LOD_WIDTH - 1;
	/** Contains all meshes that need a mesh update. */
	public static final BinaryMaxHeap<ChunkMesh> updateQueue = new BinaryMaxHeap<ChunkMesh>();
	/** Contains all meshes that should be cleaned up. */
	private static final BinaryMaxHeap<ChunkMesh> cleanupQueue = new BinaryMaxHeap<ChunkMesh>();
	
	/** Contains all the root nodes for the OctTrees. */
	private static ChunkOctTreeNode[] rootNodes = new ChunkOctTreeNode[0];
	private static ChunkOctTreeNode[] swapNodes = new ChunkOctTreeNode[0];
	private static SimpleList<ChunkMesh> renderMeshes = new SimpleList<ChunkMesh>(new ChunkMesh[16]);
	private static SimpleList<ChunkMesh> swapMeshes = new SimpleList<ChunkMesh>(new ChunkMesh[16]);
	private static int renderDistance = 0;
	private static int dataStructureRadius = 0;
	private static float lodFactor = 2;
	private static final Vector3f playerPosition = new Vector3f();
	
	private static void updateRenderDistance() {
		dataStructureRadius = 1 + (int)Math.ceil(renderDistance/(float)MAX_LOD_WIDTH);
		int newSize = dataStructureRadius*dataStructureRadius*dataStructureRadius*8;
		if(newSize != swapNodes.length)
			swapNodes = new ChunkOctTreeNode[newSize];
		updatePosition();
		if(newSize != swapNodes.length)
			swapNodes = new ChunkOctTreeNode[newSize];
	}
	
	private static void updatePosition() {
		int renderDistanceSquare = renderDistance*renderDistance;
		// Copy the old nodes:
		for(ChunkOctTreeNode node : rootNodes) {
			if(node == null) continue;
			// Check if it is still within render distance:
			int dx = node.wx + MAX_LOD_WIDTH/2 - (int)playerPosition.x;
			int dy = node.wy + MAX_LOD_WIDTH/2 - (int)playerPosition.y;
			int dz = node.wz + MAX_LOD_WIDTH/2 - (int)playerPosition.z;
			if(dx*dx + dy*dy + dz*dz < renderDistanceSquare) {
				// Get the index in the data structure:
				dx = (node.wx)/MAX_LOD_WIDTH - (int)playerPosition.x/MAX_LOD_WIDTH;
				dy = (node.wy)/MAX_LOD_WIDTH - (int)playerPosition.y/MAX_LOD_WIDTH;
				dz = (node.wz)/MAX_LOD_WIDTH - (int)playerPosition.z/MAX_LOD_WIDTH;
				int x = dx + dataStructureRadius;
				int y = dy + dataStructureRadius;
				int z = dz + dataStructureRadius;
				int index = x*dataStructureRadius + y + z*dataStructureRadius*dataStructureRadius;
				if(index >= 0 && index < swapNodes.length) { // <- just one last sanity check to prevent unpleasant surprises.
					swapNodes[index] = node;
					continue;
				}
			}
			// Clean it if it wasn't added:
			node.clean();
		}
		// Check if there are any unexpected holes in the data structure and fill them.
		// Also update all nodes.
		for(int x = 0; x < 2*dataStructureRadius; x++) {
			for(int z = 0; z < 2*dataStructureRadius; z++) {
				for(int y = 0; y < 2*dataStructureRadius; y++) {
					int wx = (x - dataStructureRadius + (int)playerPosition.x/MAX_LOD_WIDTH)*MAX_LOD_WIDTH;
					int wy = (y - dataStructureRadius + (int)playerPosition.y/MAX_LOD_WIDTH)*MAX_LOD_WIDTH;
					int wz = (z - dataStructureRadius + (int)playerPosition.z/MAX_LOD_WIDTH)*MAX_LOD_WIDTH;
					int dx = wx + MAX_LOD_WIDTH/2 - (int)playerPosition.x;
					int dy = wy + MAX_LOD_WIDTH/2 - (int)playerPosition.y;
					int dz = wz + MAX_LOD_WIDTH/2 - (int)playerPosition.z;
					if(dx*dx + dy*dy + dz*dz < renderDistanceSquare) {
						int index = x*dataStructureRadius + y + z*dataStructureRadius*dataStructureRadius;
						if(swapNodes[index] == null) {
							swapNodes[index] = new ChunkOctTreeNode(wx, wy, wz, MAX_LOD);
						}
						swapNodes[index].update(renderDistanceSquare, !swapNodes[index].mesh.needsUpdate);
					}
				}
			}
		}
		
		// Wait for the render Thread to finish rendering and swap the mesh lists:
		synchronized(renderMeshes) {
			SimpleList<ChunkMesh> local = renderMeshes;
			renderMeshes = swapMeshes;
			swapMeshes = local;
			ChunkOctTreeNode[] local2 = rootNodes;
			rootNodes = swapNodes;
			swapNodes = local2;
		}
		
		// Empty the data structures when done:
		swapMeshes.clearFully();
		Arrays.fill(swapNodes, null);
	}
	
	public static void update(Vector3f playerPosition, int renderDistance, float lodFactor) {
		RenderPlanet.playerPosition.set(playerPosition);
		RenderPlanet.lodFactor = lodFactor;
		if(renderDistance != RenderPlanet.renderDistance) {
			RenderPlanet.renderDistance = renderDistance;
			updateRenderDistance();
		} else {
			updatePosition();
		}
	}
	
	// Some stuff for testing:
	static Vector3f cameraPos = new Vector3f(0, 64, 148);
	
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
		rotation = new Matrix3f().identity().rotateX(0.2f).rotateY(((System.currentTimeMillis() & 65535)/10000.0f));
		update(camera, 5000, 2.0f); // TODO: Do that in an extra Thread.
		
		synchronized(renderMeshes) {
			ChunkMesh.bind(Window.projectionMatrix, rotation);
			for(int i = 0; i < renderMeshes.size; i++) {
				renderMeshes.array[i].render(camera);
			}
			ChunkMesh.unbind();
			ChunkMesh.bindTransparent(Window.projectionMatrix, rotation);
			for(int i = 0; i < renderMeshes.size; i++) {
				renderMeshes.array[i].renderTransparent(camera);
			}
			ChunkMesh.unbind();
		}
		while(cleanupQueue.notEmpty()) {
			cleanupQueue.extractMax().cleanup();
		}
		System.out.println("chunk misses: "+ChunkCache.chunkCache.cacheMisses+"/"+ChunkCache.chunkCache.cacheRequests);
		System.out.println("visibility misses: "+ChunkCache.visibilityDataCache.cacheMisses+"/"+ChunkCache.visibilityDataCache.cacheRequests);
	}
}
