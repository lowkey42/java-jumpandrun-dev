varying vec3 light0;
varying vec2 pos;
uniform float normalMapped;
uniform vec3 lightPos0;

void main() {
	if( normalMapped>0.0 )
		light0 = vec3( (gl_ModelViewProjectionMatrix * vec4(lightPos0 - gl_Vertex.xyz, 0.0)).xy, lightPos0.z );
	
	gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;

    // transform the texture coordinates
    gl_TexCoord[0] = gl_TextureMatrix[0] * gl_MultiTexCoord0;

    // forward the vertex color
    gl_FrontColor = gl_Color;
    
}