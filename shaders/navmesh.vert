#version 120

uniform mat4 modelViewProjectionMatrix;

void main () {
	gl_Position = modelViewProjectionMatrix * gl_Vertex;
	gl_Position.z = -1;
}