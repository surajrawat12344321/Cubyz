package cubyz.world;

import java.util.UUID;
import java.util.function.Consumer;

import cubyz.utils.datastructures.BinaryMaxHeap;

/**
 * Handles all Worlds of this Universe.
 * TODO
 */

public class Universe extends UniverseInterface {
	public World[] loadedWorlds = new World[1];
	public final Thread generationThread;
	public boolean running = true;
	
	public BinaryMaxHeap<ChunkGenerationRequest> chunkRequests = new BinaryMaxHeap<ChunkGenerationRequest>();
	
	
	public Universe() {
		loadedWorlds[0] = new World();
		generationThread = new Thread(() -> {
			while(running) {
				if(chunkRequests.notEmpty()) {
					ChunkGenerationRequest request = chunkRequests.extractMax();
					request.callback.accept(ChunkCache.getOrGenerateVisibilityData(request));
				} else {
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		generationThread.start();
	}
	private static final class ChunkGenerationRequest extends ChunkData implements Comparable<ChunkGenerationRequest> {
		final float priority;
		final Consumer<ChunkVisibilityData> callback;
		ChunkGenerationRequest(World world, int wx, int wy, int wz, int resolution, float priority, Consumer<ChunkVisibilityData> callback) {
			super(world, wx, wy, wz, resolution);
			this.priority = priority;
			this.callback = callback;
		}
		@Override
		public int compareTo(ChunkGenerationRequest o) {
			return priority < o.priority ? -1 : 1;
		}
	}

	@Override
	public void breakBlock(UUID playerUuid, int positionX, int positionY, int positionZ) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void generateVisibilityData(UUID playerUuid, int x, int y, int z, int resolution, float priority, Consumer<ChunkVisibilityData> callback) {
		// TODO: Divide the player priority by their average or something, to prevent abuse.
		ChunkGenerationRequest request = new ChunkGenerationRequest(loadedWorlds[0], x, y, z, resolution, priority, callback);
		// Test if it is already cached:
		ChunkVisibilityData dat = ChunkCache.getOrGenerateVisibilityData(request);
		if(dat != null) {
			callback.accept(dat);
		} else {
			chunkRequests.add(request);
		}
	}

}
