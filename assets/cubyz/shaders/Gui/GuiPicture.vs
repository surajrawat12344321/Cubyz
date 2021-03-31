#version 330 


layout (location=0) in vec2 vertex_pos;
layout (location=1) in vec2 face_position;	//position on the face,between 0-1

out vec2 frag_face_position;

uniform vec2 model_pos;
uniform vec2 model_size;
uniform vec2 scene_size;


void main(){
	
	//convert from scene coords to opengl coords
	vec2 model_size_opengl = vec2(model_size.x/scene_size.x,model_size.y/scene_size.y)*2;
	vec2 model_pos_opengl = vec2(model_pos.x/scene_size.x*2-1,1-model_pos.y/scene_size.y*2);

	gl_Position = vec4(model_pos_opengl+vec2(vertex_pos.x*model_size_opengl.x,vertex_pos.y*model_size_opengl.y),0,1);
	
	frag_face_position = face_position;
}