package io.cubyz.rendering;

import static org.lwjgl.opengl.GL30.glUseProgram;
import static org.lwjgl.opengl.GL30.*;

import java.nio.file.Files;
import java.nio.file.Path;



import io.cubyz.utils.log.Log;

public class Shader {

	int id= 0;
	
	//shader creation
	int compileShaderFromString(int type,String code) throws Throwable {
		
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
			throw new Exception("Couldn't compile shader. " + glGetShaderInfoLog(id, 1024));
		}
		
		return id;
	}
	//shader programm creation
	int compileProgramFromString(String vertexShader,String fragmentShader) throws Throwable
	{
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
		
		return program;
	}
	
	public void loadFromFile(String vertexShaderPath,String fragmentShaderPath) {
		try {
			String vertexShader = Files.readString(Path.of(vertexShaderPath));
			String fragmentShader = Files.readString(Path.of(fragmentShaderPath));
			
			id = compileProgramFromString(vertexShader,fragmentShader);
		} catch (Throwable e) {
			Log.severe(e);
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
}
