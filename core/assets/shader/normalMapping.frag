uniform sampler2D texture;
uniform sampler2D lightmap;

uniform float normalMapped;
uniform sampler2D normals;
varying vec3 light0;

uniform vec2 windowSize;

uniform vec4 ambientColor;

void main() {
	float lambert = 1.0;

	if( normalMapped>0.0 ) {
    	vec3 nColor = texture2D(normals, gl_TexCoord[0].xy).rgb;
    	
	    vec3 normal = normalize(nColor * 2.0 - 1.0);
	    vec3 lightDir = normalize(vec3( (light0.x - gl_TexCoord[0].x) / 10.0, (light0.y - -gl_TexCoord[0].y)/10.0, light0.z ));
	    lambert = clamp(dot(normal, lightDir), 0.0, 1.0);
	}

	vec4 color = texture2D(texture, gl_TexCoord[0].xy);
    vec3 lColor = texture2D(lightmap, gl_FragCoord.xy / windowSize).rgb;
    
    
    vec3 result = clamp((ambientColor.rgb * ambientColor.a) + lambert * lambert * lColor, 0.0, 1.0);
    result *= color.rgb;
    
    gl_FragColor = gl_Color * vec4(result + min(lambert * lColor.rgb * 0.5, 0.2), color.a);
}