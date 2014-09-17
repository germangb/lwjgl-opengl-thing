#version 330

in vec3 vPosition;
in vec3 vNormal;
in vec2 vUv;

uniform mat4 modelViewProjectionMatrix;
uniform mat4 modelViewMatrix;
uniform mat4 viewMatrix;
uniform mat4 shadowMatrix;
uniform int highlight;
uniform vec3 dirLight;

out vec3 shadowCoord;
out vec3 lightDirection;
out vec3 position;
out vec3 normal;
out vec2 uv;

void main () {
	vec4 vec = vec4(vPosition, 1.0);
	shadowCoord = (shadowMatrix * vec).xyz;
	if (highlight == 1)
		vec.xyz += vNormal * 0.5;
	gl_Position = (modelViewProjectionMatrix) * vec;
	position = (modelViewMatrix * vec).xyz;
	normal = normalize((modelViewMatrix * vec4(vNormal, 0.0)).xyz);
	uv = vUv.xy;
	lightDirection = normalize((viewMatrix * vec4(dirLight,0)).xyz);
}
