#version 330

in vec3 vPosition;

uniform mat4 modelViewProjectionMatrix;
uniform float time;

out vec3 position;

void main () {
	vec3 vec = vPosition;
	float s = sin(time * 4.0);
	vec.y += s * vec.x * vec.x * 0.35;
	gl_Position = modelViewProjectionMatrix * vec4(vec, 1.0);
	position = gl_Position.xyz;
}
