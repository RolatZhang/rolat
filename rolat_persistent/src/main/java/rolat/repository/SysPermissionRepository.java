package rolat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import rolat.entity.SysPermission;
import rolat.repository.specification.IMyRepository;

public interface SysPermissionRepository extends IMyRepository<SysPermission, Long>  {

	@Query(value="select e.* from sys_user a\n" +
			" inner join sys_user_role_list b on a.id=b.sys_user_id\n" +
			" inner join sys_role c on b.role_list_id=c.id\n" +
			" inner join sys_role_sys_permission_list d on c.id=d.sys_role_id\n" +
			" inner join sys_permission e on d.sys_permission_list_id=e.id" +
			" where a.id= :id", nativeQuery= true)
	List<SysPermission> findByAdminUserId(@Param("id") Long id);
}
