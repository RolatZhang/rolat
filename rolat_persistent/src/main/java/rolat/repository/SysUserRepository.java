package rolat.repository;

import rolat.entity.SysUser;
import rolat.repository.specification.IMyRepository;

public interface SysUserRepository extends IMyRepository<SysUser, Long> {

	SysUser findByUserName(String username);

}
