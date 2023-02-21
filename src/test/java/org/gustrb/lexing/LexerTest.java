package org.gustrb.lexing;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LexerTest {
    private String EMPTY_CONTENT = "";
    private String A_SHITLOAD_OF_EMPTY_SPACE = " \n\t ";
    private String A_TOTALLY_VALID_JSON = "{ \n\t\"hello\": \"world\",\n\t\"val\":123456\n }";

    @Test()
    public void smokeTest() {
        var content = "this is a totally valid json content";
        assertDoesNotThrow(() -> new Lexer(content));
    }

    @Test()
    public void itShouldReturnEofIfTheStringIsEmpty() {
        var lexer = new Lexer(EMPTY_CONTENT);
        assertEquals(lexer.getNextToken().getType(), TokenType.EOF);
    }

    @Test()
    public void itShouldBeAbleToLexAllTheBasicCharacters() {
        var thingsToLex = new String[]{"{", "}", "[", "]", ":", ","};
        var expectedTokenTypes = new TokenType[]{
                TokenType.OPEN_BRACKETS,
                TokenType.CLOSING_BRACKETS,
                TokenType.OPEN_BRACES,
                TokenType.CLOSING_BRACES,
                TokenType.SEMI_COLON,
                TokenType.COMMA
        };

        for (int i = 0; i < expectedTokenTypes.length; i++) {
            var lexer = new Lexer(thingsToLex[i]);
            var token = lexer.getNextToken();

            assertEquals(token.getType(), expectedTokenTypes[i]);
        }
    }

    @Test()
    public void itShouldBeAbleToLexMoreThanOneToken() {
        var MORE_THAN_ONE_TOKEN = "{}[]";
        var expectedTokens = new TokenType[]{
                TokenType.OPEN_BRACKETS,
                TokenType.CLOSING_BRACKETS,
                TokenType.OPEN_BRACES,
                TokenType.CLOSING_BRACES,
                TokenType.EOF
        };
        var lexer = new Lexer(MORE_THAN_ONE_TOKEN);
        var tokens = collectTokensFromLexer(lexer);

        for (int i = 0; i < expectedTokens.length; i++) {
            assertEquals(expectedTokens[i], tokens.get(i).getType());
        }
    }

    @Test()
    public void itShouldReturnOnlyEofWhenLexingWhitespaces() {
        var lexer = new Lexer(A_SHITLOAD_OF_EMPTY_SPACE);
        var tokens = collectTokensFromLexer(lexer);

        assertEquals(tokens.size(), 1);
        assertEquals(tokens.get(0).getType(), TokenType.EOF);
    }

    @Test()
    public void itShouldBeAbleToLexAStringLiteral() {
        var STRING_LITERAL = "\"Hello, world!\"";
        var lexer = new Lexer(STRING_LITERAL);
        var tokens = collectTokensFromLexer(lexer);

        assertEquals(tokens.size(), 2);
        var token = tokens.get(0);
        assertEquals(token.getType(), TokenType.STRING_LITERAL);
        assertEquals(token.getValue(), "Hello, world!");
    }

    @Test()
    public void itShouldBeAbleToLexANumericLiteral() {
        var NUMERIC_LITERAL = "1234567890";
        var lexer = new Lexer(NUMERIC_LITERAL);
        var tokens = collectTokensFromLexer(lexer);

        assertEquals(tokens.size(), 2);
        var token = tokens.get(0);

        assertEquals(token.getType(), TokenType.NUMERIC_LITERAL);
        assertEquals(token.getNumericalValue(), 1234567890);
    }

    @Test()
    public void itShouldBeAbleToLexAValidJson() {
        var lexer = new Lexer(A_TOTALLY_VALID_JSON);
        /*
         * {
         *   "hello": "world",
         *   "val": 123456
         * }
         * */
        var expectedTokens = List.of(
                new Token(TokenType.OPEN_BRACKETS),
                new Token(TokenType.STRING_LITERAL, "hello"),
                new Token(TokenType.SEMI_COLON),
                new Token(TokenType.STRING_LITERAL, "world"),
                new Token(TokenType.COMMA),
                new Token(TokenType.STRING_LITERAL, "val"),
                new Token(TokenType.SEMI_COLON),
                new Token(TokenType.NUMERIC_LITERAL, "123456"),
                new Token(TokenType.CLOSING_BRACKETS),
                new Token(TokenType.EOF)
        );
        var got = collectTokensFromLexer(lexer);
        expectTokensEquality(expectedTokens, got);
    }

    private List<Token> collectTokensFromLexer(Lexer lexer) {
        List<Token> tokens = new ArrayList<>();
        Token token = null;
        do {
            token = lexer.getNextToken();
            tokens.add(token);
        } while (token.getType() != TokenType.EOF);

        return tokens;
    }

    private void expectTokensEquality(List<Token> expected, List<Token> got) {
        assertEquals(expected.size(), got.size());
        for (int i = 0; i < expected.size(); i++) {
            var expectedToken = expected.get(i);
            var gotToken = got.get(i);

            assertEquals(expectedToken.getType(), gotToken.getType());
            assertEquals(expectedToken.getValue(), gotToken.getValue());
        }
    }
}
