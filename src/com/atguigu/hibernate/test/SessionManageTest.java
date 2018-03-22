package com.atguigu.hibernate.test;

import static org.junit.Assert.*;

import java.sql.SQLException;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.amaker.util.HibernateUtil2;
import com.atguigu.hibernate.dao.DepartmentDao;
import com.atguigu.hibernate.entities.Department;
import com.sun.corba.se.pept.transport.Connection;


public class SessionManageTest {
	
	@Before
	public void setUp() throws Exception {
		
	}

	@After
	public void tearDown() throws Exception {
		
	}
	
	/**
	 * 管理Session的方式（thread：与本地线程绑定）
	 */
	@Test
	public void testManageSession(){
		//获取Session
		Session session=HibernateUtil2.getInstance().getSession();
		//开启事务
		Transaction transaction=session.beginTransaction();
		System.out.println("-->"+session.hashCode());
		
		DepartmentDao departmentDao=new DepartmentDao();
		Department dept=new Department();
		dept.setName("尼姑管理部");
		departmentDao.save(dept);
		//departmentDao.save(dept);
		//departmentDao.save(dept);
		
		//若Session是由thread来管理的，则在提交或回滚事务时，已经关闭Session了。
		transaction.commit();
		System.out.println(session.isOpen());
	}
	
	/**
	 * 批量操作(推荐用 JDBC方式，效率最高,速度最快)
	 */
	@Test
	public void testBatch(){
		/*session.doWork(new Work(){
			@Override
			public void execute(Connection connection)throws SQLException{
				//通过JDBC原生的API进行操作，效率最高，速度最快！
			}
		});*/
	}
}
