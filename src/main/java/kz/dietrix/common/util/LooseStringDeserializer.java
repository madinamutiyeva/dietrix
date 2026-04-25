package kz.dietrix.common.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.List;

/**
 * Lenient String deserializer — tolerates frontends that send a field as either:
 *   - a plain string:   "DARK"
 *   - an object with one of common label/value keys:
 *         { "value": "DARK" }   { "name": "DARK" }   { "id": "DARK" }
 *         { "code": "DARK" }    { "label": "DARK" }  { "key": "DARK" }
 *   - a number / boolean → toString
 *   - null → null
 *
 * Useful for enum-like settings fields where UI libraries (e.g. react-select)
 * often emit the whole option object instead of the raw value.
 */
public class LooseStringDeserializer extends JsonDeserializer<String> {

    private static final List<String> KEYS = List.of("value", "name", "id", "code", "label", "key");

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonToken t = p.currentToken();
        if (t == null || t == JsonToken.VALUE_NULL) return null;
        if (t == JsonToken.VALUE_STRING) return p.getValueAsString();
        if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT
                || t == JsonToken.VALUE_TRUE || t == JsonToken.VALUE_FALSE) {
            return p.getValueAsString();
        }
        if (t == JsonToken.START_OBJECT) {
            JsonNode node = p.readValueAsTree();
            for (String key : KEYS) {
                JsonNode v = node.get(key);
                if (v != null && !v.isNull() && v.isValueNode()) {
                    return v.asText();
                }
            }
            return null;
        }
        if (t == JsonToken.START_ARRAY) {
            // first scalar element of an array
            JsonNode node = p.readValueAsTree();
            if (node.size() > 0 && node.get(0).isValueNode()) {
                return node.get(0).asText();
            }
            return null;
        }
        return null;
    }
}

