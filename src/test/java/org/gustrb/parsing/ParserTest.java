package org.gustrb.parsing;

import org.gustrb.lexing.Lexer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {
    private String AN_EMPTY_OBJECT = "{}";
    private String AN_EMPTY_ARRAY = "[]";
    private String A_TOTALLY_VALID_JSON = "{ \n\t\"hello\": \"world\",\n\t\"val\":123456\n }";
    private String ANOTHER_TOTALLY_VALID_JSON = "[ \"hello\", \"world\" ]";
    private String AN_ARRAY_OF_OBJECTS = "[{ \n\t\"hello\": \"world\",\n\t\"val\":1\n }, { \n\t\"hello\": \"world\",\n\t\"val\":2\n }]";
    private String AN_OBJECT_THAT_HAS_AN_ARRAY = "{ \"stuff\": [ 12, 14 ] }";
    private String AN_OBJECT_CONTAINING_NULL = "{ \"foo\": null, \"bar\": null }";
    private String AN_OBJECT_CONTAINING_TRUE_AND_FALSE = "{ \"foo\": true, \"bar\": false }";

    @Test()
    public void smokeTest() {
        var lexer = new Lexer("");
        var parser = new Parser(lexer);

        assertThrows(
                InvalidJSONException.class,
                () -> parser.parse()
        );
    }

    @Test()
    public void itShouldParseAnEmptyObject() {
        var lexer = new Lexer(AN_EMPTY_OBJECT);
        var parser = new Parser(lexer);
        var value = parser.parse();

        assertEquals(value.type, JSONValueType.OBJECT);
    }

    @Test()
    public void itShouldParseAnEmptyArray() {
        var lexer = new Lexer(AN_EMPTY_ARRAY);
        var parser = new Parser(lexer);
        var value = parser.parse();

        assertEquals(value.type, JSONValueType.ARRAY);
    }

    @Test()
    public void itShouldParseASimpleJsonObject() {
        var lexer = new Lexer(A_TOTALLY_VALID_JSON);
        var parser = new Parser(lexer);
        var value = parser.parse();

        assertEquals(value.type, JSONValueType.OBJECT);
        var obj = value.obj;

        assertEquals(obj.get("hello").type, JSONValueType.STRING);
        assertEquals(obj.get("hello").str, "world");

        assertEquals(obj.get("val").type, JSONValueType.NUMBER);
        assertEquals(obj.get("val").num, 123456);
    }

    @Test()
    public void itShouldParseASimpleJsonArray() {
        var lexer = new Lexer(ANOTHER_TOTALLY_VALID_JSON);
        var parser = new Parser(lexer);
        var value = parser.parse();

        assertEquals(value.type, JSONValueType.ARRAY);
        var arr = value.arr;

        assertEquals(arr.get(0).type, JSONValueType.STRING);
        assertEquals(arr.get(0).str, "hello");

        assertEquals(arr.get(1).type, JSONValueType.STRING);
        assertEquals(arr.get(1).str, "world");
    }

    @Test()
    public void itCanParseAnObjectContainingNull() {
        var lexer = new Lexer(AN_OBJECT_CONTAINING_NULL);
        var parser = new Parser(lexer);
        var value = parser.parse();

        assertEquals(value.type, JSONValueType.OBJECT);
        var obj = value.obj;

        assertEquals(obj.get("foo").type, JSONValueType.NULL);
        assertEquals(obj.get("bar").type, JSONValueType.NULL);
    }

    @Test()
    public void itCanParseAnObjectContainingTheBooleanLiterals() {
        var lexer = new Lexer(AN_OBJECT_CONTAINING_TRUE_AND_FALSE);
        var parser = new Parser(lexer);
        var value = parser.parse();

        assertEquals(value.type, JSONValueType.OBJECT);
        var obj = value.obj;

        assertEquals(obj.get("foo").type, JSONValueType.BOOLEAN);
        assertEquals(obj.get("foo").bool, true);

        assertEquals(obj.get("bar").type, JSONValueType.BOOLEAN);
        assertEquals(obj.get("bar").bool, false);
    }

    @Test()
    public void itCanParseAnArrayOfObjects() {
        var lexer = new Lexer(AN_ARRAY_OF_OBJECTS);
        var parser = new Parser(lexer);
        var value = parser.parse();

        assertEquals(value.type, JSONValueType.ARRAY);
        var arr = value.arr;

        var firstObject = arr.get(0);
        assertEquals(firstObject.type, JSONValueType.OBJECT);
        assertEquals(firstObject.obj.get("hello").str, "world");
        assertEquals(firstObject.obj.get("val").num, 1);


        var secondObject = arr.get(1);
        assertEquals(secondObject.type, JSONValueType.OBJECT);
        assertEquals(secondObject.obj.get("hello").str, "world");
        assertEquals(secondObject.obj.get("val").num, 2);
    }

    @Test()
    public void itCanParseAnObjectContainingAnArray() {
        var lexer = new Lexer(AN_OBJECT_THAT_HAS_AN_ARRAY);
        var parser = new Parser(lexer);
        var value = parser.parse();

        assertEquals(value.type, JSONValueType.OBJECT);
        var obj = value.obj;

        assertEquals(obj.get("stuff").type, JSONValueType.ARRAY);
        var arr = obj.get("stuff").arr;

        assertEquals(arr.get(0).num, 12);
        assertEquals(arr.get(1).num, 14);
    }
}
