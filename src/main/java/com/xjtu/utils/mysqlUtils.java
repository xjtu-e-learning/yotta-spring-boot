package com.xjtu.utils;


import com.xjtu.common.Config;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据库操作类，包括增删改查
 * @author 石磊
 * @serialData 2016年10月27日15:27:54
 * @category class of mysql operation
 * @version v1.0
 */
public class mysqlUtils {
    //加载驱动
    private final String DRIVER = "com.mysql.jdbc.Driver";
    //设置url等参数
    private final String URL = Config.MYSQL_URL;
    //定义数据库的连接
    private Connection connection;
    //定义sql语句的执行对象
    private PreparedStatement pStatement;
    //定义查询返回的结果集合
    private ResultSet resultset;

    public mysqlUtils() {
        try {
            Class.forName(DRIVER);//注册驱动
//    		connection = DriverManager.getConnection(URL,USERNAME,PASSWORD);//定义连接
            connection = DriverManager.getConnection(URL);//定义连接

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断表格是否存在
     * @param sql 语句
     * @return SQL语句执行成功返回true, 否则返回false
     * @throws SQLException
     */
    public ResultSet judge(String sql) throws SQLException {
        pStatement = connection.prepareStatement(sql);  //填充占位符
        ResultSet resultSet = pStatement.executeQuery(sql);
        return resultSet;
    }

    /**
     * 完成对数据库的增删改操作
     * @param sql 语句
     * @param params 传入的占位符，List集合
     * @return SQL语句执行成功返回true, 否则返回false
     * @throws SQLException
     */
    public boolean addDeleteModify(String sql, List<Object> params) throws SQLException {
        int result = -1;//设置为
        pStatement = connection.prepareStatement(sql);  //填充占位符
        int index = 1; //从第一个开始添加
        if (params != null && !params.isEmpty()) {
            for (int i = 0; i < params.size(); i++) {
                pStatement.setObject(index++, params.get(i));//填充占位符
            }
        }
        result = pStatement.executeUpdate();//执行成功将返回大于0的数
        return result > 0 ? true : false;
    }

    /**
     * 完成对数据库的增删改操作
     * @param sql 语句
     * @param params 传入的占位符，List集合
     * @return SQL语句执行成功返回true, 否则返回false
     * @throws SQLException
     */
    public int addGeneratedKey(String sql, List<Object> params) throws SQLException {
        int generatedKeyValue = 0;
        int result = -1;//设置为
        pStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);  //填充占位符
        int index = 1; //从第一个开始添加
        if (params != null && !params.isEmpty()) {
            for (int i = 0; i < params.size(); i++) {
                pStatement.setObject(index++, params.get(i));//填充占位符
            }
        }
        result = pStatement.executeUpdate(); // 执行成功将返回大于0的数
        //输出主键值
        resultset = pStatement.getGeneratedKeys();
        if(resultset.next()){
            generatedKeyValue = resultset.getInt(1);
        }
        return generatedKeyValue;
    }

    /**
     * 查询数据库，返回多条记录
     * @param sql 语句
     * @param params 占位符
     * @return list集合，包含查询的结果
     * @throws SQLException
     */
    public List<Map<String, Object>> returnMultipleResult(String sql, List<Object> params) throws SQLException {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        //填充占位符
        int index = 1;
        pStatement = connection.prepareStatement(sql);
        if (params != null && !params.isEmpty()) {
            for (int i = 0; i < params.size(); i++) {
                pStatement.setObject(index++, params.get(i));
            }
        }
        //执行SQL语句
        resultset = pStatement.executeQuery();
        //封装resultset成map类型
        ResultSetMetaData metaDate = resultset.getMetaData();//获取列信息,交给metaDate
        int columnlength = metaDate.getColumnCount();
        while (resultset.next()) {
            Map<String, Object> map = new HashMap<String, Object>();
            for (int i = 0; i < columnlength; i++) {
                String metaDateKey = metaDate.getColumnName(i + 1);//获取列名
                Object resultsetValue = resultset.getObject(metaDateKey);
                if (resultsetValue == null) {
                    resultsetValue = "";
                }
                map.put(metaDateKey, resultsetValue);
            }
            list.add(map);
        }
        return list;
    }

    /**
     * 通过反射机制访问数据库，并返回多条记录
     * @param sql 语句
     * @param params 占位符
     * @param tJavabean 会执行javabean类里面的toString方法
     * @return
     * @throws Exception
     */
    public <T> List<T> returnMultipleResult_Ref(String sql, List<Object> params, Class<T> tJavabean) throws Exception {
        List<T> list = new ArrayList<T>();
        int index = 1;
        pStatement = connection.prepareStatement(sql);
        if (params != null && !params.isEmpty()) {
            for (int i = 0; i < params.size(); i++) {
                pStatement.setObject(index, params.get(i));
            }           
        }
        resultset = pStatement.executeQuery(sql);
        //封装resultset
        ResultSetMetaData metaData = resultset.getMetaData();//取出列的信息
        int columnLength = metaData.getColumnCount();//获取列数
        while (resultset.next()) {
            T tResult = tJavabean.newInstance();//通过反射机制创建一个对象
            for (int i = 0; i < columnLength; i++) {
                String metaDataKey = metaData.getColumnName(i + 1);
                Object resultsetValue = resultset.getObject(metaDataKey);
                if (resultsetValue == null) {
                    resultsetValue = "";
                }
                Field field = tJavabean.getDeclaredField(metaDataKey);
                field.setAccessible(true);
                field.set(tResult, resultsetValue);
            }
            list.add(tResult);
        }
        return list;
    }
    /**
     * 注意在finally里面执行以下方法，关闭连接
     */
    public void closeconnection() {
        if (resultset != null) {
            try {
                resultset.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (pStatement != null) {
            try {
                pStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取指定位数的随机字符串(包含小写字母、大写字母、数字,0<length) 可有参数 int length
     */
    public String getRandomString() {
        int length = 17;
        //随机字符串的随机字符库
        String KeyString = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuffer sb = new StringBuffer();
        sb.append("_");
        int len = KeyString.length();
        for (int i = 0; i < length; i++) {
            sb.append(KeyString.charAt((int) Math.round(Math.random() * (len - 1))));
        }
        return sb.toString();
    }
}