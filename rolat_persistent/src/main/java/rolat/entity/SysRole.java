package rolat.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
/*@Data
@EqualsAndHashCode(callSuper=true)
@AllArgsConstructor
@NoArgsConstructor*/
public class SysRole extends BaseEntity{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String name;
	
	@ManyToMany(cascade={CascadeType.REFRESH},fetch=FetchType.LAZY)
	private List<SysPermission> sysPermissionList;
	
	

	
}
