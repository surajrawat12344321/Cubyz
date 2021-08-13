package cubyz.world;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.Consumer;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import cubyz.utils.FileUtils;
import cubyz.utils.datastructures.BinaryMaxHeap;
import cubyz.utils.log.Log;

/**
 * Handles all Worlds of this Universe.
 * TODO
 */

public class World extends WorldInterface {
	public final long seed = 6487396473896L;

	public final Thread generationThread;
	public boolean running = true;
	
	public String savePath = "saves/world_test";
	
	public BinaryMaxHeap<ChunkGenerationRequest> chunkRequests = new BinaryMaxHeap<ChunkGenerationRequest>();
	
	HashMap<UUID, Player> onlinePlayers = new HashMap<UUID, Player>();	
	
	public World() {
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
		ChunkGenerationRequest request = new ChunkGenerationRequest(this, x, y, z, resolution, priority, callback);
		// Test if it is already cached:
		ChunkVisibilityData dat = ChunkCache.getOrNull(request);
		if(dat != null) {
			callback.accept(dat);
		} else {
			chunkRequests.add(request);
		}
	}

	@Override
	public boolean authenticate(UUID playerUuid, char[] passphrase, String username) {
		if(!checkPassphrase(playerUuid, passphrase)) return false;
		// TODO: Find player in the world.
		return true;
	}
	
	public boolean checkPassphrase(UUID playerUuid, char[] passphrase) {
		// TODO: IP whitelist/blacklist
		// Password shouldn't be too short.
		if(passphrase.length < 32) return false;
		// Check the hash if the player was already on the server:
		File playerData = new File(savePath+"/auth/"+playerUuid.toString());
		try {
			if(playerData.exists()) {
				String[] data = FileUtils.readFile(playerData).split("\n");
				// Compute the hash:
				Base64.Decoder dec = Base64.getDecoder();
				byte[] salt = dec.decode(data[0]);
				KeySpec spec = new PBEKeySpec(passphrase, salt, 65536, 128);
				SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
				byte[] hash = f.generateSecret(spec).getEncoded();
				byte[] expectedHash = dec.decode(data[1]);
				return Arrays.equals(hash, expectedHash);
			}
			// Compute the hash and store it:
			SecureRandom random = new SecureRandom();
			byte[] salt = new byte[32];
			random.nextBytes(salt);
			KeySpec spec = new PBEKeySpec(passphrase, salt, 65536, 128);
			SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
			byte[] hash = f.generateSecret(spec).getEncoded();
			Base64.Encoder enc = Base64.getEncoder();
			StringBuilder storage = new StringBuilder();
			storage.append(enc.encodeToString(salt));
			storage.append('\n');
			storage.append(enc.encodeToString(hash));
			storage.append('\n');
			FileUtils.writeFile(playerData, storage.toString());
		} catch(NoSuchAlgorithmException | InvalidKeySpecException e) {
			Log.severe("Authentication failed!");
			Log.severe(e);
			return false;
		}
		return true;
	}

}
