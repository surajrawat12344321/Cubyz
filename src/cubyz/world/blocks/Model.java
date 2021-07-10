package cubyz.world.blocks;

import java.nio.IntBuffer;
import java.util.HashMap;

import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIVector3D;

import cubyz.utils.Utils;
import cubyz.utils.log.Log;

import static org.lwjgl.assimp.Assimp.*;

/**
 * Stores the vertex data of a block model.
 * TODO: Also handles the hitbox.
 */

public class Model {
	
	private static final int flags = aiProcess_JoinIdenticalVertices | aiProcess_Triangulate;
	
	private static final HashMap<String, Model> loadedModels = new HashMap<>();
	
	public static final Model DEFAULT;
	
	
	public final float[] vertices;
	public final float[] textCoords;
	public final float[] normals;
	public final int[] indices;
	
	/**
	 * True if the model is a cube filling the entire block. This variable is used to cull unused faces when creating the chunk mesh and to determine whether a block is visible at all.
	 * This value will be determined automatically in the constructor to prevent misuse.
	 */
	public final boolean isCube;
	
	/**
	 * Create a model from a predefined set of data.
	 * @param positions
	 * @param textCoords
	 * @param normals
	 * @param indices
	 */
	public Model(float[] positions, float[] textCoords, float[] normals, int[] indices) {
		this(positions, textCoords, normals, indices, false);
	}
	
	/**
	 * For cube models only. A lot can go wrong if used incorrectly, so it's private. Hooray!
	 * @param positions
	 * @param textCoords
	 * @param normals
	 * @param indices
	 * @param isCube
	 */
	private Model(float[] positions, float[] textCoords, float[] normals, int[] indices, boolean isCube) {
		this.vertices = positions;
		this.textCoords = textCoords;
		this.normals = normals;
		this.indices = indices;
		this.isCube = isCube;
	}
	
	static {
		// Load the base cubes.
		// WARNING! WHENEVER YOU ADD A NEW MODEL MAKE SURE THE FOLLOWING CONDITIONS ARE MET!
		// 1. There are EXACTLY 4 vertices and 2 faces per side.
		// 2. The faces and the vertices are SORTED by their `Neighbor` index.
		Model block = loadModelFromFile("assets/.cubyz/WARNING/CHANGING_THINGS_HERE_MIGHT_BREAK_THE_GAME/cube_models/block.obj", true);
		loadedModels.put("cubyz:block", block);
		block = loadModelFromFile("assets/.cubyz/WARNING/CHANGING_THINGS_HERE_MIGHT_BREAK_THE_GAME/cube_models/block_log.obj", true);
		loadedModels.put("cubyz:block_log", block);
		DEFAULT = loadModelFromID("cubyz:block");
	}

	/**
	 * Read a model from an obj file. The model might be a cube model.
	 * @param filePath
	 * @param isCube
	 */
	private static Model loadModelFromFile(String filePath, boolean isCube) {
		AIScene aiScene = aiImportFile(filePath, flags);
		if(aiScene == null) {
			Log.warning("Couldn't find model in path: "+filePath+"! Using default model instead.");
			if(DEFAULT == null) {
				Log.severe("Couldn't find the default model! Using empty model instead.");
				return new Model(new float[0], new float[0], new float[0], new int[0]);
			}
			return DEFAULT;
		}
		PointerBuffer aiMeshes = aiScene.mMeshes();
		AIMesh aiMesh = AIMesh.create(aiMeshes.get());
		float[] vertices = process(aiMesh.mVertices());
		float[] textCoords = processTextCoords(aiMesh);
		float[] normals = process(aiMesh.mNormals());
		int[] indices = processIndices(aiMesh);
		return new Model(vertices, textCoords, normals, indices, isCube);
	}

	/**
	 * Read a model from an obj file.
	 * @param filePath
	 */
	public static Model loadModelFromFile(String filePath) {
		return loadModelFromFile(filePath, false);
	}

	/**
	 * Read a model from the model ID.
	 * The model ID contains the mod/addon it comes from and the name of the model seperated by :
	 * @param filePath
	 */
	public static Model loadModelFromID(String ID) {
		Model model = loadedModels.get(ID); // Don't load models multiple times.
		if(model == null) {
			model = loadModelFromFile(Utils.idToFile(ID, "models", ".obj"));
			loadedModels.put(ID, model);
		}
		return model;
	}
	
	// Some functions to help extract the data from lwjgl Assimp:

	private static float[] process(AIVector3D.Buffer buffer) {
		float[] result = new float[buffer.limit() * 3];
		buffer.rewind();
		for (int i = 0; i < buffer.limit(); i++) {
			AIVector3D aiVector = buffer.get();
			int j = i * 3;
			result[j] = aiVector.x();
			result[j + 1] = aiVector.y();
			result[j + 2] = aiVector.z();
		}
		return result;
	}

	private static float[] processTextCoords(AIMesh aiMesh) {
		AIVector3D.Buffer aiTextureCoords = aiMesh.mTextureCoords(0);
		float[] textureCoords = new float[aiTextureCoords.limit() * 2];
		aiTextureCoords.rewind();
		for (int i = 0; i < aiTextureCoords.limit(); i++) {
			AIVector3D textureCoord = aiTextureCoords.get();
			int j = i * 2;
			textureCoords[j] = textureCoord.x();
			textureCoords[j + 1] = 1 - textureCoord.y(); // The y coordinate needs to be inverted for some reason.
		}
		return textureCoords;
	}

	private static int[] processIndices(AIMesh aiMesh) {
		int numFaces = aiMesh.mNumFaces();
		int[] indices = new int[numFaces*3];
		AIFace.Buffer aiFaces = aiMesh.mFaces();
		for (int i = 0; i < numFaces; i++) {
			AIFace aiFace = aiFaces.get(i);
			IntBuffer buffer = aiFace.mIndices();
			int j = i * 3;
			// The faces are always triangle(see `flags`).
			indices[j] = buffer.get(0);
			indices[j + 1] = buffer.get(1);
			indices[j + 2] = buffer.get(2);
		}
		return indices;
	}
}
