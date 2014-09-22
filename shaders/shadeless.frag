#version 330

uniform vec4 tint;

out vec4 fragColor;

void main () {
	fragColor = vec4(vec4(1.0)*tint);
}
