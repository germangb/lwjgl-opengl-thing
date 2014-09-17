#version 120

uniform mat4 modelViewProjectionMatrix;
uniform mat4 modelViewMatrix;

varying vec3 position;
varying vec3 color;

void main () {
	gl_Position = modelViewProjectionMatrix * (gl_Vertex*vec4(vec3(0.1), 1));
	position = (modelViewMatrix * gl_Vertex).xyz;
	color = gl_Color.rgb;
}
