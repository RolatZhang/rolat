package rolat.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import rolat.entity.Menu;
import rolat.repository.specification.IMyRepository;

public interface MenuRepository extends IMyRepository<Menu, Long>  {

	List<Menu> findAllByPid(Long pid, Sort sort);

	List<Menu> findAllByStatus(Integer status);
	
	Page<Menu> findAll(Pageable pageable);

}
