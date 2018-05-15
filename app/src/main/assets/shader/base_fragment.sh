precision mediump float;
varying vec2 textureCoordinate;
uniform sampler2D vTexture;
void main() {
    gl_FragColor = texture2D( vTexture, textureCoordinate );
}