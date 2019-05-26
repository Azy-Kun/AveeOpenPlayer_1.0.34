//"in" attributes
attribute vec2 Position;
attribute vec2 TexCoord;

//"out" varyings to our fragment shader
varying vec4 vColor;
varying vec2 vTexCoord;

const vec2 madd = vec2(0.5,0.5);

void main() {
    vColor = vec4(1.0, 1.0,1.0, 1.0);
    vTexCoord = Position.xy*madd+madd;
    gl_Position = vec4(Position.xy,0.0,1.0);
}