package it.mmzitarosa.databasemanager.manager.sql;

import it.mmzitarosa.databasemanager.annotation.ForeignKey;
import it.mmzitarosa.databasemanager.annotation.Id;
import it.mmzitarosa.databasemanager.util.Util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;

public class SqlUtil {

    public static String tableNameFromClass(Class<?> classType) {
        return classType.getSimpleName();
    }

    public static String tableFromTablePath(String tablePath) {
        if (tablePath == null)
            return "";
        tablePath = tablePath.replace("'", "");
        if (tablePath.contains("."))
            return tablePath.substring(tablePath.lastIndexOf("." + 1));
        return tablePath;
    }

    public static String columnNameFromField(Field field) {
        if (!hasAnnotation(field, ForeignKey.class))
            return field.getName();
        if (field.getType() == List.class) {
            // TODO cosa fare in questo caso?
        }
        return "id" + Util.capitalize(field.getName());
    }

    public static String columnAliasFromField(Field field, String tablePath, boolean id) {
        if (tablePath == null)
            tablePath = "";
        else {
            tablePath = tablePath.replace("'", "");
            tablePath += ".";
        }
        return tablePath + columnNameFromField(field) + ((field.getAnnotation(Id.class) != null && id) ? "@Id" : "");
    }

    public static Field primaryKeyFieldFromClass(Class<?> tableClass) {
        for (Field field : tableClass.getDeclaredFields()) {
            if (hasAnnotation(field, Id.class))
                return field;
        }
        return null;
    }

    public static <T> Object valueFromField(Class<?> classType, Field field) throws SqlException {
        try {
            return valueFromField(field, classType.newInstance());
        } catch (IllegalAccessException | InstantiationException e) {
            throw new SqlException(e);
        }
    }

    public static Object valueFromField(Field field, Object object) throws SqlException {
        boolean accessibility = field.isAccessible();
        field.setAccessible(true);
        try {
            Object value = field.get(object);
            field.setAccessible(accessibility);
            return value;
        } catch (IllegalAccessException e) {
            throw new SqlException(e);
        }
    }

    public static boolean hasAnnotation(Field field, Class<? extends Annotation> annotationClass) {
        return field.getAnnotation(annotationClass) != null;
    }

}
