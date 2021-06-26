#version 330

in vec2 outTexCoord;
in vec3 lightColor;

out vec4 fragColor;

uniform sampler2D texture_sampler;

vec4 getTextureColor() {
	return texture(texture_sampler, outTexCoord);
}

void main() {
	fragColor = vec4(1, 1, 1, 1)*vec4(lightColor, 1);
}
