package com.ivvlev.car.framework.json;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class JsonArgsBuilder {
    private List<JsonArg> mArgs = new ArrayList<JsonArg>();
    private final Gson mGson = new Gson();

    public JsonArgsBuilder put(String name, String value) {
        return putObject(name, "String", value);
    }

    public JsonArgsBuilder put(String name, boolean value) {
        return putObject(name, "boolean", value);
    }

    public JsonArgsBuilder put(String name, double value) {
        return putObject(name, "double", value);
    }

    public JsonArgsBuilder put(String name, long value) {
        return putObject(name, "long", value);
    }

    public JsonArgsBuilder putNull(String name) {
        return putObject(name, "Null", null);
    }

    public JsonArgsBuilder put(String name, Number value) {
        if (value == null) {
            return putNull(name);
        } else if (value instanceof Long) {
            return putObject(name, "Long", value);
        } else if (value instanceof Integer) {
            return putObject(name, "Integer", value);
        } else if (value instanceof Double) {
            return putObject(name, "Double", value);
        } else if (value instanceof BigDecimal) {
            return putObject(name, "BigDecimal", value);
        } else if (value instanceof BigInteger) {
            return putObject(name, "BigInteger", value);
        }
        throw new UnsupportedOperationException("Can not accept " + value.getClass().getCanonicalName());
    }

    private JsonArgsBuilder putObject(String name, String datatype, Object value) {
        mArgs.add(new JsonArg(name, datatype, value));
        return this;
    }

    public String toJsonString() {
        try {
            StringWriter stringWriter = new StringWriter();
            JsonWriter writer = null;
            writer = mGson.newJsonWriter(stringWriter);
            writer.beginArray();
            for (JsonArg arg : mArgs) {
                writer.beginObject();
                writer.name(arg.name);
                switch (arg.dataType) {
                    case "String":
                        writer.value((String) arg.value);
                        break;
                    case "boolean":
                        writer.value((boolean) arg.value);
                        break;
                    case "double":
                        writer.value((double) arg.value);
                        break;
                    case "long":
                        writer.value((long) arg.value);
                        break;
                    case "Long":
                        writer.value((Long) arg.value);
                        break;
                    case "Integer":
                        writer.value((Integer) arg.value);
                        break;
                    case "Double":
                        writer.value((Double) arg.value);
                        break;
                    case "BigDecimal":
                        writer.value((BigDecimal) arg.value);
                        break;
                    case "BigInteger":
                        writer.value((BigInteger) arg.value);
                        break;
                    case "Null":
                        writer.nullValue();
                        break;                }
                writer.endObject();
            }
            writer.endArray();
            return stringWriter.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    public JsonArg[] fromJsonString(String sJson) throws IOException {
        mArgs.clear();
        JsonReader reader = mGson.newJsonReader(new StringReader(sJson));
        try {
            reader.beginArray();
            while (reader.hasNext()) {
                reader.beginObject();
                while (reader.hasNext()) {
                    String name = reader.nextName();
                    switch (name) {
                        case "String":
                            put(name, reader.nextString());
                            break;
                        case "boolean":
                            put(name, reader.nextBoolean());
                            break;
                        case "double":
                            put(name, reader.nextDouble());
                            break;
                        case "long":
                            put(name, reader.nextLong());
                            break;
                        case "Long":
                            put(name, Long.valueOf(reader.nextLong()));
                            break;
                        case "Integer":
                            put(name, Integer.valueOf(reader.nextInt()));
                            break;
                        case "Double":
                            put(name, Double.valueOf(reader.nextDouble()));
                            break;
                        case "BigDecimal":
                            put(name, BigDecimal.valueOf(reader.nextDouble()));
                            break;
                        case "BigInteger":
                            put(name, BigInteger.valueOf(reader.nextLong()));
                            break;
                        case "Null":
                            putObject(name, "Null", null);
                            break;
                    }
                }
                reader.endObject();
            }
            reader.endArray();
        } finally {
            reader.close();
        }
        return toArgs();
    }

    public JsonArg[] toArgs() throws IOException {
        return mArgs.toArray(new JsonArg[0]);
    }
}
