#version 120

uniform sampler2D texture;

varying vec2 uv;

void main () {

	vec4 color = texture2D(texture, uv);
	if (color.a < 0.1) discard;
	
	gl_FragColor = color;

}
