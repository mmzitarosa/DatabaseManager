package it.mmzitarosa.databasemanager.manager.sql;

import it.mmzitarosa.databasemanager.annotation.ForeignKey;
import it.mmzitarosa.databasemanager.annotation.Id;
import it.mmzitarosa.databasemanager.manager.DatabaseException;
import it.mmzitarosa.databasemanager.util.GsonManager;
import it.mmzitarosa.databasemanager.util.Util;
import org.json.JSONObject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public static String aliasName(String name) {
        name = name.replace("'", "");
        return name.replace(".", "") + "Alias";
    }

    public static String whereClauseFromMap(String tableName, Map<String, Object> condMap) {
        if (condMap == null || condMap.isEmpty())
            return "";
        StringBuilder whereClause = new StringBuilder();
        boolean firstTime = true;
        for (String key : condMap.keySet()) {
            if (firstTime) {
                whereClause.append(" WHERE ");
                firstTime = false;
            } else {
                whereClause.append(" AND ");
            }
            if (!key.contains("."))
                whereClause.append(tableName)
                        .append(".")
                        .append(key);
            else
                whereClause.append(SqlUtil.aliasName(key.substring(0, key.lastIndexOf("."))))
                        .append(".")
                        .append(key.substring(key.lastIndexOf(".") + 1));
            whereClause.append("=")
                    .append("?");

        }
        return whereClause.toString();
    }

    public static void fillPreparedStatement(PreparedStatement preparedStatement, Map<String, Object> condMap) throws DatabaseException {
        fillMultiplePreparedStatement(preparedStatement, condMap);
    }

    public static void fillMultiplePreparedStatement(PreparedStatement preparedStatement, Map<String, Object>... condMaps) throws DatabaseException {
        if (condMaps.length == 0)
            throw new DatabaseException("Null or empty json parameters.");
        int count = 0;
        for (Map<String, Object> condMap : condMaps) {
            for (String key : condMap.keySet()) {
                Object value = condMap.get(key);
                ++count;
                try {
                    preparedStatement.setObject(count, value);
                } catch (SQLException e) {
                    throw new DatabaseException(e);
                }
            }
        }
    }

    public static <T> List<T> resultSetToMap(ResultSet resultSet, Class<?> tableClass) throws SQLException, ClassNotFoundException {
        List<T> list = new ArrayList<>();
        while (resultSet.next()) {
            ResultSetMetaData metaData = resultSet.getMetaData();
            JSONObject jsonObject = new JSONObject();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                String columnName = metaData.getColumnLabel(i);
//                jsonObject.put(columnName, resultSet.getObject(i));
                String x = metaData.getColumnTypeName(i);
                String t = metaData.getTableName(i);
                fillJsonRecursively(jsonObject, columnName.split("\\."), 0, resultSet.getObject(i));
            }
            list.add((T) GsonManager.getInstance().fromJson(jsonObject.toString(), (Type) tableClass));
        }
        return list;
    }


    private static JSONObject fillJsonRecursively(JSONObject jsonObject, String[] explodedColumn, int index, Object value) {
        if ("".equalsIgnoreCase(explodedColumn[index]))
            return fillJsonRecursively(jsonObject, explodedColumn, index + 1, value);
        if (explodedColumn.length - 1 == index) { //ultima occorrenza
            return jsonObject.put(explodedColumn[index].endsWith("@Id") ? explodedColumn[index].replace("@Id", "") : explodedColumn[index], value);
        } else {
            if (!jsonObject.has(explodedColumn[index])) { // il json non contiene la colonna
                if (explodedColumn[explodedColumn.length - 1].endsWith("@Id") && value != null) {
                    jsonObject.put(explodedColumn[index], fillJsonRecursively(new JSONObject(), explodedColumn, index + 1, value));
                }
            } else {
                jsonObject.put(explodedColumn[index], fillJsonRecursively(jsonObject.getJSONObject(explodedColumn[index]), explodedColumn, index + 1, value));
            }
            return jsonObject;
        }
    }

}
