package org.gustrb.parsing;

// https://www.json.org/img/value.png
public class JSONValue {

    public String str;
    public int num;
    public JSONObject obj;
    public JSONArray arr;
    public JSONValueType type;
    public boolean bool;

    public static JSONValue createObject(JSONObject obj) {
        var val = new JSONValue();
        val.type = JSONValueType.OBJECT;
        val.obj = obj;

        return val;
    }

    public static JSONValue createArray(JSONArray arr) {
        var val = new JSONValue();
        val.type = JSONValueType.ARRAY;
        val.arr = arr;

        return val;
    }

    public static JSONValue createStringLiteral(String val) {
        var value = new JSONValue();

        value.type = JSONValueType.STRING;
        value.str = val;
        return value;
    }

    public static JSONValue createNumericLiteral(int number) {
        var value = new JSONValue();

        value.type = JSONValueType.NUMBER;
        value.num = number;

        return value;
    }

    public static JSONValue createNull() {
        var value = new JSONValue();

        value.type = JSONValueType.NULL;

        return value;
    }

    public static JSONValue createBooleanLiteral(String strRepresentation) {
        var value = new JSONValue();

        value.type = JSONValueType.BOOLEAN;
        value.bool = Boolean.valueOf(strRepresentation);

        return value;
    }
}
