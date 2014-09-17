#version 120

uniform float time;
uniform vec3 fogColor;
uniform float fogStart;
uniform float fogConst;
uniform vec3 ambientTint;

varying vec3 position;
varying vec3 color;

void main () {
	vec4 finalColor = vec4(color, 1.0);
	
	/* fog */
	float dist = abs(position.z);
	if (dist > fogStart) {
		float fog = 1.0 - exp(-(dist-fogStart) * fogConst);
		finalColor.rgb = mix(finalColor.rgb, fogColor, mix(0, 0.9, fog));
	}

	/* tint */
	finalColor.rgb *= ambientTint;
	finalColor.rgb = pow(finalColor.rgb, vec3(1.125));
	
	/* output the color */
	gl_FragData[0] = finalColor;
	
	/* reflection stuff */
	gl_FragData[1] = vec4(0.0, 1.0, 0.0, 1.0);
}
