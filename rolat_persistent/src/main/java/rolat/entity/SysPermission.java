package rolat.entity;

import javax.persistence.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@EqualsAndHashCode(callSuper=true)
@AllArgsConstructor
@NoArgsConstructor
public class SysPermission extends BaseEntity {
	
	private static final long serialVersionUID = 1L;
	// 权限名称
	private String name;
	private String icon;
	// 授权链接
	private String url;
	// 父节点id
	private Long pid;
	

}
