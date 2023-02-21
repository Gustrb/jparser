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
        }

        return new Token(TokenType.NUMERIC_LITERAL, tokenValue);
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
        var sign = '-';
        var asciiValue = (int) this.currentCharacter;
        return (asciiValue >= 48 && asciiValue <= 57) || this.currentCharacter == sign;
    }

    public List<Token> collectTokens() {
        List<Token> tokens = new ArrayList<>();
        Token token = null;
        do {
            token = this.getNextToken();
            tokens.add(token);
        } while (token.getType() != TokenType.EOF);

        return tokens;
    }
}
