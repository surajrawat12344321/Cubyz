#version 330 core

layout (location=0) in vec2 vertex_pos;


//in pixel
uniform vec2 rectStart;
uniform vec2 rectSize;
uniform vec2 scene;


vec2 convert2Proportional(vec2 original,vec2 full) {
	return vec2(original.x/full.x,original.y/full.y);
}


void main() {

	vec2 position_percentage 	= convert2Proportional(rectStart + vertex_pos*rectSize,scene);
	
	vec2 position = vec2(position_percentage.x, -position_percentage.y)*2+vec2(-1,1);
	
	gl_Position = vec4(position, 0, 1);
}