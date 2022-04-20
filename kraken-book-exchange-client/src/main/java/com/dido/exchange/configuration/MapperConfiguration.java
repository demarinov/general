package com.dido.exchange.configuration;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfiguration {

    public static ObjectMapper getObjectMapper() {
        JsonFactory factory = new JsonFactory();

        SimpleModule m = new SimpleModule();
        m.addSerializer(java.lang.CharSequence.class, new ToStringSerializer());
        ObjectMapper mapper = new ObjectMapper(factory);
        mapper.registerModule(m);
        return mapper;
    }
}
