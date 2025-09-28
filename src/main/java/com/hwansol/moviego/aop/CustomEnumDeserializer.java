package com.hwansol.moviego.aop;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.util.Arrays;

public class CustomEnumDeserializer extends StdDeserializer<Enum<?>> implements ContextualDeserializer {

    public CustomEnumDeserializer() {
        this(null);
    }

    protected CustomEnumDeserializer(Class<?> vc) {
        super(vc);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Enum<?> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        String text = jsonParser.getText().toUpperCase();
        Class<? extends Enum> enumType = (Class<? extends Enum>) this._valueClass;

        return Arrays.stream(enumType.getEnumConstants())
                .filter(constant -> constant.name().equals(text))
                .findAny()
                .orElse(null);
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext deserializationContext, BeanProperty beanProperty) throws JsonMappingException {
        return new CustomEnumDeserializer(beanProperty.getType().getRawClass());
    }
}
