#version 120

uniform float time;
uniform vec2 resolution;
uniform sampler2D colorTexture;

void main () {
	vec2 uv = gl_FragCoord.xy / resolution;
	vec4 color = texture2D(colorTexture, uv).rgba;
	gl_FragColor = vec4(color.rgb, 1.0);
}
