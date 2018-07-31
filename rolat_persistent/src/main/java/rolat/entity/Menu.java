package rolat.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name="menu")
@Data
@EqualsAndHashCode(callSuper=true)
@AllArgsConstructor
@NoArgsConstructor
public class Menu  extends BaseEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String name;//菜单名称
	private String url;//菜单路径
	private String icon;//菜单图标
	@Column(name="sequence",nullable=false)
	private Integer sequence;//序号
	@Column(columnDefinition="int default 1",nullable=false)
	private Integer status=1;//页面状态 1：启用，2停用；0：删除
	private Long pid;//父节点Id
	
	@Transient
	private List<Menu> subMenuList;

	public Menu(Long id){
		this.id=id;
	}
	
}
