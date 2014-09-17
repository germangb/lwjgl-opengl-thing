#version 120

uniform mat4 modelViewProjectionMatrix;

varying vec2 uv;
varying vec4 tint;

void main () {
	gl_Position = modelViewProjectionMatrix * gl_Vertex;
	uv = gl_MultiTexCoord0.xy;
	tint = gl_Color.rgba;
}