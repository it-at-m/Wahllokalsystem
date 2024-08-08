package de.muenchen.oss.wahllokalsystem.basisdatenservice.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ObjectPropertyChecker {

    public static Boolean objectHasProperty(Object obj, String propertyName) {
        List<Field> properties = getAllFields(obj);
        for (Field field : properties) {
            if (field.getName().equalsIgnoreCase(propertyName)) {
                return true;
            }
        }
        return false;
    }

    public static Object getValueFromField(Object theObjectInstance, String propertyName) throws NoSuchFieldException, IllegalAccessException {
        return theObjectInstance.getClass().getField(propertyName).get(propertyName);
    }

    private static List<Field> getAllFields(Object obj) {
        List<Field> fields = new ArrayList<>();
        getAllFieldsRecursive(fields, obj.getClass());
        return fields;
    }

    private static List<Field> getAllFieldsRecursive(List<Field> fields, Class<?> type) {
        fields.addAll(Arrays.asList(type.getDeclaredFields()));

        if (type.getSuperclass() != null) {
            fields = getAllFieldsRecursive(fields, type.getSuperclass());
        }

        return fields;
    }
}