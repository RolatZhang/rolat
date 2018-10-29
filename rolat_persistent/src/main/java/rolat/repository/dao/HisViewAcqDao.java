package rolat.repository.dao;

import com.sgcc.sgd5000.hisdata.HisViewAcq;
import com.sgcc.sgd5000.meas.Meters;
import net.njcp.service.util.CommFunc;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.List;

@Repository
public class HisViewAcqDao extends BaseDao {

    private Log log= LogFactory.getLog(HisViewAcqDao.class);

    public List<HisViewAcq> getHisViewAcqList(Timestamp startTime, Timestamp endTime, Meters meters,String tableName) {
        String sql="select * from "+tableName+" where METER_ID=? and OCCUR_TIME>=? and OCCUR_TIME<=? order by OCCUR_TIME";
        return (List<HisViewAcq>) dao.executeDynamicSQLQuery(sql,HisViewAcq.class,new Object[]{meters.getMeterId(),startTime,endTime});
    }

    public int updateHisViewAcqList(List<HisViewAcq> hisViewAcqList,String tableName,Long meterId){
        dao.getHibernateTemplate().execute(new UpdateHisViewAcqAction(hisViewAcqList,tableName, meterId));
        return 0;
    }

    /**
     * 修改Hisview将原始值转换一次值，还包括状态（漏点，冒大数等问题）
     * 加个注释
     * @author Rolat
     *
     */
    private class UpdateHisViewAcqAction implements HibernateCallback {
        private List<HisViewAcq> hisViewAcqList;
        private String tableName;
        private Long meterId;

        public UpdateHisViewAcqAction(List<HisViewAcq> hisViewAcqList,String tableName,Long meterId) {
            this.hisViewAcqList = hisViewAcqList;
            this.tableName=tableName;
            this.meterId=meterId;
        }

        @Override
        public Object doInHibernate(Session session) throws HibernateException, SQLException {

            Connection conn = session.connection();
            PreparedStatement pstmt = null;
            int batchSize = 1000;
            try {
                if ( conn != null ) {
                        String updateSql = "update " + tableName
                                + " set pap_value=?,pap_status=?,rap_value=?,rap_status=?,"
                                + "prp_value=?,prp_status=?,rrp_value=?,rrp_status=?" + " where meter_id="
                                + this.meterId + " and occur_time=?";
                    log.info(meterId+"执行sql||"+updateSql);
                        pstmt = conn.prepareStatement(updateSql);
                        for ( int i = 0; i < this.hisViewAcqList.size(); i++ ) {
                            HisViewAcq view = this.hisViewAcqList.get(i);
                            Timestamp occurTime = view.getId().getOccurTime();
                            boolean flag=false;
                            if ( view.getPapValue() != null )
                                pstmt.setDouble(1, view.getPapValue());
                            else
                                pstmt.setNull(1, Types.DOUBLE);
                            if ( view.getPapStatus() != null )
                                pstmt.setLong(2, view.getPapStatus());
                            else
                                pstmt.setNull(2, Types.BIGINT);
                            if ( view.getRapValue() != null )
                                pstmt.setDouble(3, view.getRapValue());
                            else
                                pstmt.setNull(3, Types.DOUBLE);
                            if ( view.getRapStatus() != null )
                                pstmt.setLong(4, view.getRapStatus());
                            else
                                pstmt.setNull(4, Types.BIGINT);
                            if ( view.getPrpValue() != null )
                                pstmt.setDouble(5, view.getPrpValue());
                            else
                                pstmt.setNull(5, Types.DOUBLE);
                            if ( view.getPrpStatus() != null )
                                pstmt.setLong(6, view.getPrpStatus());
                            else
                                pstmt.setNull(6, Types.BIGINT);
                            if ( view.getRrpValue() != null )
                                pstmt.setDouble(7, view.getRrpValue());
                            else
                                pstmt.setNull(7, Types.DOUBLE);
                            if ( view.getRrpStatus() != null )
                                pstmt.setLong(8, view.getRrpStatus());
                            else
                                pstmt.setNull(8, Types.BIGINT);
                            if ( occurTime != null )
                                pstmt.setTimestamp(9, occurTime);
                            else
                                pstmt.setNull(9, Types.TIMESTAMP);
                            pstmt.addBatch();
                            if ( (i != 0 && i % batchSize == 0) || i == this.hisViewAcqList.size() - 1 ) {
                                try {
                                    pstmt.executeBatch();
                                    pstmt.clearBatch();
                                } catch ( Exception ex ) {
                                    log.error(ex, ex);
                                }
                            }
                        }
                        return 1;
                }
            } catch ( Exception e ) {
                log.error(e, e);
            } finally {
                try {
                    if ( pstmt != null )
                        pstmt.close();
                    if ( conn != null )
                        conn.close();
                } catch ( Exception ex ) {
                    log.error(ex, ex);
                }
            }
            return 0;
        }
    }
}


