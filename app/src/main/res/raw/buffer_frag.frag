
precision mediump float;
precision mediump int;
precision lowp sampler2D;
precision lowp samplerCube;

//"in" attributes from our vertex shader
varying vec4 vColor;
varying vec2 vTexCoord;

//declare uniforms
uniform sampler2D u_texture;

void main() {
	vec4 color1 = texture2D(u_texture,vTexCoord);
	gl_FragColor = vColor * color1;
}