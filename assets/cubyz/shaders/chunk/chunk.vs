#version 330

layout (location=0)  in vec3 position;
layout (location=1)  in vec2 texCoord;
layout (location=2)  in vec3 vertexNormal;

out vec2 outTexCoord;
out vec3 lightColor;

uniform vec3 relativePlayerPos;
uniform mat3 rotationMatrix;
uniform mat4 projectionMatrix;

void main() {
	gl_Position = projectionMatrix*vec4(rotationMatrix*(position - relativePlayerPos), 1);
	outTexCoord = texCoord;
	lightColor = 0.5 + 0.5*vertexNormal;
}
