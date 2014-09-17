#version 120
#define PI 3.14159265

uniform vec3 fogColor;
uniform float fogStart;
uniform float fogConst;
uniform vec3 ambientTint;

uniform sampler2D colorTexture;
uniform sampler2D depth;
uniform sampler2D scene;
uniform vec2 resolution;
uniform mat4 invProj;
uniform mat4 invMvp;
uniform vec3 size;
uniform float opacity;

varying vec3 lightDirection;

void main () {
	vec4 finalColor = vec4(0,0,0,1);
	
	/* decal projection */
	vec2 uv = gl_FragCoord.xy / resolution;
	float depth = texture2D(depth, uv).r;
	vec4 screenPos = vec4(uv*2.0-1.0, depth*2.0-1.0, 1.0);
	vec4 localPos = invMvp * screenPos;
	localPos = vec4(localPos.xyz/localPos.w, localPos.w);
	vec4 worldPos = invProj * screenPos;
	worldPos = vec4(worldPos.xyz/worldPos.w, worldPos.w);
	
	if (abs(localPos.x) < size.x/2 &&
		abs(localPos.z) < size.z/2 &&
		abs(localPos.y) < size.y/2) {
		vec2 decalUv;
		decalUv.x = (localPos.x + size.x/2)/size.x;
		decalUv.y = (localPos.z + size.z/2)/size.z;
		if (abs(decalUv.x-1.0) < 0.0 ||
			abs(decalUv.x-1.0) < 0.0)
			discard;
		vec4 tex = texture2D(colorTexture, decalUv);
		tex.a *= opacity;
		if (tex.a < 0.01) discard;
		vec4 sce = texture2D(scene, uv);
		tex.rgb *= sce.a;// lighting
		tex.rgb *= ambientTint;
		// fog applied to the decal
		float dist = abs(worldPos.z);
		if (dist > fogStart) {
			float fog = (1.0 - exp(-(dist-fogStart) * fogConst));
			tex.rgb = mix(tex.rgb, fogColor, mix(0, 0.9, fog));
		}
		/* tint */
		tex.rgb *= ambientTint;
		
		finalColor.rgb = mix(sce.rgb, tex.rgb, tex.a);
	} else discard;
	
	gl_FragData[0] = finalColor;
}
