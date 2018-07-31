package rolat.repository.specification;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

public class MyRepositoryFactoryBean<T extends JpaRepository<S, ID>,S,ID extends Serializable> extends JpaRepositoryFactoryBean<T,S,ID>{

	public MyRepositoryFactoryBean(Class<? extends T> repositoryInterface) {
		super(repositoryInterface);
	}
	
	@Override
	protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
		
		return new MyRepositoryFactory(entityManager);
	}
	
	
	private static class MyRepositoryFactory extends JpaRepositoryFactory {

		public MyRepositoryFactory(EntityManager entityManager) {
			super(entityManager);
		}
		@SuppressWarnings("unchecked")
		@Override
		protected <T, ID extends Serializable> SimpleJpaRepository<?, ?> getTargetRepository(
				RepositoryInformation information, EntityManager entityManager) {

			return new MyRepository<T,ID>((Class<T>) information.getDomainType(), entityManager);
		}
		@Override
		protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
				return MyRepository.class;
		}
	}
}
