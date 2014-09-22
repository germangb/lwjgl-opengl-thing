#version 330
#define PI 3.14159265

uniform sampler2D colorTexture;
uniform sampler2D shadowMap;
uniform vec3 fogColor;
uniform float fogStart;
uniform float fogConst;
uniform float time;
uniform vec3 ambientTint;
uniform int highlight;
uniform int renderShadows;

in vec3 shadowCoord;
in vec3 lightDirection;
in vec3 position;
in vec3 normal;
in vec2 uv;

out vec4 fragData0;
out vec4 fragData1;

float smoothstep(float edge0, float edge1, float x) {
    // Scale, bias and saturate x to 0..1 range
    x = clamp((x - edge0)/(edge1 - edge0), 0.0, 1.0); 
    // Evaluate polynomial
    return x*x*(3 - 2*x);
}

float rand(vec2 co){
    return abs(fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453));
}

void main () {
	/* final fragment color */
	vec4 finalColor = texture(colorTexture, uv);
	
	/* highlight */
	if (highlight == 1) {
		finalColor.rgb = vec3(0.0);
	}
	
	/* lighting vectors */
	vec3 normSight = normalize(-lightDirection);
	
	/* apply simple lighting */
	float light = max(0.0, dot(normal, normSight));
	/* shadows */
	float shadow = 1.0;
	if (renderShadows == 1) {
		float bias = 0.0035;
		float sc = 600;
		for (float i = 0; i < 2*PI; i += 1.5) {
			vec2 pois = vec2(cos(i), sin(i));
			if (texture2D(shadowMap, shadowCoord.xy+pois/sc).z < shadowCoord.z-bias)
				shadow -= 0.175;
		}
		shadow = clamp(shadow, 0, 1);
	}
	light = mix(0.25, 1.0, min(light, shadow));
	finalColor.rgb *= light;

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
	
	/* color normal */
	//vec3 normal = (normal+1.0)*0.5;
	
	/* output the color */
	fragData0 = vec4(finalColor.rgb, 1);
	fragData1 = vec4(light,0,0,1);
}
