package rolat.repository.serviceImpl;

import com.sgcc.sgd5000.constants.EPulseType;
import com.sgcc.sgd5000.hisdata.HisProfileAcq;
import com.sgcc.sgd5000.hisdata.HisProfileAcqId;
import com.sgcc.sgd5000.hisdata.HisViewAcq;
import com.sgcc.sgd5000.meas.MeterConfigItem;
import com.sgcc.sgd5000.meas.MeterTimeTag;
import com.sgcc.sgd5000.meas.Meters;
import net.njcp.service.util.CommFunc;
import net.njcp.service.util.Constants;
import org.springframework.stereotype.Service;
import rolat.repository.service.ITransService;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("transService")
public class TransServiceImpl implements ITransService {
    @Override
    public List<HisViewAcq> processHisViewAcq(List<HisViewAcq> hisViewAcqList,Double precision) {
        int totalSize=hisViewAcqList.size();
        if ( totalSize > 0 ) {
            HisViewAcq lastView=null;
            for ( int i=0;i<totalSize;i++) {
                HisViewAcq view=hisViewAcqList.get(i);
                Long papStatus=view.getPapStatus();
                Long rapStatus=view.getRapStatus();
                Long prpStatus=view.getPrpStatus();
                Long rrpStatus=view.getRrpStatus();
                if ( papStatus != null&& CommFunc.isStatusBitSetTrue(papStatus, Constants.METER_STATUS_FROZEN)) {
                    // 冻结数据，不参与计算
                }else{
                    Double papRawValue=view.getPapRawValue();
                    view.setPapStatus(view.getPapRawStatus());
                    if ( papRawValue != null ) {
                        Double papValue = precision * papRawValue;
                        view.setPapValue(papValue);
                    }else{//数据漏点
                        Long newStatus = CommFunc.getDataStatusBy(Constants.METER_STATUS_OMISSION, 1, view.getPapRawStatus());
                        view.setPapStatus(newStatus);
                    }
                }
                if ( rapStatus != null&&CommFunc.isStatusBitSetTrue(rapStatus, Constants.METER_STATUS_FROZEN)) {
                }else{
                    Double rapRawValue=view.getRapRawValue();
                    view.setRapStatus(view.getRapRawStatus());
                    if ( rapRawValue != null ) {
                        Double rapValue = precision * rapRawValue;
                        view.setRapValue(rapValue);
                    }else{
                        Long newStatus = CommFunc.getDataStatusBy(Constants.METER_STATUS_OMISSION, 1, view.getRapRawStatus());
                        view.setRapStatus(newStatus);
                    }
                }
                if ( prpStatus != null&&CommFunc.isStatusBitSetTrue(prpStatus, Constants.METER_STATUS_FROZEN)) {
                }else{
                    Double prpRawValue=view.getPrpRawValue();
                    view.setPrpStatus(view.getPrpRawStatus());
                    if ( prpRawValue != null ) {
                        Double prpValue = precision * prpRawValue;
                        view.setPrpValue(prpValue);
                    }else{
                        Long newStatus = CommFunc.getDataStatusBy(Constants.METER_STATUS_OMISSION, 1, view.getPrpRawStatus());
                        view.setPrpStatus(newStatus);
                    }

                }
                if ( rrpStatus != null&&CommFunc.isStatusBitSetTrue(rrpStatus, Constants.METER_STATUS_FROZEN)) {
                }else{
                    Double rrpRawValue=view.getRrpRawValue();
                    view.setRrpStatus(view.getRrpRawStatus());
                    if ( rrpRawValue != null ) {
                        Double rrpValue = precision * rrpRawValue;
                        view.setRrpValue(rrpValue);
                    }else{
                        Long newStatus = CommFunc.getDataStatusBy(Constants.METER_STATUS_OMISSION, 1, view.getRrpRawStatus());
                        view.setRrpStatus(newStatus);
                    }
                }
            }



        }
        return hisViewAcqList;
    }

    @Override
    public int processHisProfileAcq(List<HisViewAcq> hisViewAcqList,Meters meters, MeterConfigItem meterConfigItem, boolean updateTime, MeterTimeTag meterTimeTag) {
        int totalSize=hisViewAcqList.size();
        Map<String,HisProfileAcq> hisProfileAcqMap=new HashMap<String,HisProfileAcq>();//
        if(totalSize>1){
            EPulseType[] pulseTypeArray = EPulseType.values();
            for ( int j = 0; j < pulseTypeArray.length; j++ ) {
                HisViewAcq lastView=hisViewAcqList.get(0);
                EPulseType pulseType = pulseTypeArray[j];
                for(int i=1;i<totalSize;i++){
                    HisViewAcq hisViewAcq=hisViewAcqList.get(i);
                    Double lastRawValue = (Double) CommFunc.getValueOrStatusByPulseType(lastView, pulseType, true, CommFunc.VALUE);
                    Double rawValue = (Double) CommFunc.getValueOrStatusByPulseType(hisViewAcq, pulseType, true, CommFunc.VALUE);
                    if(lastRawValue!=null){
                        if(rawValue!=null){
                            if(lastRawValue>rawValue){

                            }
                        }
                    }

                    processHisProfileAcqPap(lastView,hisViewAcq,hisProfileAcqMap,meters.getMeterId(),meterConfigItem.getIntegerationPeriod());
                    lastView=hisViewAcq;
                }

            }
        }
        return 0;
    }

    private boolean judgeViewException(Double lastRawValue,Double rawValue) {
        boolean judge=true;
        if(lastRawValue!=null){
            if(rawValue==null){
                judge=false;
            }else if(lastRawValue>rawValue){

            }
        }
        return judge;
    }


    /**
     * pap rap 分开判断异常
     * @param lastView
     * @param view
     * @param hisProfileAcqMap
     * @param meterId
     * @param period_profile
     */
    private void processHisProfileAcqPap(HisViewAcq lastView, HisViewAcq view, Map<String,HisProfileAcq> hisProfileAcqMap, long meterId,Integer period_profile) {
        Double lastpapValue=lastView.getPapValue();
        Double papValue=view.getPapValue();
        Long lastoccurTime=lastView.getId().getOccurTime().getTime();
        Long betweenTime=view.getId().getOccurTime().getTime()-lastoccurTime;
        long a=betweenTime/(period_profile*1000);
        for(int i=0;i<a;i++){
            HisProfileAcqId id = new HisProfileAcqId();
            id.setMeterId(meterId);
            id.setOccurTime(new Timestamp(lastoccurTime+(i+1)*period_profile*1000));
            String key=id.getMeterId()+""+id.getOccurTime();

            HisProfileAcq acq= hisProfileAcqMap.get(key);
            if(acq==null)   acq = new HisProfileAcq();
            acq.setId(id);
            if(papValue!=null&&lastpapValue!=null){
                Double value=(papValue-lastpapValue)/a;
                Long laststatus = lastView.getPapStatus();
                Long status = view.getPapStatus();
                laststatus=laststatus==null?0l:laststatus;
                status=status==null?0l:status;
                status=status|laststatus;
                if ( a > 1 ) {// 如果时间间隔大于一个周期，再或上人工置数标志
                    status =CommFunc.getDataStatusBy(Constants.METER_STATUS_MANUAL_DATASET, 1, status);
                }
                acq.setPapRawValue(value);
                acq.setPapRawStatus(status);
            }
            hisProfileAcqMap.put(key,acq);
        }
    }




    private boolean judgeViewException(HisViewAcq lastview, HisViewAcq view,HisViewAcq nextview) {//lastview是库里多存入的一个点
        //Double adjustNum=0.000001;精度调节系数
        boolean judge=true;
        if(lastview!=null){
            Double lastpapVaule=lastview.getPapRawValue();
            Double lastrapVaule=lastview.getRapRawValue();
            Double lastprpVaule=lastview.getPrpRawValue();
            Double lastrrpVaule=lastview.getRrpRawValue();
            if(lastpapVaule==null||lastrapVaule==null||lastprpVaule==null||lastrrpVaule==null){//上一个点如果异常 取下一个点
                judge=true;
            }else{
                Double papVaule=view.getPapRawValue();
                Double rapVaule=view.getRapRawValue();
                Double prpVaule=view.getPrpRawValue();
                Double rrpVaule=view.getRrpRawValue();
                if(papVaule==null||rapVaule==null||prpVaule==null||rrpVaule==null){
                    judge=false;
                }else{
                    //判断倒走
                    if(judge&&papVaule<lastpapVaule){//倒走
                        judge=false;
                        if(nextview!=null&&nextview.getPapValue()!=null&&nextview.getPapValue()>papVaule&&lastpapVaule<nextview.getPapValue()){//疑似换表或满码
                            judge=true;
                        }else{
                            Long newStatus = CommFunc.getDataStatusBy(Constants.METER_STATUS_REVERSE, 1, view.getPapStatus());
                            view.setPapStatus(newStatus);
                        }
                    }
                    if(judge&&rapVaule<lastrapVaule){//倒走
                        judge=false;
                        if(nextview!=null&&nextview.getRapValue()!=null&&nextview.getRapValue()>rapVaule&&lastrapVaule<nextview.getRapValue()){//疑似换表或满码
                            judge=true;
                        }else{
                            Long newStatus = CommFunc.getDataStatusBy(Constants.METER_STATUS_REVERSE, 1, view.getRapStatus());
                            view.setRapStatus(newStatus);
                        }
                    }
                    if(judge&&prpVaule<lastprpVaule){//倒走
                        judge=false;
                        if(nextview!=null&&nextview.getPrpValue()!=null&&nextview.getPrpValue()>prpVaule&&lastprpVaule<nextview.getPrpValue()){//疑似换表或满码
                            judge=true;
                        }else{
                            Long newStatus = CommFunc.getDataStatusBy(Constants.METER_STATUS_REVERSE, 1, view.getPrpStatus());
                            view.setPrpStatus(newStatus);
                        }
                    }
                    if(judge&&rrpVaule<lastrrpVaule){//倒走
                        judge=false;
                        if(nextview!=null&&nextview.getRrpValue()!=null&&nextview.getRrpValue()>rrpVaule&&lastrrpVaule<nextview.getRrpValue()){//疑似换表或满码
                            judge=true;
                        }else{
                            Long newStatus = CommFunc.getDataStatusBy(Constants.METER_STATUS_REVERSE, 1, view.getRrpStatus());
                            view.setRrpStatus(newStatus);
                        }
                    }
                    //判断冒大数
                    if(lastpapVaule>0&&papVaule>1000&&papVaule/lastpapVaule>10){//冒大数
                        judge=false;
                        Long newStatus = CommFunc.getDataStatusBy(Constants.METER_STATUS_TREMENDOUS, 1, view.getPapStatus());
                        view.setPapStatus(newStatus);
                    }
                    if(lastrapVaule>0&&rapVaule>1000&&rapVaule/lastrapVaule>10){//冒大数
                        judge=false;
                        Long newStatus = CommFunc.getDataStatusBy(Constants.METER_STATUS_TREMENDOUS, 1, view.getRapStatus());
                        view.setRapStatus(newStatus);
                    }
                    if(lastprpVaule>0&&prpVaule>1000&&prpVaule/lastprpVaule>10){//冒大数
                        judge=false;
                        Long newStatus = CommFunc.getDataStatusBy(Constants.METER_STATUS_TREMENDOUS, 1, view.getPrpStatus());
                        view.setPrpStatus(newStatus);
                    }
                    if(lastrrpVaule>0&&rrpVaule>1000&&rrpVaule/lastrrpVaule>10){//冒大数
                        judge=false;
                        Long newStatus = CommFunc.getDataStatusBy(Constants.METER_STATUS_TREMENDOUS, 1, view.getRrpStatus());
                        view.setRrpStatus(newStatus);
                    }
                }
            }
        }
        return judge;
    }

}
