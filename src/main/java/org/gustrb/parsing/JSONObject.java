package org.gustrb.parsing;

import java.util.HashMap;

// https://www.json.org/img/object.png
public class JSONObject {
    private HashMap<String, JSONValue> actualObject;

    public JSONObject() {
        this.actualObject = new HashMap<>();
    }

    public void insert(String key, JSONValue value) {
        this.actualObject.put(key, value);
    }

    public JSONValue get(String key) {
        return this.actualObject.get(key);
    }
}
