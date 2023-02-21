package org.gustrb.parsing;

import org.gustrb.lexing.Lexer;
import org.gustrb.lexing.Token;
import org.gustrb.lexing.TokenType;

import java.util.List;

/**
 * TODO: Implement error handling on invalid jsons
 * TODO: Implement string serialization
 * TODO: Refactor all of this mess
 */
public class Parser {
    private List<Token> tokens;
    private int currentIndex;
    private Token currentToken;

    public Parser(Lexer lexer) {
         this.tokens = lexer.collectTokens();
         this.currentIndex = -1;
         this.currentToken = null;
    }

    public JSONValue parse() {
        if (this.isEmptyTokenList()) {
            throw new InvalidJSONException();
        }

        advanceCursor();

        return this.parseValue();
    }

    private JSONObject parseObject() {
        var obj = new JSONObject();

        while (this.currentToken.getType() != TokenType.CLOSING_BRACKETS && this.currentToken.getType() != TokenType.EOF) {
            var key = this.currentToken.getValue();
            advanceCursor();
            advanceCursor();

            var value = this.parseValue();
            advanceCursor();
            obj.insert(key, value);

            if (this.currentToken.getType() == TokenType.COMMA) {
                advanceCursor();
            }
        }

        return obj;
    }

    private JSONValue parseValue() {
        if (this.currentToken.getType() == TokenType.STRING_LITERAL) {
            return JSONValue.createStringLiteral(this.currentToken.getValue());
        }

        if (this.currentToken.getType() == TokenType.NUMERIC_LITERAL) {
            return JSONValue.createNumericLiteral(this.currentToken.getNumericalValue());
        }

        if (this.currentToken.getType() == TokenType.FLOATING_POINT_LITERAL) {
            return JSONValue.createFloatingPointLiteral(this.currentToken.getFloatingPointValue());
        }

        if (this.currentToken.getType() == TokenType.BOOLEAN_LITERAL) {
            return JSONValue.createBooleanLiteral(this.currentToken.getValue());
        }

        if (this.currentToken.getType() == TokenType.NULL_TOKEN) {
            return JSONValue.createNull();
        }

        if (this.currentToken.getType() == TokenType.OPEN_BRACKETS) {
            advanceCursor();
            var obj = JSONValue.createObject(this.parseObject());
            advanceCursor();
            return obj;
        }

        if (this.currentToken.getType() == TokenType.OPEN_BRACES) {
            advanceCursor();
            var arr = JSONValue.createArray(this.parseArray());
            advanceCursor();
            return arr;
        }

        return null;
    }

    private JSONArray parseArray() {
        var arr = new JSONArray();

        while (this.currentToken.getType() != TokenType.CLOSING_BRACES && this.currentToken.getType() != TokenType.EOF) {
            var val = this.parseValue();
            arr.insert(val);

            advanceCursor();

            if (this.currentToken.getType() == TokenType.COMMA) {
                advanceCursor();
            }
        }

        return arr;
    }

    private void advanceCursor() {
        this.currentIndex++;
        if (this.currentIndex < this.tokens.size()) {
            this.currentToken = this.tokens.get(this.currentIndex);
        }
    }

    private boolean isEmptyTokenList() {
        return this.tokens.get(0).getType() == TokenType.EOF;
    }
}
