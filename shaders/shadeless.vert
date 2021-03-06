#version 330

in vec3 vPosition;

uniform mat4 modelViewProjectionMatrix;
uniform vec3 scale;
uniform vec3 offset;

void main () {
	vec4 vec = vec4((vPosition*scale+offset), 1.0);
	gl_Position = (modelViewProjectionMatrix) * vec;
	//gl_Position.z = -1;
}
