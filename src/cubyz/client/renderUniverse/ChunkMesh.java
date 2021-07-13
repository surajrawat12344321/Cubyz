package cubyz.client.renderUniverse;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_SRC_COLOR;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDepthMask;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL14.GL_FUNC_REVERSE_SUBTRACT;
import static org.lwjgl.opengl.GL14.GL_MAX;
import static org.lwjgl.opengl.GL14.glBlendFuncSeparate;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glBlendEquationSeparate;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUniformMatrix3fv;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import cubyz.client.meshes.BlockMeshes;
import cubyz.gui.rendering.Shader;
import cubyz.utils.datastructures.simple_list.FloatSimpleList;
import cubyz.utils.datastructures.simple_list.IntSimpleList;
import cubyz.world.ChunkVisibilityData;
import cubyz.world.Neighbor;
import cubyz.world.blocks.Blocks;
import cubyz.world.blocks.Model;

public class ChunkMesh implements Comparable<ChunkMesh> {
	// ThreadLocal lists, to prevent (re-)allocating tons of memory.
	public static final ThreadLocal<FloatSimpleList> localVertices = new ThreadLocal<FloatSimpleList>() {
		@Override
		protected FloatSimpleList initialValue() {
			return new FloatSimpleList(50000);
		}
	};
	public static final ThreadLocal<FloatSimpleList> localNormals = new ThreadLocal<FloatSimpleList>() {
		@Override
		protected FloatSimpleList initialValue() {
			return new FloatSimpleList(50000);
		}
	};
	public static final ThreadLocal<IntSimpleList> localFaces = new ThreadLocal<IntSimpleList>() {
		@Override
		protected IntSimpleList initialValue() {
			return new IntSimpleList(30000);
		}
	};
	public static final ThreadLocal<FloatSimpleList> localTextures = new ThreadLocal<FloatSimpleList>() {
		@Override
		protected FloatSimpleList initialValue() {
			return new FloatSimpleList(40000);
		}
	};
	public static final ThreadLocal<FloatSimpleList> localTransparentVertices = new ThreadLocal<FloatSimpleList>() {
		@Override
		protected FloatSimpleList initialValue() {
			return new FloatSimpleList(50000);
		}
	};
	public static final ThreadLocal<FloatSimpleList> localTransparentNormals = new ThreadLocal<FloatSimpleList>() {
		@Override
		protected FloatSimpleList initialValue() {
			return new FloatSimpleList(50000);
		}
	};
	public static final ThreadLocal<IntSimpleList> localTransparentFaces = new ThreadLocal<IntSimpleList>() {
		@Override
		protected IntSimpleList initialValue() {
			return new IntSimpleList(30000);
		}
	};
	public static final ThreadLocal<FloatSimpleList> localTransparentTextures = new ThreadLocal<FloatSimpleList>() {
		@Override
		protected FloatSimpleList initialValue() {
			return new FloatSimpleList(40000);
		}
	};

	public static final Shader SHADER = Shader.loadFromFile("assets/cubyz/shaders/chunk/chunk.vs", "assets/cubyz/shaders/chunk/chunk.fs");
	public static final Shader SHADER_TRANSPARENT = Shader.loadFromFile("assets/cubyz/shaders/chunk/transparent.vs", "assets/cubyz/shaders/chunk/transparent.fs");
	public static final int UNIFORM_PLAYER = SHADER.getUniformLocation("relativePlayerPos");
	public static final int UNIFORM_ROT_MAT = SHADER.getUniformLocation("rotationMatrix");
	public static final int UNIFORM_PROJ_MAT = SHADER.getUniformLocation("projectionMatrix");
	public static final int UNIFORM_TEXTURE = SHADER.getUniformLocation("texture_sampler");
	public static final int UNIFORM_ATLAS_SIZE = SHADER.getUniformLocation("atlasSize");
	public static final int UNIFORM_TRANSPARENT_PLAYER = SHADER_TRANSPARENT.getUniformLocation("relativePlayerPos");
	public static final int UNIFORM_TRANSPARENT_ROT_MAT = SHADER_TRANSPARENT.getUniformLocation("rotationMatrix");
	public static final int UNIFORM_TRANSPARENT_PROJ_MAT = SHADER_TRANSPARENT.getUniformLocation("projectionMatrix");
	public static final int UNIFORM_TRANSPARENT_TEXTURE = SHADER_TRANSPARENT.getUniformLocation("texture_sampler");
	public static final int UNIFORM_TRANSPARENT_ATLAS_SIZE = SHADER_TRANSPARENT.getUniformLocation("atlasSize");
	public static final FloatBuffer ROT_MAT_BUFFER = FloatBuffer.allocate(9);
	public static final FloatBuffer PROJ_MAT_BUFFER = FloatBuffer.allocate(16);
	
	/**
	 * Binds all the stuff needed by the Chunk Mesh such as Shader and texture atlas.
	 * Also initializes all teh uniforms.
	 */
	public static void bind(Matrix4f projectionMatrix, Matrix3f rotationMatrix) {
		glDisable(GL_BLEND);
		
		TextureAtlas.BLOCKS.bindTexture();
		SHADER.bind();
		// Uniforms:
		try (MemoryStack stack = MemoryStack.stackPush()) {
			// Dump the matrix into a float buffer
			FloatBuffer fb = stack.mallocFloat(16);
			projectionMatrix.get(fb);
			glUniformMatrix4fv(UNIFORM_PROJ_MAT, false, fb);
		}
		try (MemoryStack stack = MemoryStack.stackPush()) {
			// Dump the matrix into a float buffer
			FloatBuffer fb = stack.mallocFloat(9);
			rotationMatrix.get(fb);
			glUniformMatrix3fv(UNIFORM_ROT_MAT, false, fb);
		}
		glUniform1i(UNIFORM_ATLAS_SIZE, TextureAtlas.BLOCKS.size());
	}
	
	/**
	 * Binds all the stuff needed by the transparent part of the Chunk Mesh such as Shader and texture atlas.
	 * Also initializes all teh uniforms.
	 */
	public static void bindTransparent(Matrix4f projectionMatrix, Matrix3f rotationMatrix) {
		glBlendEquationSeparate(GL_FUNC_REVERSE_SUBTRACT, GL_MAX);
		glBlendFuncSeparate(GL_SRC_COLOR, GL_ONE, 0, 0);
		glDepthMask(false);
		
		TextureAtlas.BLOCKS.bindTexture();
		SHADER_TRANSPARENT.bind();
		// Uniforms:
		try (MemoryStack stack = MemoryStack.stackPush()) {
			// Dump the matrix into a float buffer
			FloatBuffer fb = stack.mallocFloat(16);
			projectionMatrix.get(fb);
			glUniformMatrix4fv(UNIFORM_TRANSPARENT_PROJ_MAT, false, fb);
		}
		try (MemoryStack stack = MemoryStack.stackPush()) {
			// Dump the matrix into a float buffer
			FloatBuffer fb = stack.mallocFloat(9);
			rotationMatrix.get(fb);
			glUniformMatrix3fv(UNIFORM_TRANSPARENT_ROT_MAT, false, fb);
		}
		glUniform1i(UNIFORM_TRANSPARENT_ATLAS_SIZE, TextureAtlas.BLOCKS.size());
	}
	
	/**
	 * Binds all the stuff needed by the Chunk Mesh such as Shader and texture atlas.
	 */
	public static void unbind() {
		TextureAtlas.BLOCKS.unbindTexture();
		SHADER.unbind();
		SHADER_TRANSPARENT.unbind();
		glEnable(GL_BLEND);
		glDepthMask(true);
	}
	

	int vao = -1;
	int[] vbos = new int[4];
	int vaoTransparent = -1;
	int[] vbosTransparent = new int[4];

	int faceCount = 0;
	int faceCountTransparent = 0;
	
	float priority;
	
	
	ChunkVisibilityData visibilityData;
	boolean needsUpdate = true;
	/** If the ChunkMesh was already taken out of all data structures it is considered dead. If a ChunkMesh is dead, then it won't put any more data on the GPU to prevent leaks. */
	boolean isDead = false;
	
	/**
	 * Needs to be called inside the GL render thread!
	 * Renders this chunk mesh and updates it if necessary.
	 */
	public void render(Vector3f playerPosition) {
		if(vao == -1) return;
		glUniform3f(UNIFORM_PLAYER, playerPosition.x - visibilityData.wx, playerPosition.y - visibilityData.wy, playerPosition.z - visibilityData.wz);

		glBindVertexArray(vao);
		glDrawElements(GL_TRIANGLES, faceCount, GL_UNSIGNED_INT, 0);
	}
	
	/**
	 * Needs to be called inside the GL render thread!
	 * Renders the transparent chunk mesh and updates it if necessary.
	 */
	public void renderTransparent(Vector3f playerPosition) {
		if(vaoTransparent == -1) return;
		glUniform3f(UNIFORM_TRANSPARENT_PLAYER, playerPosition.x - visibilityData.wx, playerPosition.y - visibilityData.wy, playerPosition.z - visibilityData.wz);
		
		glBindVertexArray(vaoTransparent);
		glDrawElements(GL_TRIANGLES, faceCountTransparent, GL_UNSIGNED_INT, 0);
		
	}
	
	/**
	 * Called whenever there is a block update.
	 */
	public void onBlockUpdate() {
		needsUpdate = true;
	}
	
	private int addModel(FloatSimpleList vertices, FloatSimpleList normals, IntSimpleList faces, FloatSimpleList textures, int i, int vertexCount) {
		int x = visibilityData.x[i];
		int y = visibilityData.y[i];
		int z = visibilityData.z[i];
		Model model = Blocks.model(visibilityData.blocks[i]);
		int atlasX = BlockMeshes.atlasX(visibilityData.blocks[i]);
		int atlasY = BlockMeshes.atlasY(visibilityData.blocks[i]);
		int atlasWidth = BlockMeshes.atlasWidth(visibilityData.blocks[i]);
		int atlasHeight = BlockMeshes.atlasHeight(visibilityData.blocks[i]);
		int resolution = visibilityData.resolution;
		if(model.isCube) {
			int skipped = 0;
			for(int n = 0; n < Neighbor.NEIGHBORS; n++) {
				if((visibilityData.neighbors[i] & Neighbor.BIT_MASK[n]) != 0) {
					// There are 4 vertices per side.
					for(int j = n*4*3; j < (n + 1)*4*3;) {
						vertices.add((model.vertices[j++] + x)*resolution);
						vertices.add((model.vertices[j++] + y)*resolution);
						vertices.add((model.vertices[j++] + z)*resolution);
					}
					normals.add(model.normals, n*4*3, 4*3);
					for(int j = n*4*2; j < (n + 1)*4*2;) {
						textures.add(model.textCoords[j++]*atlasWidth + atlasX);
						textures.add(model.textCoords[j++]*atlasHeight + atlasY);
					}
					// There are 2 faces per side.
					for(int j = n*2*3; j < (n + 1)*2*3; j++) {
						faces.add(model.indices[j] + vertexCount - skipped*4);
					}
				} else {
					skipped++;
				}
			}
			
			vertexCount += 24 - skipped*4;
		} else {
			for(int j = 0; j < model.vertices.length;) {
				vertices.add((model.vertices[j++] + x)*resolution);
				vertices.add((model.vertices[j++] + y)*resolution);
				vertices.add((model.vertices[j++] + z)*resolution);
			}
			normals.add(model.normals);
			for(int j = 0; j < model.textCoords.length;) {
				textures.add(model.textCoords[j++]*atlasWidth + atlasX);
				textures.add(model.textCoords[j++]*atlasHeight + atlasY);
			}
			for(int j = 0; j < model.indices.length; j++) {
				faces.add(model.indices[j] + vertexCount);
			}
			
			vertexCount += model.vertices.length/3;
		}
		return vertexCount;
	}
	
	/**
	 *
	 * @return vao
	 */
	private int sendToGPU(FloatSimpleList vertices, FloatSimpleList normals, IntSimpleList faces, FloatSimpleList textures, int[] vbos) {
		int vao = glGenVertexArrays();
		glBindVertexArray(vao);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);

		// Position VBO
		int vbo = glGenBuffers();
		vbos[0] = vbo;
		FloatBuffer floatBuffer = MemoryUtil.memAllocFloat(vertices.size);
		floatBuffer.put(vertices.array, 0, vertices.size).flip();
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, floatBuffer, GL_STATIC_DRAW);
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		MemoryUtil.memFree(floatBuffer);

		// Texture VBO
		vbo = glGenBuffers();
		vbos[1] = vbo;
		floatBuffer = MemoryUtil.memAllocFloat(textures.size);
		floatBuffer.put(textures.array, 0, textures.size).flip();
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, floatBuffer, GL_STATIC_DRAW);
		glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		MemoryUtil.memFree(floatBuffer);


		// Normal VBO
		vbo = glGenBuffers();
		vbos[2] = vbo;
		floatBuffer = MemoryUtil.memAllocFloat(normals.size);
		floatBuffer.put(normals.array, 0, normals.size).flip();
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, floatBuffer, GL_STATIC_DRAW);
		glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);
		MemoryUtil.memFree(floatBuffer);

		// Index VBO
		vbo = glGenBuffers();
		vbos[3] = vbo;
		IntBuffer intBuffer = MemoryUtil.memAllocInt(faces.size);
		intBuffer.put(faces.array, 0, faces.size).flip();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, intBuffer, GL_STATIC_DRAW);
		MemoryUtil.memFree(intBuffer);

		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindVertexArray(0);
		return vao;
	}
	
	/**
	 * Regenerates the mesh and uploads it to the GPU.
	 * NEEDS TO BE CALLED IN THE OPENGL THREAD!
	 */
	public void generateMesh() {
		if(vao != -1 || vaoTransparent != -1) {
			cleanup();
		}
		if(isDead) return;
		needsUpdate = false;
		FloatSimpleList vertices = localVertices.get();
		vertices.clear();
		FloatSimpleList normals = localNormals.get();
		normals.clear();
		IntSimpleList faces = localFaces.get();
		faces.clear();
		FloatSimpleList textures = localTextures.get();
		textures.clear();
		FloatSimpleList verticesTransparent = localTransparentVertices.get();
		verticesTransparent.clear();
		FloatSimpleList normalsTransparent = localTransparentNormals.get();
		normalsTransparent.clear();
		IntSimpleList facesTransparent = localTransparentFaces.get();
		facesTransparent.clear();
		FloatSimpleList texturesTransparent = localTransparentTextures.get();
		texturesTransparent.clear();
		int vertexCount = 0;
		int vertexCountTransparent = 0;
		for(int i = 0; i < visibilityData.size; i++) {
			if(BlockMeshes.opaque(visibilityData.blocks[i])) {
				vertexCount = addModel(vertices, normals, faces, textures, i, vertexCount);
			}
			if(BlockMeshes.transparent(visibilityData.blocks[i])) {
				vertexCountTransparent = addModel(verticesTransparent, normalsTransparent, facesTransparent, texturesTransparent, i, vertexCountTransparent);
			}
		}
		faceCount = faces.size;
		faceCountTransparent = facesTransparent.size;
		// Create the VAO und VBOs.
		vao = sendToGPU(vertices, normals, faces, textures, vbos);
		vaoTransparent = sendToGPU(verticesTransparent, normalsTransparent, facesTransparent, texturesTransparent, vbos);
	}
	
	/**
	 * Cleans all resources on the GPU.
	 */
	public void cleanup() {
		glDisableVertexAttribArray(0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		for (int vbo : vbos) {
			glDeleteBuffers(vbo);
		}
		for (int vbo : vbosTransparent) {
			glDeleteBuffers(vbo);
		}
		glBindVertexArray(0);
		glDeleteVertexArrays(vao);
		glDeleteVertexArrays(vaoTransparent);
		vao = -1;
		vaoTransparent = -1;
	}

	@Override
	public int compareTo(ChunkMesh o) {
		return priority < o.priority ? -1 : priority > o.priority ? 1 : 0;
	}
}
