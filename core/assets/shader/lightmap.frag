uniform sampler2D texture;
uniform sampler2D fb;

void main() {

	vec4 old = texture2D(fb, gl_TexCoord[0].xy);
	vec4 pixel = texture2D(texture, gl_TexCoord[0].xy);

    vec3 ambient = gl_Color.rgb*gl_Color.a;

 //   gl_FragColor = vec4( old.rgb - (gl_Color.a - (pixel.rgb+gl_Color.rgb)/2), 1.0 );
 	gl_FragColor = vec4( old.rgb*(ambient+pixel.rgb) + pixel.rgb* 0.5, 1.0 );
}