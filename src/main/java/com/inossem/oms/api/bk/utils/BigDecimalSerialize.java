package com.inossem.oms.api.bk.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class BigDecimalSerialize extends JsonSerializer {
    @Override
    public void serialize(Object o, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (o != null && o instanceof BigDecimal) {
            BigDecimal bigDecimal = (BigDecimal) o;
            jsonGenerator.writeString(bigDecimal.setScale(2, RoundingMode.DOWN).toString());
        }
    }
}
