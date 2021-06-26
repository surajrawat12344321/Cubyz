package io.cubyz.gui.rendering;

import static org.lwjgl.opengl.GL30.glUseProgram;
import static org.lwjgl.opengl.GL30.*;

import java.nio.file.Files;
import java.nio.file.Path;



import io.cubyz.utils.log.Log;

public class Shader {

	int id = 0;
	
	private Shader() {
		
	}
	
	private static String getShaderType(int type) {
		if(type == GL_VERTEX_SHADER)
			return"Couldn't create vertexshader.";
		if(type == GL_FRAGMENT_SHADER)
			return "Coudln't create fragmentshader.";
		return null;
	}
	
	//shader creation
	private static int compileShaderFromString(int type, String code) throws Throwable {
		
		int id = glCreateShader(type);
		if (id == 0) {
			if(type == GL_VERTEX_SHADER)
				throw new Exception("Couldn't create vertexshader.");
			if(type == GL_FRAGMENT_SHADER)
				throw new Exception("Coudln't create fragmentshader.");
			
		}
		glShaderSource(id, code);
		glCompileShader(id);
		
		
		if (glGetShaderi(id, GL_COMPILE_STATUS) == 0) {
			
			throw new Exception("Couldn't compile "+(getShaderType(type))+"shader. " + glGetShaderInfoLog(id, 1024));
		}
		
		return id;
	}
	//shader programm creation
	public static Shader compileProgramFromString(String vertexShader, String fragmentShader) throws Throwable {
		Shader shader = new Shader();
		int program = glCreateProgram();
		if (program == 0) {
			throw new Exception("Couldn't create Shader");
		}
		int vs = compileShaderFromString(GL_VERTEX_SHADER,vertexShader);
		int fs = compileShaderFromString(GL_FRAGMENT_SHADER,fragmentShader);
		

		glAttachShader(program, vs);
		glAttachShader(program, fs);
		
		glLinkProgram(program);
		if (glGetProgrami(program, GL_LINK_STATUS) == 0) {
			throw new Exception("Couldn't link Shader." + glGetProgramInfoLog(program, 1024));
		}
		if (vs != 0) {
			glDetachShader(program, vs);
		}
		if (fs != 0) {
			glDetachShader(program, fs);
		}

		glValidateProgram(program);
		
		shader.id = program;
		
		return shader;
	}
	
	public static Shader loadFromFile(String vertexShaderPath, String fragmentShaderPath) {
		try {
			String vertexShader = Files.readString(Path.of(vertexShaderPath));
			String fragmentShader = Files.readString(Path.of(fragmentShaderPath));
			
			return compileProgramFromString(vertexShader,fragmentShader);
		} catch (Throwable e) {
			Log.severe(e);
			return new Shader();
		}
	}
	
	public void bind() {
		glUseProgram(id);
	}
	public void unbind() {
		glUseProgram(0);
	}
	public void cleanup(){
		unbind();
		if(id!=0){
			glDeleteProgram(id);
		}
	}
	
	public int getUniformLocation(String string) {
		return glGetUniformLocation(id, string);
	}
}
