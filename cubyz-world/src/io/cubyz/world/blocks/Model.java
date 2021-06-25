package io.cubyz.world.blocks;

import java.nio.IntBuffer;

import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIVector3D;

import io.cubyz.utils.log.Log;

import static org.lwjgl.assimp.Assimp.*;

/**
 * Stores the vertex data of a block model.
 * TODO: Also handles the hitbox.
 */

public class Model {
	
	private static final int flags = aiProcess_JoinIdenticalVertices | aiProcess_Triangulate;
	
	public static final Model DEFAULT = loadModelFromID("cubyz:block");
	
	
	public final float[] vertices;
	public final float[] textCoords;
	public final float[] normals;
	public final int[] indices;
	
	/**
	 * Create a model from a predefined set of data.
	 * @param positions
	 * @param textCoords
	 * @param normals
	 * @param indices
	 */
	public Model(float[] positions, float[] textCoords, float[] normals, int[] indices) {
		this.vertices = positions;
		this.textCoords = textCoords;
		this.normals = normals;
		this.indices = indices;
	}

	/**
	 * Read a model from an obj file.
	 * @param filePath
	 */
	public static Model loadModelFromFile(String filePath) {
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
		return new Model(vertices, textCoords, normals, indices);
	}

	/**
	 * Read a model from the model ID.
	 * The model ID contains the mod/addon it comes from and the name of the model seperated by :
	 * @param filePath
	 */
	public static Model loadModelFromID(String ID) {
		String[] parts = ID.split(":");
		if(parts.length != 2) {
			Log.warning("Invalid Model ID \""+ID+"\"! Using default model instead.");
			return DEFAULT;
		}
		return loadModelFromFile("assets/"+parts[0]+"/models/"+parts[1]+".obj");
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
