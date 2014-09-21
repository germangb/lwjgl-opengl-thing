#version 120

uniform mat4 modelViewProjectionMatrix;

varying vec2 uv;

void main () {
	gl_Position = modelViewProjectionMatrix * gl_Vertex;
	uv = gl_MultiTexCoord0.xy;
}
