package com.atguigu.hibernate.test;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.amaker.util.HibernateUtil;
import com.atguigu.hibernate.entities.Department;
import com.atguigu.hibernate.entities.Employee;


public class SecondLevelCacheTest {
	Session session=null;
	Transaction transaction=null;
	@Before
	public void setUp() throws Exception {
		session=new HibernateUtil().getSession5();
		transaction=session.beginTransaction();
	}

	@After
	public void tearDown() throws Exception {
		transaction.commit();//提交
		session.close();
	}
	
	
	/**
	 * 类二级缓存(EhCache插件)
	 */
	@Test
	public void testHibernateSecondLevelCache(){
		Employee employee=session.get(Employee.class, 1092);
		System.out.println(employee.getName());
		
		transaction.commit();//提交
		session.close();
		
		session=new HibernateUtil().getSession5();
		transaction=session.beginTransaction();
		
		Employee employee2=session.get(Employee.class, 1092);
		System.out.println(employee2.getName());
	}
	
	/**
	 * 集合二级缓存
	 */
	@Test
	public void testCollectionSecondLevelCache(){
		Department dept=(Department)session.get(Department.class, 17);
		System.out.println(dept.getName());
		System.out.println(dept.getEmps().size());
		
		transaction.commit();//提交
		session.close();
		
		session=new HibernateUtil().getSession5();
		transaction=session.beginTransaction();
		
		Department dept2=(Department)session.get(Department.class, 17);
		System.out.println(dept2.getName());
		System.out.println(dept2.getEmps().size());
	}
	
	
	/**
	 * 查询缓存(默认情况下，设置的缓存对HQL及QBC查询是无效的)
	 */
	@Test
	public void testQueryCache(){
		Query<Employee> query=session.createQuery("FROM Employee", Employee.class);
		query.setCacheable(true);//设置query可以被缓存，并且要在hibernate.cfg.xml中启用查询缓存cache.use_query_cache
		
		List<Employee> emps=query.list();
		System.out.println(emps.size());
		
		emps=query.list();
		System.out.println(emps.size());
	}
	
	/**
	 * 更新时间戳缓存
	 */
	@Test
	public void testUpdateTimeStampCache(){
		Query<Employee> query=session.createQuery("FROM Employee", Employee.class);
		query.setCacheable(true);//设置query可以被缓存，并且要在hibernate.cfg.xml中启用查询缓存cache.use_query_cache
		
		List<Employee> emps=query.list();
		System.out.println(emps.size());
		
		Employee employee=session.get(Employee.class, 348);
		employee.setSalary(30000);
		
		emps=query.list();
		System.out.println(emps.size());
	}
	
	@Test
	public void testQueryIterate(){
		Department dept=(Department)session.get(Department.class,17);
		System.out.println(dept.getName());
		System.out.println(dept.getEmps().size());
		
		Query<Employee> query=session.createQuery("FROM Employee e WHERE e.dept.id=17", Employee.class);
//		List<Employee> emps=query.list();
//		System.out.println(emps.size());
		
		Iterator<Employee> empIt=query.iterate();//迭代器(select只查id,然后去缓存中找需要的其他字段)
		while(empIt.hasNext()){
			System.out.println(empIt.next().getName());
		}
	}
}
