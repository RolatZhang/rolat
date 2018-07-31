package rolat.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@MappedSuperclass
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseEntity implements Serializable  {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)//根据数据库自动选择
	protected Long id;
	
	protected String remark;
	
	//设置默认值 
	//TemporalType.DATE表示日期 2010-02-02 
	//TemporalType.TIME表示时间 09:30 
	//TemporalType.TIMESTAMP表示时间戳 2010-02-02  09:30 
	@Temporal(TemporalType.TIMESTAMP) 
	protected Date createDate;
    
}
