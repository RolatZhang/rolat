package rolat.repository.specification;



import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.SingularAttribute;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import com.google.common.collect.Iterables;

/**
 * 自定义查询
 * @author Rolat
 *
 */
public class MySpecs {
	
	private static String pattern(String str){
		return "%"+str+"%";
	}
	
	/**
	 * 根据实体，查询结果
	 * @param entityManager++
	 * @param example
	 * @return
	 */
	public static <T> Specification<T> byAutoOr(final EntityManager entityManager,final T example){
		@SuppressWarnings("unchecked")
		final Class<T> type=(Class<T>) example.getClass();
		return new Specification<T>(){

			@Override
			public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicateList=new ArrayList<Predicate>();
				EntityType<T> entity=entityManager.getMetamodel().entity(type);
				for(Attribute<T, ?> attr: entity.getDeclaredAttributes()){
					Object attrValue=getValue(example,attr);
					if(attrValue!=null){
						if(attr.getJavaType()==String.class){
							if(!StringUtils.isEmpty(attrValue)){
								predicateList.add(cb.like(root.get(attribute(entity,attr.getName(),String.class)), pattern((String)attrValue)));
							}
						}else{
							if(attr.getName().equals("status"))
								cb.and(cb.equal(root.get(attribute(entity,attr.getName(),attr.getJavaType())), attrValue));
							else
								predicateList.add(cb.equal(root.get(attribute(entity,attr.getName(),attr.getJavaType())),attrValue));
						}
							
					}
				}
				return predicateList.isEmpty()?cb.conjunction():cb.or(Iterables.toArray(predicateList, Predicate.class));
			}
			
		};
	}
	/**
	 * 根据实体，查询结果
	 * @param entityManager
	 * @param example
	 * @return
	 */
	public static <T> Specification<T> byAuto(final EntityManager entityManager,final T example){
		@SuppressWarnings("unchecked")
		final Class<T> type=(Class<T>) example.getClass();
		return new Specification<T>(){

			@Override
			public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicateList=new ArrayList<>();
				EntityType<T> entity=entityManager.getMetamodel().entity(type);
				for(Attribute<T, ?> attr: entity.getDeclaredAttributes()){
					Object attrValue=getValue(example,attr);
					if(attrValue!=null){
						if(attr.getJavaType()==String.class){
							if(!StringUtils.isEmpty(attrValue)){
								predicateList.add(cb.like(root.get(attribute(entity,attr.getName(),String.class)), pattern((String)attrValue)));
							}
						}else{
							predicateList.add(cb.equal(root.get(attribute(entity,attr.getName(),attr.getJavaType())),attrValue));
						}
							
					}
				}
				return predicateList.isEmpty()?cb.conjunction():cb.and(Iterables.toArray(predicateList, Predicate.class));
			}
			
		};
	}
	/**
	 * 根据实体模糊查询结果
	 * @param entityManager
	 * @param example
	 * @return
	 */
	public static <T> Specification<T> byAutoLike(final EntityManager entityManager,final T example){
		@SuppressWarnings("unchecked")
		final Class<T> type=(Class<T>) example.getClass();
		return new Specification<T>(){

			@Override
			public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicateList=new ArrayList<>();
				EntityType<T> entity=entityManager.getMetamodel().entity(type);
				for(Attribute<T, ?> attr: entity.getDeclaredAttributes()){
					Object attrValue=getValue(example,attr);
					if(attrValue!=null){
						if(attr.getJavaType()==String.class){
							if(!StringUtils.isEmpty(attrValue)){
								predicateList.add(cb.like(root.get(attribute(entity,attr.getName(),String.class)), pattern((String)attrValue)));
							}
						}else{
							predicateList.add(cb.equal(root.get(attribute(entity,attr.getName(),attr.getJavaType())), attrValue));
						}
							
					}
				}
				return predicateList.isEmpty()?cb.conjunction():cb.and(Iterables.toArray(predicateList, Predicate.class));
			}
			
		};
	}
	
	private static <T> Object getValue(T example,Attribute<T, ?> attr){
		return ReflectionUtils.getField((Field) attr.getJavaMember(), example);
	}
	
	private static <E,T> SingularAttribute<T, E> attribute(EntityType<T> entity,String fieldName,Class<E> fieldClass){
		return entity.getDeclaredSingularAttribute(fieldName, fieldClass);
	}
}
