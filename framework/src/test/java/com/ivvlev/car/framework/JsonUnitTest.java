package com.ivvlev.car.framework;

import com.ivvlev.car.framework.json.JsonArg;
import com.ivvlev.car.framework.json.JsonArgsBuilder;

import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class JsonUnitTest {
    @Test
    public void toJsonString() throws IOException {
        JsonArgsBuilder builder = new JsonArgsBuilder();
        builder.put("long", 100500L);
        builder.put("double", 100500.105D);
        builder.put("Long", new Long(100500L));
        builder.put("Double", new Double(100500D));
        builder.put("BigDecimal", new BigDecimal(100500D));
        builder.put("boolean", true);
        String json = builder.toJsonString();

        System.out.println(json);

        JsonArg[] args = builder.fromJsonString(json);
        for (JsonArg jsonArg : args) {
            System.out.print(jsonArg.name);
            System.out.print(":");
            System.out.print(jsonArg.dataType);
            System.out.print("=");
            System.out.println(jsonArg.value);
        }
    }

    @Test
    public void GSON_toJsonString() throws IOException {
    }
}