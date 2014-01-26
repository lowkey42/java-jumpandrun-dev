uniform sampler2D texture;
uniform sampler2D normals;

uniform vec3 light0;
uniform vec4 ambientColor;

void main() {
	vec3 attenuation = vec3(0.4, 0.1, 0.1);

	vec4 color = texture2D(texture, gl_TexCoord[0].xy);
    vec3 nColor = texture2D(normals, gl_TexCoord[0].xy).rgb;
    
    vec3 normal = normalize(nColor * 2.0 - 1.0);
    
    vec3 lightDir = normalize(light0);
    float lambert = clamp(dot(normal, lightDir), 0.0, 1.0);
    
    //now let's get a nice little falloff
    float d = sqrt(dot(light0, light0));       
    float att = 1.0 / ( attenuation.x + (attenuation.y*d) + (attenuation.z*d*d) );
    
    vec3 lightColor = vec3(1,1,0.8);
    vec3 result = (ambientColor.rgb * ambientColor.a * 0.0) + (lightColor.rgb * lambert) * att;
    result *= color.rgb;
       
    gl_FragColor = gl_Color * vec4(result, color.a);
    

	//vec4 pixel = texture2D(texture, gl_TexCoord[0].xy);

 	//gl_FragColor = gl_Color * pixel;
}