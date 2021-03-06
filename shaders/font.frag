#version 120

uniform sampler2D texture;
uniform vec4 tint;

varying vec2 uv;

void main () {

	vec4 color = texture2D(texture, uv) * tint;
	if (color.a < 0.025) discard;
	
	gl_FragColor = color;

}
