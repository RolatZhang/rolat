package rolat.repository.specification;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

public class MyRepository<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements IMyRepository<T, ID> {

	private EntityManager entityManager;

	public MyRepository(Class<T> domainClass, EntityManager entityManager) {
		super(domainClass, entityManager);
		this.entityManager=entityManager;
	}

	

	@Override
	public Page<T> findByAuto(T example, Pageable pageable) {
		return findAll(MySpecs.byAuto(entityManager, example), pageable);
	}



	@Override
	public Page<T> findByAutoLike(T example, Pageable pageable) {
		return findAll(MySpecs.byAutoLike(entityManager, example), pageable);
	}



	@Override
	public Page<T> findByAutoOr(T example, Pageable pageable) {
		return findAll(MySpecs.byAutoOr(entityManager, example), pageable);
	}
	
}
