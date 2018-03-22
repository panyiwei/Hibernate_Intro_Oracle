package com.atguigu.hibernate.dao;

import org.hibernate.Session;

import com.amaker.util.HibernateUtil2;
import com.atguigu.hibernate.entities.Department;

public class DepartmentDao {
	/**
	 * 若需要传入一个Session对象，则意味着上一层(Service)需要获取到Session对象。
	 * 这说明上一层需要和Hibernate的API紧密耦合.所以不推荐使用此种方法。
	 * @param session
	 * @param dept
	 */
	public void save(Session session, Department dept){
		session.save(dept);
	}
	
	public void save(Department dept){
		//内部获取Session对象
		//获取和当前线程绑定的Session对象。
		//1.不需要从外部传入Session对象。
		//2.多个DAO方法也可以使用一个事务！
		Session session=HibernateUtil2.getInstance().getSession();
		System.out.println(session.hashCode());
		
		//session.save(dept);
		System.out.println("saveEnd");
	}
}
