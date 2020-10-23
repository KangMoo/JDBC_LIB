package org.hkm.jdbc.module;

import org.apache.commons.dbutils.QueryRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 *
 * @author kangmoo Heo
 */
public class DbUpdater {
    Logger logger = LoggerFactory.getLogger(DbUpdater.class);
    private QueryRunner runner;

    public DbUpdater(QueryRunner runner) {
        this.runner = runner;
    }

    /**
     * Insert/Update/Delete Query 실행 후 결과(affected row count) 반환
     * @param sql query string
     * @param params input params or null
     * @return result
     */
    public int updateQueryResult(String sql, Object ... params){
        try {
            return runner.update(sql, params);
        } catch (Exception e) {
            logger.error("updateQueryResult() EXCEPTION", e);
            return -1;
        }
    }
}
