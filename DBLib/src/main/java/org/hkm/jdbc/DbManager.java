package org.hkm.jdbc;

import java.sql.*;
import java.util.List;
import java.util.Map;
import org.hkm.jdbc.module.*;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 *
 * @author kangmoo Heo
 */
public class DbManager {
    static Logger logger = LoggerFactory.getLogger(DbManager.class);

    private static final String HIKARI_CONF_FILE = "/hikaricp.properties";
    private static final DbManager instance = new DbManager();
    public static DbManager getInstance() {
        return instance;
    }
    private HikariDataSource ds;
    private DbSelector selector;
    private DbUpdater updater;

    private DbManager() {
        // nothing to do
    }

    public Connection getConnection() throws SQLException {
        return this.ds.getConnection();
    }

    /**
     * DB Connection
     * @param serverIp db server address
     * @param port db server port
     * @param database db name
     * @param userName username
     * @param password password
     * @return result
     */
    public boolean start(String serverIp, String port, String database, String userName, String password){
        if(ds != null){
            logger.warn("DB Connection Pool has already been created.");
            return false;
        }
        try {
            HikariConfig config = new HikariConfig(HIKARI_CONF_FILE);
            config.setJdbcUrl("jdbc:mysql://" + serverIp + ":" + port + "/" + database + "?serverTimezone=UTC");
            config.setUsername(userName);
            config.setPassword(password);
            ds = new HikariDataSource(config);
            logger.debug("DB Connection Pool has created.");
            QueryRunner runner = new QueryRunner(ds);
            this.selector = new DbSelector(runner);
            this.updater = new DbUpdater(runner);
        } catch (Exception e){
            logger.error("DB Connection Pool Creation has failed.",e);
            return false;
        }
        return true;
    }

    /**
     * DB Disconnection
     */
    public void shutdown(){
        try {
            if(ds != null) {
                ds.close();
                logger.debug("DB Connection Pool is shutting down.");
            }
            ds = null;
        } catch(Exception e) {
            logger.warn("Error when shutting down DB Connection Pool", e);
        }
    }

    /**
     * Query 실행 후 결과 String 으로 반환
     * @param sql query string
     * @return string
     */
    public String doQueryResultAsString(String sql){
        try(Connection con = ds.getConnection()){
            return selector.doQueryResultAsString(sql, con);
        }catch (Exception e){
            logger.error("JDBC Connection Error",e);
            return null;
        }
    }

    /**
     * SQL Query 실행 후 결과를 <T>로 변환하여 반환
     * @param sql query string
     * @param clazz class
     * @param params input params or null
     * @return obj
     */
    public <T> T doQueryResultAsObj(String sql, Class<T> clazz, Object ... params) {
        return selector.doQueryResultAsObj(sql, clazz, params);
    }

    /**
     * SQL Query 실행 후 결과를 List<T>로 변환하여 반환
     * @param sql query string
     * @param clazz class
     * @param params input params or null
     * @return obj list
     */
    public <T> List<T> doQueryResultAsListObj(String sql, Class<T> clazz, Object ... params) {
        return selector.doQueryResultAsListObj(sql, clazz, params);
    }

    /**
     * SQL Query 실행 후 결과를 List<Map<String,Object>>으로 변환하여 반환
     * @param sql query string
     * @param params input params or null
     * @return list
     */
    public List<Map<String, Object>> doQueryResultAsListMap(String sql, Object ... params){
        return selector.doQueryResultAsListMap(sql,params);
    }

    public List<Object[]> doQueryResultAsListArray(String sql, Object ... params) {
        return selector.doQueryResultAsListArray(sql, params);
    }

    /**
     * Insert/Update/Delete Query 실행 후 결과(affected row count) 반환
     * @param sql query string
     * @param params input params or null
     * @return result
     */
    public int updateQueryResult(String sql, Object ... params){
        return updater.updateQueryResult(sql, params);
    }
}