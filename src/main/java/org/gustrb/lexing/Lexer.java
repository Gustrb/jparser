package org.gustrb.lexing;

import java.util.ArrayList;
import java.util.List;

public class Lexer {
    private char[] contentToLex;
    private int currentIndex;
    private char currentCharacter;

    // TODO: Add current row and column to have error tracking
    //       I didn't do it because I'm lazy and that sounds awful to implement
    //       and debug

    public Lexer(String contentToLex) {
        this.contentToLex = contentToLex.toCharArray();
        this.currentIndex = -1;
        this.currentCharacter = '\0';
    }

    public Token getNextToken() {
        advanceCursor();

        while (this.currentIndex < this.contentToLex.length) {
            if (this.isWhitespace()) {
                this.advanceCursor();
                continue;
            }

            if (this.currentCharacter == '{') {
                return new Token(TokenType.OPEN_BRACKETS);
            }

            if (this.currentCharacter == '}') {
                return new Token(TokenType.CLOSING_BRACKETS);
            }

            if (this.currentCharacter == '[') {
                return new Token(TokenType.OPEN_BRACES);
            }

            if (this.currentCharacter == ']') {
                return new Token(TokenType.CLOSING_BRACES);
            }

            if (this.currentCharacter == ':') {
                return new Token(TokenType.SEMI_COLON);
            }

            if (this.currentCharacter == ',') {
                return new Token(TokenType.COMMA);
            }

            if (this.currentCharacter == '\"') {
                return this.getStringLiteralToken();
            }

            if (this.currentCharacter == 'n') {
                return this.getNullToken();
            }

            if (this.currentCharacter == 't' || this.currentCharacter == 'f') {
                return this.getBooleanLiteral();
            }

            if (this.isNumericLiteral()) {
               return this.getNumericLiteral();
            }

            throw new RuntimeException("[Lexer.getNextToken] TODO: Implement token " + this.currentCharacter);
        }

        return new Token(TokenType.EOF);
    }

    private Token getBooleanLiteral() {
        var literal = this.advanceTo(this.currentIndex + 4);
        if (literal.equals("true")) {
            return new Token(TokenType.BOOLEAN_LITERAL, "true");
        }

        literal += this.advanceTo(this.currentIndex + 1);
        if (literal.equals("false")) {
            return new Token(TokenType.BOOLEAN_LITERAL, "false");
        }

        return null;
    }

    private Token getNullToken() {
        var literal = this.advanceTo(this.currentIndex + 4);
        if (literal.equals("null")) {
            return new Token(TokenType.NULL_TOKEN);
        }
        return null;
    }

    private String advanceTo(int end) {
        var content = "";
        for (var i = this.currentIndex; i < end; i++) {
            content += this.currentCharacter;
            this.advanceCursor();
        }
        return content;
    }

    private Token getStringLiteralToken() {
        // Skipping the opening "
        this.advanceCursor();
        var tokenValue = "";

        while (this.currentCharacter != '\"' && this.currentIndex < this.contentToLex.length) {
            tokenValue += this.currentCharacter;
            this.advanceCursor();
        }

        return new Token(TokenType.STRING_LITERAL, tokenValue);
    }

    private Token getNumericLiteral() {
        var tokenValue = "";

        while (this.isNumericLiteral() && this.currentIndex < this.contentToLex.length) {
            tokenValue += this.currentCharacter;
            this.advanceCursor();

            if (this.currentCharacter == '.' || this.currentCharacter == 'e') {
                return this.getFloatingPointLiteral(tokenValue);
            }
        }

        return new Token(TokenType.NUMERIC_LITERAL, tokenValue);
    }

    private Token getFloatingPointLiteral(String partialValue) {
        partialValue += this.currentCharacter;
        advanceCursor();
        while (this.isNumericLiteral() && this.currentIndex < this.contentToLex.length) {
            partialValue += this.currentCharacter;
            advanceCursor();
        }

        return new Token(TokenType.FLOATING_POINT_LITERAL, partialValue);
    }

    private void advanceCursor() {
        this.currentIndex++;
        if (this.currentIndex < this.contentToLex.length) {
            this.currentCharacter = this.contentToLex[this.currentIndex];
        }
    }

    private boolean isWhitespace() {
        return this.currentCharacter == '\n'
                || this.currentCharacter == '\t'
                || this.currentCharacter == ' ';
    }

    private boolean isNumericLiteral() {
        // TODO: A number can only contain a - in the beginning
        //       if it is located in the middle of it then an error should be
        //       thrown
        var sign = '-';
        var asciiValue = (int) this.currentCharacter;
        return (asciiValue >= 48 && asciiValue <= 57) || this.currentCharacter == sign;
    }

    public List<Token> collectTokens() {
        List<Token> tokens = new ArrayList<>();
        Token token;
        do {
            token = this.getNextToken();
            tokens.add(token);
        } while (token.getType() != TokenType.EOF);

        return tokens;
    }
}
