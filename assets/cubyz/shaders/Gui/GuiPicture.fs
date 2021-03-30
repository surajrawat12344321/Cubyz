#version 330 

layout (location=0) out vec4 frag_color;
in vec2 frag_face_position;

uniform vec2 shadow;



//	takes percentage position
//	returns a color of the shadow

vec4 getShadowPixel(vec2 face_pos){	
	//top
	float closestBorderDistance = face_pos.y*shadow.x;
	vec4 color = vec4(0,0,0,1);
	
	//left
	if(face_pos.x*shadow.y<closestBorderDistance){
		closestBorderDistance = face_pos.x*shadow.y;
		color = vec4(1,0,0,1);
	}
	
	//down
	if((1-face_pos.y)*shadow.x<closestBorderDistance){
		closestBorderDistance = (1-face_pos.y)*shadow.x;
		color = vec4(0,1,0,1);
	}
	
	//right
	if((1-face_pos.x)*shadow.y<closestBorderDistance){
		closestBorderDistance = (1-face_pos.x)*shadow.y;
		color = vec4(0,0,1,1);
	}
	return color;	
}
vec4 overlayShadow(vec2 face_pos,vec4 color){
	float shadowIntensity = 0.75;

	if(
		face_pos.x<shadow.x||
		face_pos.y<shadow.y||
		face_pos.x>=1-shadow.x||
		face_pos.y>=1-shadow.y){
			color = (1-shadowIntensity)*color;
			color += shadowIntensity*getShadowPixel(frag_face_position);
		}
	return color;
}


void main(){
	frag_color = overlayShadow(frag_face_position,vec4(frag_face_position.xy,0,1));
}