#version 330

in vec3 vPosition;

uniform mat4 modelViewProjectionMatrix;
uniform vec3 scale;

void main () {
	vec4 vec = vec4(vPosition*scale, 1.0);
	gl_Position = (modelViewProjectionMatrix) * vec;
	//gl_Position.z = -1;
}
