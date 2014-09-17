#version 120

uniform mat4 modelViewProjectionMatrix;
uniform vec3 size;
uniform mat4 shadowMatrix;
uniform vec3 dirLight;

varying vec3 lightDirection;

void main () {
	vec4 pos = gl_Vertex * vec4(size, 1.0);
	gl_Position = modelViewProjectionMatrix * pos;
}
