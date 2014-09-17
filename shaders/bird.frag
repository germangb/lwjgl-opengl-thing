#version 330

uniform vec3 fogColor;
uniform float fogStart;
uniform float fogConst;
uniform vec3 ambientTint;

in vec3 position;

out vec4 fragColor;

void main () {
	
	vec3 finalColor = vec3(0.0);
	
	/* tint */
	finalColor.rgb *= ambientTint;
	
	/* fog */
	float dist = abs(position.z);
	if (dist > fogStart) {
		float fog = 1.0 - exp(-(dist-fogStart) * fogConst);
		finalColor.rgb = mix(finalColor.rgb, fogColor, mix(0, 0.9, fog));
	}

	/* tint */
	finalColor.rgb *= ambientTint;

	fragColor = vec4(finalColor, 1.0);
}