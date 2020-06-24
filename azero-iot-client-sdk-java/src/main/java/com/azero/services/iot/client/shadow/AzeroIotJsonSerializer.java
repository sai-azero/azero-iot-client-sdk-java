package com.azero.services.iot.client.shadow;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * This is a customized JSON serializer for the Jackson databind module. It is
 * used for serializing the device properties to be reported to the shadow.
 */
public class AzeroIotJsonSerializer extends JsonSerializer<AbstractAzeroIotDevice> {

    @Override
    public void serialize(AbstractAzeroIotDevice device, JsonGenerator generator, SerializerProvider provider)
            throws IOException, JsonProcessingException {
        generator.writeStartObject();

        try {
            for (String property : device.getReportedProperties().keySet()) {
                Field field = device.getReportedProperties().get(property);

                Object value = invokeGetterMethod(device, field);
                generator.writeObjectField(property, value);
            }
        } catch (IllegalArgumentException e) {
            throw new IOException(e);
        }

        generator.writeEndObject();
    }

    private static Object invokeGetterMethod(Object target, Field field) throws IOException {
        String fieldName = Character.toUpperCase(field.getName().charAt(0)) + field.getName().substring(1);
        String getter = "get" + fieldName;

        Method method;
        try {
            method = target.getClass().getMethod(getter);
        } catch (NoSuchMethodException | SecurityException e) {
            if (e instanceof NoSuchMethodException && boolean.class.equals(field.getType())) {
                getter = "is" + fieldName;
                try {
                    method = target.getClass().getMethod(getter);
                } catch (NoSuchMethodException | SecurityException ie) {
                    throw new IllegalArgumentException(ie);
                }
            } else {
                throw new IllegalArgumentException(e);
            }
        }

        Object value;
        try {
            value = method.invoke(target);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new IOException(e);
        }
        return value;
    }

}
