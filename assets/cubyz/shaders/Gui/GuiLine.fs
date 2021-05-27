#version 330 

layout (location=0) out vec4 frag_color;

uniform sampler2D texture_sampler;

//in pxls
uniform int lineColor;


void main() {
	frag_color = vec4((lineColor & 0xff0000)>>16, (lineColor & 0xff00)>>8, lineColor & 0xff, (lineColor>>24) & 255)/255.0;
}