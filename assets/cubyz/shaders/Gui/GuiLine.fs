#version 330 

layout (location=0) out vec4 frag_color;

uniform sampler2D texture_sampler;

//in pxls
uniform vec4 lineColor;


void main() {
	frag_color = lineColor;
}