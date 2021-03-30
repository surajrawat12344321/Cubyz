#version 330 


layout (location=0) in vec2 position;
layout (location=1) in vec2 face_position;	//position on the face,between 0-1

out vec2 frag_face_position;

uniform vec2 translation;


void main(){
	gl_Position = vec4(translation+position.xy,0,1);
	frag_face_position = face_position;
}