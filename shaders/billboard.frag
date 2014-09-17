#version 120

uniform sampler2D texture;

uniform float time;
uniform vec3 fogColor;
uniform float fogStart;
uniform float fogConst;
uniform vec3 ambientTint;

varying vec3 position;
varying vec2 uv;

void main () {
	/* texture color */
	vec4 finalColor = texture2D(texture, uv);
	if (finalColor.a < 0.1) discard;
	
	/* fog */
	float dist = abs(position.z);
	if (dist > fogStart) {
		float fog = 1.0 - exp(-(dist-fogStart) * fogConst * 0.75);
		finalColor.rgb = mix(finalColor.rgb, fogColor, mix(0, 0.9, fog));
	}

	/* tint */
	finalColor.rgb *= ambientTint;
	finalColor.rgb = pow(finalColor.rgb, vec3(1.125));
	
	/* set color */
	gl_FragData[0] = finalColor;
	gl_FragData[1] = vec4(0,0.5,0,1);
}
