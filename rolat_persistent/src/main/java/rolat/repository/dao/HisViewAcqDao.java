package rolat.repository.dao;

import com.sgcc.sgd5000.hisdata.HisViewAcq;
import com.sgcc.sgd5000.meas.Meters;
import net.njcp.service.util.CommFunc;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class HisViewAcqDao extends BaseDao {

    public List<HisViewAcq> getHisViewAcqList(Timestamp startTime, Timestamp endTime, Meters meters,String tableName) {
        String sql="select * from "+tableName+" where METER_ID=? and OCCUR_TIME>=? and OCCUR_TIME<=?";
        return (List<HisViewAcq>) dao.executeDynamicSQLQuery(sql,HisViewAcq.class,new Object[]{meters.getMeterId(),startTime,endTime});
    }

    public int updateHisViewAcqList(List<HisViewAcq> hisViewAcqList,String tableName){
        dao.getHibernateTemplate().execute(new UpdateHisViewAcqAction(hisViewAcqList));
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

        public UpdateHisViewAcqAction(List<HisViewAcq> hisViewAcqList ) {
            this.hisViewAcqList = hisViewAcqList;
        }

        @Override
        public Object doInHibernate(Session session) throws HibernateException, SQLException {

          /*  Connection conn = session.connection();
            PreparedStatement pstmt = null;
            Timestamp timeForTableName = null;
            int batchSize = 1000;
            try {
                if ( conn != null ) {
                    // pstmt = conn.createStatement();
                    for ( int i = 0; i < this.hisViewAcqList.size(); i++ ) {
                        HisViewAcq view = this.hisViewAcqList.get(i);
                        if ( view == null ) {
                            continue;
                        }
                        timeForTableName = view.getId().getOccurTime();
                        if ( timeForTableName != null )
                            break;
                    }
                    if ( timeForTableName != null ) {
                        String tableName = CommFunc.getHisTableName(this.meter.getAcquiredId(), timeForTableName,
                                HisViewAcq.class);
                        String updateSql = "update " + tableName
                                + " set pap_value=?,pap_status=?,rap_value=?,rap_status=?,"
                                + "prp_value=?,prp_status=?,rrp_value=?,rrp_status=?" + " where meter_id="
                                + this.meter.getId() + " and occur_time=?";
                        pstmt = conn.prepareStatement(updateSql);
                        for ( int i = 0; i < this.viewList.size(); i++ ) {
                            HisViewAcq view = this.viewList.get(i);
                            if ( view == null ) {
                                continue;
                            }
                            Timestamp occurTime = view.getId().getOccurTime();
                            boolean flag=false;
                            if(dataLockConfigList!=null){//跳过在配置时间内
                                for (DataLockConfig dataLockConfig : dataLockConfigList) {
                                    Long startLong=dataLockConfig.getId().getStartDate().getTime();
                                    Long endLong=dataLockConfig.getEndDate().getTime();
                                    if(occurTime.getTime()>=startLong&&occurTime.getTime()<=endLong){
                                        flag=true;
                                        break;
                                    }
                                }
                            }
                            if(flag)
                                continue;
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
                            if ( (i != 0 && i % batchSize == batchSize - 1) || i == this.viewList.size() - 1 ) {
                                try {
                                    pstmt.executeBatch();
                                    pstmt.clearBatch();
                                } catch ( Exception ex ) {
                                    Alarm.error("error sql is: " + updateSql);
                                    Alarm.error(ex, ex);
                                }
                            }
                        }
                    }
                }
            } catch ( Exception e ) {
                Alarm.error(e, e);
            } finally {
                try {
                    if ( pstmt != null )
                        pstmt.close();
                    if ( conn != null )
                        conn.close();
                } catch ( Exception ex ) {
                    Alarm.error(ex, ex);
                }
            }*/
            return null;
        }
    }
}


