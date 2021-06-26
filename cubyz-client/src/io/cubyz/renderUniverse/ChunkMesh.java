package io.cubyz.renderUniverse;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
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

import io.cubyz.gui.rendering.Shader;
import io.cubyz.utils.datastructures.simple_list.FloatSimpleList;
import io.cubyz.utils.datastructures.simple_list.IntSimpleList;
import io.cubyz.world.ChunkVisibilityData;
import io.cubyz.world.blocks.Blocks;
import io.cubyz.world.blocks.Model;

public class ChunkMesh {
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

	public static final Shader SHADER = Shader.loadFromFile("assets/cubyz/shaders/chunk/chunk.vs", "assets/cubyz/shaders/chunk/chunk.fs");
	public static final int UNIFORM_PLAYER = SHADER.getUniformLocation("relativePlayerPos");
	public static final int UNIFORM_ROT_MAT = SHADER.getUniformLocation("rotationMatrix");
	public static final int UNIFORM_PROJ_MAT = SHADER.getUniformLocation("projectionMatrix");
	public static final int UNIFORM_TEXTURE = SHADER.getUniformLocation("texture_sampler");
	public static final FloatBuffer ROT_MAT_BUFFER = FloatBuffer.allocate(9);
	public static final FloatBuffer PROJ_MAT_BUFFER = FloatBuffer.allocate(16);
	
	int vao = -1;
	int[] vbos = new int[4];
	
	int faceCount = 0;
	
	
	public final ChunkVisibilityData visibilityData;
	boolean needsUpdate = true;
	public ChunkMesh(ChunkVisibilityData visibilityData) {
		this.visibilityData = visibilityData;
	}
	
	/**
	 * Needs to be called inside the GL render thread!
	 * Renders this chunk mesh and updates it if necessary.
	 */
	public void render(Matrix4f projectionMatrix, Matrix3f rotationMatrix, Vector3f playerPosition) {
		if(needsUpdate) {
			generateMesh();
		}
		if(vao == -1) return;
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
		glUniform3f(UNIFORM_PLAYER, playerPosition.x - visibilityData.wx, playerPosition.y - visibilityData.wy, playerPosition.z - visibilityData.wz);
		// Init
		glBindVertexArray(vao);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		// Draw
		glDrawElements(GL_TRIANGLES, faceCount, GL_UNSIGNED_INT, 0);
		// Restore state
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(2);
		glBindVertexArray(0);
		
		SHADER.unbind();
	}
	
	/**
	 * Called whenever there is a block update.
	 */
	public void onBlockUpdate() {
		needsUpdate = true;
	}
	
	private void generateMesh() {
		//needsUpdate = false;
		if(vao != -1) {
			cleanup();
		}
		FloatSimpleList vertices = localVertices.get();
		vertices.clear();
		FloatSimpleList normals = localNormals.get();
		normals.clear();
		IntSimpleList faces = localFaces.get();
		faces.clear();
		FloatSimpleList textures = localTextures.get();
		textures.clear();
		int vertexCount = 0;
		for(int i = 0; i < visibilityData.size; i++) {
			int x = visibilityData.x[i];
			int y = visibilityData.y[i];
			int z = visibilityData.z[i];
			Model model = Blocks.model(visibilityData.blocks[i]);
			for(int j = 0; j < model.vertices.length;) {
				vertices.add(model.vertices[j++] + x);
				vertices.add(model.vertices[j++] + y);
				vertices.add(model.vertices[j++] + z);
			}
			normals.add(model.normals);
			textures.add(model.textCoords);
			for(int j = 0; j < model.indices.length; j++) {
				faces.add(model.indices[j] + vertexCount);
			}
			
			vertexCount += model.vertices.length/3;
		}
		faceCount = faces.size;
		// Create the VAO und VBOs.
		vao = glGenVertexArrays();
		glBindVertexArray(vao);

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
		glBindVertexArray(0);
		glDeleteVertexArrays(vao);
		vao = -1;
	}
}
