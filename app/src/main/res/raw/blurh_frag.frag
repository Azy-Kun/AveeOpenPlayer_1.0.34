
precision mediump float;
precision mediump int;
precision lowp sampler2D;
precision lowp samplerCube;

//"in" attributes from our vertex shader
varying vec4 vColor;
varying vec2 vTexCoord;

//declare uniforms
uniform sampler2D u_texture;
uniform float resolutionW;
uniform float resolutionH;
uniform float radius;//the amount to blur, in pixels

void main() {
	vec4 sum = vec4(0.0);

	vec2 tc = vTexCoord;

	float blurW = radius/resolutionW;
	float blurH = radius/resolutionH;

	float hstep = 1.0;

    //apply blurring, using a 9-tap filter with predefined gaussian weights
	sum += texture2D(u_texture, vec2(tc.x - 4.0*blurW*hstep, tc.y)) * 0.0162162162;
	sum += texture2D(u_texture, vec2(tc.x - 3.0*blurW*hstep, tc.y)) * 0.0540540541;
	sum += texture2D(u_texture, vec2(tc.x - 2.0*blurW*hstep, tc.y)) * 0.1216216216;
	sum += texture2D(u_texture, vec2(tc.x - 1.0*blurW*hstep, tc.y)) * 0.1945945946;
	
	sum += texture2D(u_texture, vec2(tc.x, tc.y)) * 0.2270270270;
	
	sum += texture2D(u_texture, vec2(tc.x + 1.0*blurW*hstep, tc.y)) * 0.1945945946;
	sum += texture2D(u_texture, vec2(tc.x + 2.0*blurW*hstep, tc.y)) * 0.1216216216;
	sum += texture2D(u_texture, vec2(tc.x + 3.0*blurW*hstep, tc.y)) * 0.0540540541;
	sum += texture2D(u_texture, vec2(tc.x + 4.0*blurW*hstep, tc.y)) * 0.0162162162;

    gl_FragColor = sum;

}