package io.cubyz.world.blocks;

import java.nio.IntBuffer;

import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIVector3D;

import static org.lwjgl.assimp.Assimp.*;

/**
 * Stores the vertex data of a block model.
 * TODO: Also handles the hitbox.
 */

public class Model {
	
	private static final int flags = aiProcess_JoinIdenticalVertices | aiProcess_Triangulate;
	
	
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
	public Model(String filePath) {
		AIScene aiScene = aiImportFile(filePath, flags);
		PointerBuffer aiMeshes = aiScene.mMeshes();
		AIMesh aiMesh = AIMesh.create(aiMeshes.get());
		vertices = process(aiMesh.mVertices());
		textCoords = processTextCoords(aiMesh);
		normals = process(aiMesh.mNormals());
		indices = processIndices(aiMesh);
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
