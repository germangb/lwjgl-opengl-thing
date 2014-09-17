#version 120

uniform mat4 modelViewProjectionMatrix;
uniform mat4 modelViewMatrix;
uniform mat4 viewMatrix;
uniform mat4 shadowMatrix;
uniform vec3 dirLight;

varying vec3 shadowCoord;
varying vec3 position;
varying vec3 lightDirection;
varying vec3 rawPosition;
varying vec3 normal;
varying float concrete;
varying float sand;
varying float road;

void main () {
	shadowCoord = (shadowMatrix * gl_Vertex).xyz;
	gl_Position = modelViewProjectionMatrix * gl_Vertex;
	rawPosition = (gl_Vertex).xyz;
	position = (modelViewMatrix * gl_Vertex).xyz;
	normal = normalize((modelViewMatrix * vec4(gl_Normal, 0.0))).xyz;
	lightDirection = normalize((viewMatrix * vec4(dirLight,0)).xyz);
	concrete = gl_Color.r;
	sand = gl_Color.g;
	road = gl_Color.b;
}
