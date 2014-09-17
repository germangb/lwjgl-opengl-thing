#version 120

uniform mat4 modelViewProjectionMatrix;
uniform mat4 modelViewMatrix;
uniform mat4 viewMatrix;

varying vec3 position;
varying vec2 uv;

void main () {
	gl_Position = modelViewProjectionMatrix * gl_Vertex;
	position = (modelViewMatrix * gl_Vertex).xyz;
	uv = gl_MultiTexCoord0.xy;
}