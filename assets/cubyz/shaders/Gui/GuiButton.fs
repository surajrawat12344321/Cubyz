#version 330 

layout (location=0) out vec4 frag_color;
in vec2 frag_face_position;

uniform vec2 shadow;
uniform int mode;
uniform float shadowIntensity;
uniform vec3 color;


//	takes percentage position
//	returns a color of the shadow

vec4 brighten(vec4 inColor,float intensity){
	return vec4(1,1,1,1)*intensity+(1-intensity)*inColor;
}
vec4 darken(vec4 inColor,float intensity){
	return 	(1-intensity)*inColor;
}

vec4 getShadowOverlay(vec2 face_pos,vec4 inColor){	
	
	//top
	float closestBorderDistance = face_pos.y*shadow.x;
	vec4 color = inColor;
	switch (mode) {
			case 0: //unselected
				color = brighten(inColor,shadowIntensity);
				break;
			case 1:	//clicked
				color = darken(inColor,shadowIntensity);
				break;
		}
	
	//left
	if(face_pos.x*shadow.y<closestBorderDistance){
		closestBorderDistance = face_pos.x*shadow.y;
		
		switch (mode) {
			case 0: //unselected
				color = brighten(inColor,0.75*shadowIntensity);
				break;
			case 1: //pressed
				color = darken(inColor,0.75*shadowIntensity);
				break;
		}	
	}
	
	//down
	if((1-face_pos.y)*shadow.x<closestBorderDistance){
		closestBorderDistance = (1-face_pos.y)*shadow.x;
		switch (mode) {
			case 0: //unselected
				color = darken(inColor,1*shadowIntensity);
				break;
			case 1:	//clicked 
				color = brighten(inColor,1*shadowIntensity);
				break;
		}
	}
	
	//right
	if((1-face_pos.x)*shadow.y<closestBorderDistance){
		closestBorderDistance = (1-face_pos.x)*shadow.y;
		switch (mode) {
			case 0: //unselected
				color = darken(inColor,0.75*shadowIntensity);
				break;
			case 1:	//clicked 
				color = brighten(inColor,0.75*shadowIntensity);
				break;
		}
	}
	return vec4(color.xyz,1);	
}



vec4 overlayShadow(vec2 face_pos,vec4 color){
	

	if(
		face_pos.x<shadow.x||
		face_pos.y<shadow.y||
		face_pos.x>=1-shadow.x||
		face_pos.y>=1-shadow.y){
			color = getShadowOverlay(frag_face_position,color);
		}
	return vec4(color.xyz,1);	
}


void main(){
	frag_color = overlayShadow(frag_face_position,vec4(color/255,1));
}