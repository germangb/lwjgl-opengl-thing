#version 120
#define PI 3.14159265

uniform sampler2D shadowMap;

uniform vec3 fogColor;
uniform float fogStart;
uniform float fogConst;
uniform vec3 ambientTint;
uniform int renderShadows;

varying vec3 shadowCoord;
varying vec3 position;
varying vec3 lightDirection;
varying vec3 rawPosition;
varying vec3 normal;
varying float concrete;
varying float sand;
varying float road;

/* layer colors */
const vec3 grassColor = vec3(97/255f, 100/255f, 67/255f);
const vec3 sandColor = vec3(165/255f, 160/255f, 128/255f);
const vec3 concreteColor = vec3(138/255f);
const vec3 roadColor = vec3(77/255f);

void main () {

	/* final fragment color */
	vec2 uv = rawPosition.xz / 8.0;

	vec4 finalColor = vec4(grassColor.rgb, 1.0);
	
	float borderStrength = 5.5;
	/* paint sand */
	vec3 sandLineColor = sandColor.rgb * 1.5;
	if (sand > 0.5) {
		float iso = abs(sand - 0.5);
		finalColor.rgb = mix(sandColor, sandLineColor, exp(-pow( iso , borderStrength) * 10000000000.0));
	} else {
		float iso = abs(sand - 0.5);
		finalColor.rgb = mix(finalColor.rgb, sandLineColor, exp(-pow( iso , borderStrength) * 10000000000.0));
	}
	
	/* paint concrete */
	vec3 concreteLineColor = concreteColor.rgb * 1.5;
	if (concrete > 0.5) {
		float iso = abs(concrete - 0.5);
		finalColor.rgb = mix(concreteColor, concreteLineColor, exp(-pow( iso , borderStrength) * 10000000000.0));
	} else {
		float iso = abs(concrete - 0.5);
		finalColor.rgb = mix(finalColor.rgb, concreteLineColor, exp(-pow( iso , borderStrength) * 10000000000.0));
	}
	
	/* paint road */
	vec3 roadLineColor = roadColor.rgb * 0.5;
	if (road > 0.5) {
		float iso = abs(road - 0.5);
		finalColor.rgb = mix(roadColor,roadLineColor, exp(-pow( iso , borderStrength) * 10000000000.0));
	} else {
		float iso = abs(road - 0.5);
		finalColor.rgb = mix(finalColor.rgb, roadLineColor, exp(-pow( iso , borderStrength) * 10000000000.0));
	}

	/* apply simple lighting */
	float light = max(0.0, dot(normal, -lightDirection));
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
	light = mix(0.0, 1.0, min(light, shadow));
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
	gl_FragData[0] = vec4(finalColor.rgb, light);
}
