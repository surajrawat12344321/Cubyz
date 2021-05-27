#version 330 

layout (location=0) out vec4 frag_color;

uniform int rectColor;

void main(){
	frag_color = vec4((rectColor & 0xff0000)>>16, (rectColor & 0xff00)>>8, rectColor & 0xff, (rectColor>>24) & 255)/255.0;
}