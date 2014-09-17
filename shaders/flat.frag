#version 120

uniform sampler2D texture;

varying vec2 uv;
varying vec4 tint;

void main () {
	/* get final color */
	vec4 finalColor = texture2D(texture, uv)*tint;
	
	/* out color */
	gl_FragData[0] = finalColor;
	gl_FragData[1] = vec4(0,0,0,finalColor.a);
}