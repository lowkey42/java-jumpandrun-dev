#version 130
precision lowp float;

uniform sampler2D texture;
uniform sampler2D noiseTexture;

uniform vec2 size;
uniform vec2 waveSize;
uniform vec2 noiseOffsetFac;
uniform vec4 secColor;

void main() {
	vec4 color = texture2D(texture, gl_TexCoord[0].xy);
	
	if( color.a<=0.0 ) {
	    float noise = texture2D(noiseTexture, vec2( 
		    (gl_TexCoord[0].x-0.5)*noiseOffsetFac.x + (gl_TexCoord[0].y-0.5)*noiseOffsetFac.y + 0.5, 
		    (gl_TexCoord[0].x-0.5)*noiseOffsetFac.y + (gl_TexCoord[0].y-0.5)*noiseOffsetFac.x) + 0.5 ).r;
	    vec2 nsize = (size+ clamp(noise*2.0 -1.0, 0.0, 1.0) * waveSize) / 5.0;
		vec2 offx = vec2(max(nsize.x, 0.0), 0.0);
		vec2 offy = vec2(0.0, max(nsize.y, 0.0));
	    
		float aa = 0.0;
	
		for ( int i = 1; i <= 5; ++i ) {
			float fi = float(i);
			vec2 roffx = offx * fi;
			vec2 roffy = offy * fi;
	
			if( texture2D(texture, gl_TexCoord[0].xy - roffx).a > 0.0
				|| texture2D(texture, gl_TexCoord[0].xy + roffx).a > 0.0
				|| texture2D(texture, gl_TexCoord[0].xy - roffy).a > 0.0
				|| texture2D(texture, gl_TexCoord[0].xy + roffy).a > 0.0
				|| texture2D(texture, gl_TexCoord[0].xy - roffx - roffy).a > 0.0
				|| texture2D(texture, gl_TexCoord[0].xy - roffx + roffy).a > 0.0
			    || texture2D(texture, gl_TexCoord[0].xy + roffx - roffy).a > 0.0
			    || texture2D(texture, gl_TexCoord[0].xy + roffx + roffy).a > 0.0 ) {
				aa+= 1.0 / (fi*max(1.0, fi));
			}
	    }
	    
	    float x = min(1.0, aa-0.2);
	    
		gl_FragColor = (gl_Color*x + secColor*(1.0-x)) 
						* vec4(1.0, 1.0, 1.0, min(1.0, aa));
		
	} else {
	    gl_FragColor = vec4(0.0);
	}
}