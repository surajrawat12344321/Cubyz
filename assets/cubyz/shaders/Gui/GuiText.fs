#version 330 

layout (location=0) out vec4 frag_color;

in vec2 frag_face_pos;
uniform sampler2D texture_sampler;
uniform vec4 texture_rect;

void main(){
	frag_color = vec4(0.1,0,0,0.1)+texture(texture_sampler,vec2(texture_rect.x+frag_face_pos.x*texture_rect.z,texture_rect.y+frag_face_pos.y*texture_rect.w));
}