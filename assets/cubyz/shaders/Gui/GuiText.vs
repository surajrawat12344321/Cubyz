#version 330 core

layout (location=0) in vec2 vertex_pos;
layout (location=1) in vec2 face_pos;

out vec2 frag_face_pos;
uniform vec4 rect;

void main(){
	vec2 position = vec2(rect.x+vertex_pos.x*rect.z,-rect.y+vertex_pos.y*rect.w)*2+vec2(-1,1);
	gl_Position = vec4(position,0,1);
	frag_face_pos = face_pos;
}