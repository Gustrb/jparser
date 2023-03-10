package org.gustrb.lexing;

public class Token {
    private TokenType type;
    private String value;

    public Token(TokenType type) {
        this.type = type;
    }

    public Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return this.value;
    }

    public int getNumericalValue() {
       return Integer.parseInt(this.value);
    }

    public float getFloatingPointValue() {
        return Float.parseFloat(this.value);
    }
}
