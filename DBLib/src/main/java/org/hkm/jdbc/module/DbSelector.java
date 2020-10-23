package org.hkm.jdbc.module;

import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.GenerousBeanProcessor;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;
/**
 *
 * @author kangmoo Heo
 */
public class DbSelector {
    Logger logger = LoggerFactory.getLogger(DbSelector.class);
    private QueryRunner runner;

    public DbSelector(QueryRunner runner) {
        this.runner = runner;
    }

    /**
     * Query 실행 후 결과 String 으로 반환
     * @param sql query string
     * @return string
     */
    public String doQueryResultAsString(String sql, Connection con){
        String result = null;
        ResultSet rs = null;
        Statement stmt = null;
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);
            result = getResultStringFromResultSet(rs);
        } catch (SQLException e) {
            logger.error("SQL Syntax Error", e);
        } catch (Exception e) {
            logger.error("doQueryResultAsString() EXCEPTION", e);
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (Exception e) {
                logger.error("close() EXCEPTION", e);
            }
            try {
                if( rs != null ) rs.close();
            } catch (SQLException e) {
                logger.error("rs.close() EXCEPTION", e);
            }
        }
        return result;
    }

    /**
     * SQL Query 실행 후 결과를 <T>로 변환하여 반환
     * @param sql query string
     * @param clazz class
     * @param params input params or null
     * @return obj
     */
    public <T> T doQueryResultAsObj(String sql, Class<T> clazz, Object ... params) {
        try {
            ResultSetHandler<T> rsh = new BeanHandler<>(clazz,
                    new BasicRowProcessor(new GenerousBeanProcessor()));
            return runner.query(sql, rsh, params);
        } catch (Exception e) {
            logger.error("doQueryResultAsObj() EXCEPTION", e);
            return null;
        }
    }

    /**
     * SQL Query 실행 후 결과를 List<T>로 변환하여 반환
     * @param sql query string
     * @param clazz class
     * @param params input params or null
     * @return obj list
     */
    public <T> List<T> doQueryResultAsListObj(String sql, Class<T> clazz, Object ... params) {
        try {
            ResultSetHandler<List<T>> rsh = new BeanListHandler<>(clazz,
                    new BasicRowProcessor(new GenerousBeanProcessor()));
            return runner.query(sql, rsh, params);
        } catch (Exception e) {
            logger.error("doQueryResultAsListObj() EXCEPTION", e);
            return Collections.emptyList();
        }
    }

    /**
     * SQL Query 실행 후 결과를 List<Map<String,Object>>으로 변환하여 반환
     * @param sql query string
     * @param params input params or null
     * @return list
     */
    public List<Map<String, Object>> doQueryResultAsListMap(String sql, Object ... params){
        try{
            return runner.query(sql, new MapListHandler(), params);
        } catch(Exception e){
            logger.error("doQueryResultAsListMap() EXCEPTION", e);
            return Collections.emptyList();
        }
    }

    /**
     * SQL Query 실행 후 결과를 List<Object[]>로 변환하여 반환
     * @param sql query string
     * @param params input params or null
     * @return list
     */
    public List<Object[]> doQueryResultAsListArray(String sql, Object ... params) {
        try {
            return runner.query(sql, new ArrayListHandler(), params);
        } catch (Exception e) {
            logger.error("doQueryResultAsListArray() EXCEPTION", e);
            return Collections.emptyList();
        }
    }

    /**
     * ResultSet을 받아 읽을 수 있는 String 반환
     * @param rs Query ResultSet
     * @return result string
     * @throws SQLException exception
     */
    public static String getResultStringFromResultSet(ResultSet rs) throws SQLException {
        StringBuilder sb = new StringBuilder();
        // 메터 정보
        ResultSetMetaData metaInfo = rs.getMetaData();
        // 컬럼 갯수
        int count = metaInfo.getColumnCount();

        // 컬럼명과 타입
        for (int i = 0; i < count; i++) {
            sb.append(metaInfo.getColumnName(i + 1)).append(" : ").append(metaInfo.getColumnTypeName(i + 1)).append("\n");
        }
        sb.append("\n");

        // 출력 결과 헤더
        sb.append("------------------------------------------------------------------------------\n");
        // 컬럼명
        for (int i = 0; i < count; i++) {
            sb.append(metaInfo.getColumnName(i + 1)).append("\t");
        }
        sb.append("\n------------------------------------------------------------------------------\n");

        // 결과값
        while (rs.next()) {
            // 레코드
            for (int i = 0; i < count; i++) {
                sb.append(rs.getString(i + 1)).append("\t");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

}
