#version 330 

layout (location=0) out vec4 frag_color;

uniform vec4 rectColor;

void main(){
	frag_color = rectColor;
}