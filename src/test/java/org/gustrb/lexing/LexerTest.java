package org.gustrb.lexing;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LexerTest {
    private String EMPTY_CONTENT = "";
    private String A_SHITLOAD_OF_EMPTY_SPACE = " \n\t ";
    private String A_TOTALLY_VALID_JSON = "{ \n\t\"hello\": \"world\",\n\t\"val\":123456\n }";
    private String A_NULL_VALUE = "null";
    private String THE_BOOLEAN_LITERALS = "true false";
    private String SOME_NUMBERS = "-100 100 -4";
    private String A_FLOATING_POINT_NUMBER = "10e4";

    private String FLOATING_POINT_NUMBERS = "10e2 10.2 100 -14.2";

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
        var tokens = lexer.collectTokens();

        for (int i = 0; i < expectedTokens.length; i++) {
            assertEquals(expectedTokens[i], tokens.get(i).getType());
        }
    }

    @Test()
    public void itShouldReturnOnlyEofWhenLexingWhitespaces() {
        var lexer = new Lexer(A_SHITLOAD_OF_EMPTY_SPACE);
        var tokens = lexer.collectTokens();

        assertEquals(tokens.size(), 1);
        assertEquals(tokens.get(0).getType(), TokenType.EOF);
    }

    @Test()
    public void itShouldBeAbleToLexAStringLiteral() {
        var STRING_LITERAL = "\"Hello, world!\"";
        var lexer = new Lexer(STRING_LITERAL);
        var tokens = lexer.collectTokens();

        assertEquals(tokens.size(), 2);
        var token = tokens.get(0);
        assertEquals(token.getType(), TokenType.STRING_LITERAL);
        assertEquals(token.getValue(), "Hello, world!");
    }

    @Test()
    public void itShouldBeAbleToLexIntegers() {
        var lexer = new Lexer(SOME_NUMBERS);
        var tokens = lexer.collectTokens();
        var numbers = new int[]{-100, 100, -4};
        assertEquals(tokens.size(), 4);
        for (var i = 0; i < 3; i++) {
            var token = tokens.get(i);
            assertEquals(token.getType(), TokenType.NUMERIC_LITERAL);
            assertEquals(token.getNumericalValue(), numbers[i]);
        }
    }

    @Test()
    public void itShouldBeAbleToLexANumericLiteral() {
        var NUMERIC_LITERAL = "1234567890";
        var lexer = new Lexer(NUMERIC_LITERAL);
        var tokens = lexer.collectTokens();

        assertEquals(tokens.size(), 2);
        var token = tokens.get(0);

        assertEquals(token.getType(), TokenType.NUMERIC_LITERAL);
        assertEquals(token.getNumericalValue(), 1234567890);
    }

    @Test()
    public void itShouldBeAbleToLexAFloat() {
        var lexer = new Lexer(A_FLOATING_POINT_NUMBER);
        var tokens = lexer.collectTokens();

        assertEquals(2, tokens.size());
        var token = tokens.get(0);

        assertEquals(token.getType(), TokenType.FLOATING_POINT_LITERAL);
        assertEquals(token.getFloatingPointValue(), 10e4f);
    }

    @Test()
    public void itShouldBeAbleToLexAllTheTypesOfNumbers() {
        var lexer = new Lexer(FLOATING_POINT_NUMBERS);
        var tokens = lexer.collectTokens();

        assertEquals(5, tokens.size());

        assertEquals(tokens.get(0).getFloatingPointValue(), 10e2f);
        assertEquals(tokens.get(1).getFloatingPointValue(), 10.2f);
        assertEquals(tokens.get(2).getNumericalValue(), 100);
        assertEquals(tokens.get(3).getFloatingPointValue(), -14.2f);
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
        var got = lexer.collectTokens();
        expectTokensEquality(expectedTokens, got);
    }

    @Test()
    public void itCanLexTheNullToken() {
        var lexer = new Lexer(A_NULL_VALUE);
        var expectedTokens = List.of(
                new Token(TokenType.NULL_TOKEN),
                new Token(TokenType.EOF)
        );
        var got = lexer.collectTokens();

        expectTokensEquality(expectedTokens, got);
    }

    @Test()
    public void itCanLexTheBooleanLiterals() {
        var lexer = new Lexer(THE_BOOLEAN_LITERALS);
        var expectedTokens = List.of(
                new Token(TokenType.BOOLEAN_LITERAL, "true"),
                new Token(TokenType.BOOLEAN_LITERAL, "false"),
                new Token(TokenType.EOF)
        );
        var got = lexer.collectTokens();

        expectTokensEquality(expectedTokens, got);
    }

    private void expectTokensEquality(List<Token> expected, List<Token> got) {
        assertEquals(expected.size(), got.size());
        for (var i = 0; i < expected.size(); i++) {
            var expectedToken = expected.get(i);
            var gotToken = got.get(i);

            assertEquals(expectedToken.getType(), gotToken.getType());
            assertEquals(expectedToken.getValue(), gotToken.getValue());
        }
    }
}
