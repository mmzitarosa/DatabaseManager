package it.mmzitarosa.databasemanager.manager;

import it.mmzitarosa.databasemanager.annotation.*;
import it.mmzitarosa.databasemanager.io.StatusCode;
import it.mmzitarosa.databasemanager.manager.sql.SqlGenerator;
import it.mmzitarosa.databasemanager.util.GsonManager;
import it.mmzitarosa.databasemanager.util.GuitarBaseException;
import it.mmzitarosa.databasemanager.util.Util;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

abstract class DatabaseManager {

    abstract Class<?> setTable();

    private Class<?> tableClass;

    private String dbUrl;
    private String dbUsername;
    private String dbPassword;

    private String select = "";
    private String joinClause = "";

    DatabaseManager() throws GuitarBaseException {
        loadConfiguration();
        this.tableClass = setTable();

        String tableSql = SqlGenerator.createTableSql(tableClass);
        execute(tableSql);
        // crea select e join
        generateSelectAndJoin();
    }

    /**
     * SELECT
     */

    public <T> List<T> selectAll() throws Exception {
        return select(null);
    }

    protected <T> List<T> select(Map<String, Object> condMap) throws Exception {
        Connection connection;
        PreparedStatement preparedStatement;
        List<T> result;
        try {
            connection = connect();
            String whereClause = "";//generateWhereClause(jsonCond);
            String query = "SELECT " + select + " FROM " + getTableName() + joinClause + whereClause + ";";
            preparedStatement = connection.prepareStatement(query);
//            if (!"".equalsIgnoreCase(whereClause)) {
//                fillPreparedStatement(preparedStatement, jsonCond);
//            }
            System.out.println(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            result = resultSetToMap(resultSet);
        } catch (SQLException e) {
            throw new DatabaseException(e);
        } catch (ClassNotFoundException e) {
            return null;
        }
        return result;
    }

    private <T> List<T> resultSetToMap(ResultSet resultSet) throws SQLException, ClassNotFoundException {
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

    private JSONObject fillJsonRecursively(JSONObject jsonObject, String[] explodedColumn, int index, Object value) {
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

    private void generateSelectAndJoin() {
        generateSelectAndJoin(this.tableClass, null);
        select = select.substring(0, select.length() - 2);
    }

    private void generateSelectAndJoin(Class<?> tableClass, String tableName) {
        for (Field field : tableClass.getDeclaredFields()) {
            String fieldName = field.getName();
            String tableAlias = tableClass.getSimpleName();
            String columnAlias = "";
            String lastName = "";
            if (tableName != null) {
                tableAlias = getAlias(tableName);
                columnAlias = tableName.replace("'", "");
                if (columnAlias.contains("."))
                    lastName = columnAlias.substring(columnAlias.lastIndexOf(".") + 1);
                else
                    lastName = columnAlias;
            }

            if (field.getAnnotation(ForeignKey.class) != null && !fieldName.equals(lastName)) {
                if (tableName != null) {
                    fieldName = "'" + tableName + "." + fieldName + "'";
                }

                if (field.getAnnotation(Required.class) != null) {
                    joinClause += " INNER";
                } else {
                    joinClause += " LEFT";
                }
                joinClause += " JOIN ";
                joinClause += field.getType().getSimpleName() + " " + getAlias(fieldName);
                joinClause += " ON ";
                joinClause += tableAlias + "." + getColumnName(field) + " = " + getAlias(fieldName) + "." + getPrimaryKey(field.getType()).getName();

                generateSelectAndJoin(field.getType(), fieldName);

            } else {
                select += tableAlias + "." + getColumnName(field) + " '" + columnAlias.replace("'", "") + "." + getColumnName(field) + ((field.getAnnotation(Id.class) != null) ? "@Id" : "") + "', ";
            }
        }
    }

    private <T> void fillPreparedStatement(PreparedStatement preparedStatement, T object, Object foreignKey) throws DatabaseException {
        fillMultiplePreparedStatement(preparedStatement, object, foreignKey);
    }

    private <T> void fillMultiplePreparedStatement(PreparedStatement preparedStatement, T object, Object... foreignKeys) throws DatabaseException {
        int count = 0;
        for (Object foreignKey : foreignKeys) {
            for (Field field : object.getClass().getDeclaredFields()) {
                if (field.getAnnotation(Auto.class) == null) {
                    Object value;
                    try {
                        if (field.getAnnotation(ForeignKey.class) != null) {
                            if (field.getAnnotation(Required.class) != null && foreignKey == null)
                                throw new DatabaseException(field.getName() + " is required.");
                            else if (foreignKey == null)
                                continue;
                            else
                                value = foreignKey;
                        } else {
                            boolean accessible = field.isAccessible();
                            field.setAccessible(true);
                            value = field.get(object);
                            field.setAccessible(accessible);
                        }
                        ++count;
                        preparedStatement.setObject(count, value);
                    } catch (SQLException | IllegalAccessException e) {
                        throw new DatabaseException(e);
                    }
                }
            }
        }
//        Logger.i(readablePreparedStatement(preparedStatement));
    }


    /**
     * INSERT
     */

    public <T> int insert(T object, Object foreingKey) throws DatabaseException {
        Connection connection;
        PreparedStatement preparedStatement;
        try {
            connection = connect();
            String query = "INSERT INTO " + getTableName() + generateInsert(object, foreingKey) + ";";
            System.out.println(query);
            preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            fillPreparedStatement(preparedStatement, object, foreingKey);
            preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
        return -1;
    }

    private <T> String generateInsert(T object) throws DatabaseException {
        return generateInsert(object, null);
    }

    private <T> String generateInsert(T object, Object foreignKey) throws DatabaseException {
        StringBuilder insert = new StringBuilder();
        boolean firstTime = true;
        int fields = 0;
        insert.append(" (");
        for (Field field : object.getClass().getDeclaredFields()) {
            if (field.getAnnotation(Auto.class) == null) {
                if (field.getAnnotation(ForeignKey.class) != null) {
                    if (field.getAnnotation(Required.class) != null && foreignKey == null)
                        throw new DatabaseException(field.getName() + " is required.");
                    else if (foreignKey == null)
                        continue;
                }

                if (firstTime) {
                    firstTime = false;
                } else {
                    insert.append(", ");
                }
                insert.append(getColumnName(field));
                fields++;
            }
        }
        insert.append(") VALUES (");
        for (int i = 0; i < fields; i++) {
            if (i > 0) {
                insert.append(", ");
            }
            insert.append("?");
        }
        insert.append(")");
        return insert.toString();
    }

    /**
     * UTILITY
     */

    public String getTableName() {
        return tableClass.getSimpleName();
    }

    private static String getColumnName(Field field) {
        if (field.getAnnotation(ForeignKey.class) == null)
            return field.getName();
        if (field.getType() == List.class) {
            // TODO cosa fare in questo caso?
        }
        return "id" + Util.capitalize(field.getName());
    }

    private Field getPrimaryKey(Class<?> tableClass) {
        for (Field field : tableClass.getDeclaredFields()) {
            if (field.getAnnotation(Id.class) != null)
                return field;
        }
        return null;
    }

    private static String getAlias(String string) {
        string = string.replace("'", "");
        return string.replace(".", "") + "Alias";
    }

    /**
     * DATABASE
     */

    private void loadConfiguration() throws DatabaseException {
        ResourceBundle resource = ResourceBundle.getBundle("configuration");
        String dbIp = resource.getString("db.ip");
        String dbPort = resource.getString("db.port");
        String dbName = resource.getString("db.name");
        dbUrl = "jdbc:mariadb://" + dbIp + ":" + dbPort + "/" + dbName;
        dbUsername = resource.getString("db.username");
        dbPassword = resource.getString("db.password");

        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new DatabaseException(e);
        }
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
    }

    private void disconnect(Connection connection, PreparedStatement preparedStatement, ResultSet resultSet) throws DatabaseException {
        try {
            if (resultSet != null) {
                resultSet.close();
            }

            if (preparedStatement != null) {
                preparedStatement.close();
            }

            if (connection != null) {
                connection.close();
            }

        } catch (SQLException e) {
            throw new DatabaseException(StatusCode.DATABASE_ERROR, e);
        }
    }

    private void execute(String string) throws DatabaseException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = connect();
            preparedStatement = connection.prepareStatement(string);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            this.disconnect(connection, preparedStatement, null);
            throw new DatabaseException(StatusCode.DATABASE_ERROR, e);
        }

        this.disconnect(connection, preparedStatement, null);
    }


}
