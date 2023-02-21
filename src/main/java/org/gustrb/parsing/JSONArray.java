package org.gustrb.parsing;

import java.util.ArrayList;
import java.util.List;

// https://www.json.org/img/array.png
public class JSONArray {
    private List<JSONValue> values;

    public JSONArray() {
        this.values = new ArrayList<>();
    }

    public void insert(JSONValue val) {
        this.values.add(val);
    }

    public JSONValue get(int index) {
        return this.values.get(index);
    }
}
