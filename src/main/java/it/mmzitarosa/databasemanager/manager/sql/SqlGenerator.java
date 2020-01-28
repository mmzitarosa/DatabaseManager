package it.mmzitarosa.databasemanager.manager.sql;

import it.mmzitarosa.databasemanager.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;

public class SqlGenerator {

    private final static int DEFAULT_SIZE = -1;
    private final static int INT_DEFAULT_SIZE = 11;
    private final static int LONG_DEFAULT_SIZE = 20;
    private final static int BOOLEAN_DEFAULT_SIZE = 1;
    private final static int STRING_DEFAULT_SIZE = 255;

    public static String createTableSql(Class<?> classType) throws SqlException {
        return "CREATE TABLE IF NOT EXISTS " +
                SqlUtil.tableNameFromClass(classType) +
                "(" +
                generateTableBodySQL(classType) +
                ");";
    }

    private static String generateTableBodySQL(Class<?> classType) throws SqlException {
        StringBuilder tableBuilder = new StringBuilder();
        StringBuilder constraintBuilder = new StringBuilder();

        for (Field field : classType.getDeclaredFields()) {
            String tableName = SqlUtil.tableNameFromClass(classType);
            String columnName = SqlUtil.columnNameFromField(field);
            tableBuilder.append(columnName); // nome colonna
            tableBuilder.append(getFieldType(field)); // tipo colonna
            boolean notNull = false;

            for (Annotation annotation : field.getAnnotations()) {
//              ID
                if (annotation instanceof Id) { // se è una chiave primaria
                    tableBuilder.append(" PRIMARY KEY");
                    notNull = true;

//              AUTO
                } else if (annotation instanceof Auto) { // se è auto_increment (e int)
                    if (field.getType() == int.class || field.getType() == Integer.class)
                        tableBuilder.append(" AUTO_INCREMENT");

//              REQUIRED
                } else if (annotation instanceof Required) {
                    tableBuilder.append(" NOT NULL");               // not null
                    notNull = true;

//              FOREIGN KEY
                } else if (annotation instanceof ForeignKey) {
                    Class<?> foreignKeyClass = field.getType();
                    String foreignKeyClassName = SqlUtil.tableNameFromClass(foreignKeyClass);
                    if ((field.getType() == List.class)) {
                        // TODO cosa fare qui?
                        throw new SqlException(field.getType().getSimpleName() + " type not yet implemented for foreign key.");
                    }

                    if (SqlUtil.primaryKeyFieldFromClass(foreignKeyClass) == null)
                        throw new SqlException("The " + foreignKeyClass.getSimpleName() + " class does not contain the @Id annotation on any field");

                    constraintBuilder.append("CONSTRAINT ")
                            .append(tableName)
                            .append("_")
                            .append(foreignKeyClassName)
                            .append("_id_fk")
                            .append(" FOREIGN KEY (")
                            .append(columnName)
                            .append(") REFERENCES ")
                            .append(foreignKeyClassName)
                            .append(" (id), ");

                } else if (annotation instanceof Size) {
                    // Niente da fare qui, da mettere per evitare di andare in eccezione
                } else {
                    throw new SqlException("Annotation not implemented.");
                }
            }

            field.setAccessible(true);
            Object value = SqlUtil.valueFromField(classType, field);

            if ((value != null) && !SqlUtil.hasAnnotation(field, Auto.class)) {
                tableBuilder.append(" DEFAULT ");               // default
                if (field.getType() == String.class) {
                    tableBuilder.append("'");
                    tableBuilder.append(value);
                    tableBuilder.append("'");
                } else {
                    tableBuilder.append(value);
                }
            }

            if (!notNull)
                tableBuilder.append(" NULL");

            tableBuilder.append(", ");
        }
        // aggiungo le foreign key
        tableBuilder.append(constraintBuilder.toString());
        return tableBuilder.toString().substring(0, tableBuilder.toString().length() - 2); // rimuovo al termine ", "
    }

    private static String getFieldType(Field field) throws SqlException {
        if (field == null)
            throw new SqlException("Field is null");
        int size = DEFAULT_SIZE;
        Size sizeAnnotation;
        if ((sizeAnnotation = field.getAnnotation(Size.class)) != null) {
            if ((size = sizeAnnotation.value()) <= 0) {
                throw new SqlException("Field size cannot be 0 or negative.");
            }
        }
        if (field.getAnnotation(ForeignKey.class) != null) {
            return getFieldType(SqlUtil.primaryKeyFieldFromClass(field.getType()));
        } else if (field.getType() == int.class
                || field.getType() == Integer.class) {
            if (size == DEFAULT_SIZE)
                size = INT_DEFAULT_SIZE;
            return " INT(" + size + ")";

        } else if (field.getType() == long.class || field.getType() == Long.class) {
            if (size == DEFAULT_SIZE)
                size = LONG_DEFAULT_SIZE;
            return " BIGINT(" + size + ")";

        } else if (field.getType() == boolean.class || field.getType() == Boolean.class) {
            return " TINYINT(" + BOOLEAN_DEFAULT_SIZE + ")";
        } else {
            if (size == DEFAULT_SIZE)
                size = STRING_DEFAULT_SIZE;
            return " VARCHAR(" + size + ")";
        }
    }

    private static String newSelect = "";
    private static String newJoinClause = "";

    public static String selectAndJoinSql(Class<?> classType) {
        newSelect = "";
        newJoinClause = "";
        selectSql(classType, null);
        newSelect = newSelect.substring(0, newSelect.length() - 2);

        String query = "SELECT " + newSelect + " FROM " + SqlUtil.tableNameFromClass(classType) + newJoinClause + ";";
        return query;
    }

    private static void selectSql(Class<?> classType, String tablePath) {
        String parentTable = (tablePath != null) ? tablePath.replace(".", "") : SqlUtil.tableNameFromClass(classType);
        String parentTableAlias = (tablePath != null) ? parentTable + "Alias" : parentTable;
        for (Field field : classType.getDeclaredFields()) {
            if (SqlUtil.hasAnnotation(field, ForeignKey.class) && !field.getName().equals(SqlUtil.tableFromTablePath(tablePath))) {
                parentTableAlias = (tablePath != null) ? parentTable + field.getName() + "Alias" : field.getName() + "Alias";
                if (SqlUtil.hasAnnotation(field, Required.class)) {
                    newJoinClause += " INNER";
                } else {
                    newJoinClause += " LEFT";
                }
                newJoinClause += " JOIN ";
                newJoinClause += SqlUtil.tableNameFromClass(field.getType()) + " " + parentTableAlias;
                newJoinClause += " ON ";
                newJoinClause += SqlUtil.columnAliasFromField(field, (tablePath == null) ? SqlUtil.tableNameFromClass(classType) : tablePath, false) + " = " + parentTableAlias + "." + SqlUtil.primaryKeyFieldFromClass(field.getType()).getName();

                selectSql(field.getType(), (tablePath != null) ? tablePath + "." + field.getName() : field.getName());
            } else {
                newSelect += parentTableAlias + "." + SqlUtil.columnNameFromField(field) + " '" + SqlUtil.columnAliasFromField(field, tablePath, true) + "', ";
            }

        }
    }

}
