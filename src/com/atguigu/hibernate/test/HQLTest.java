package com.atguigu.hibernate.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
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
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;


public class HQLTest {
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
	
	@Test
	public void testHQL(){
		//1.创建Query对象
		//基于位置的参数
		String hql="FROM Employee e WHERE e.salary > ? AND e.email LIKE ? AND e.dept=?";
		Query<Employee> query=session.createQuery(hql, Employee.class);
		
		//2.绑定参数(使用占位符参数)
		//Query对象调用setXxx()方法支持方法链的编程风格
		Department dept=new Department();
		dept.setId(17);
		query.setParameter(0, (float)4000)
				.setParameter(1, "%4483%")
				.setEntity(2, dept);//hql语句中可以有实体类类型
		
		//3.执行查询
		List<Employee> emps=query.list();
		System.out.println(emps.size());
	}
	
	@Test
	public void testHQLNamedParameter(){
		//1.创建Query对象
		//基于命名的参数
		String hql="FROM Employee e WHERE e.salary > :sal AND e.email LIKE :email ORDER BY e.salary DESC";
		Query<Employee> query=session.createQuery(hql, Employee.class);
		
		//2.绑定参数(使用命名参数)
		query.setParameter("sal", (float)4000).setParameter("email", "%4483%");
		
		//3.执行查询
		List<Employee> emps=query.list();
		System.out.println(emps.size());
	}
	
	/**
	 * 分页查询
	 */
	@Test
	public void testPageQuery(){
		String hql="FROM Employee";
		Query<Employee> query=session.createQuery(hql, Employee.class);
		
		//第一页第一条的索引是0
		int pageNo=3;//第几页
		int pageSize=5;//每页几条数据
		
		List<Employee> emps=query.setFirstResult( (pageNo-1) * pageSize )//第pageNo页的第一条数据
			    								.setMaxResults(pageSize)
			    								.list();
		System.out.println(emps);
	}
	
	/**
	 * 命名查询法
	 */
	@Test
	public void testNamedQuery(){
		Query<Employee> query=session.getNamedQuery("salaryEmps");//salaryEmps名称来源于.hbm.xml文件中的<query/>元素
		List<Employee> emps=query.setParameter("minSal", (float)3000)
												.setParameter("maxSal", (float)5000).list();
		
		System.out.println(emps.size());
	}
	
	/**
	 * 投影查询(查部分属性)
	 */
	@Test
	public void testFieldQuery(){
		String hql="SELECT e.email,e.salary,e.dept FROM Employee e WHERE e.dept=:dept";
		Query<Object[]> query=session.createQuery(hql, Object[].class);//返回的每一条数据是含这3个元素的数组[e.email]  [e.salary]  [e.dept]
		
		Department dept=new Department();
		dept.setId(17);
		List<Object[]> result=query.setParameter("dept", dept)
												.list();
		
		for(Object[] objs:result){
			System.out.println(Arrays.asList(objs));//将数组objs转换成List，方便输出
			//或者 System.out.println(objs[0]+": "+objs[1]+": "+objs[2]);
		}
	}
	
	/**
	 * 投影查询(查部分属性,用new  构造器方式)
	 */
	@Test
	public void testFieldQuery2(){
		String hql="SELECT new Employee(e.salary,e.email,e.dept) "
				+"FROM Employee e "
				+"WHERE e.dept=:dept";
		Query<Employee> query=session.createQuery(hql, Employee.class);
		
		Department dept=new Department();
		dept.setId(17);
		List<Employee> result=query.setParameter("dept", dept)
												.list();
		
		for(Employee emp:result){
			System.out.println(emp.getId()+", "+emp.getName()+", "+emp.getEmail()+", "+emp.getSalary()+", "+emp.getDept());
		}
	}
	
	
	/**
	 * 报表查询
	 */
	@Test
	public void testGroupBy(){
		String hql="SELECT min(e.salary),max(e.salary) "
				     +"FROM Employee e "
				     +"GROUP BY e.dept "
				     +"HAVING min(e.salary)>:minSal";
		
		Query<Object[]> query=session.createQuery(hql, Object[].class)
				                                     .setParameter("minSal", (float)3000);
		
		List<Object[]> result=query.list();
		for(Object[] objs:result){
			System.out.println(Arrays.asList(objs));//将数组objs转换成List，方便输出
			//或者 System.out.println(objs[0]+": "+objs[1]+": "+objs[2]);
		}
	}
	
	/**
	 * 迫切左外连接(推荐使用),一对多
	 */
	@Test
	public void testLeftJoinFetch(){
		//String hql="SELECT DISTINCT d FROM Department d LEFT JOIN FETCH d.emps";//DISTINCT用来去重
		String hql="FROM Department d LEFT JOIN FETCH d.emps";//不用DISTINCT的话，可以用另外一种去重方法。
		Query<Department> query=session.createQuery(hql, Department.class);
		
		List<Department> depts=query.list();//返回的是集合对象Department；Employee集合都被初始化
		depts=new ArrayList<>(new LinkedHashSet<>(depts));//第二种去重方法
		System.out.println(depts.size());
		
		for(Department dept:depts){
			System.out.println(dept.getName()+"-"+dept.getEmps().size());
		}
	}
	
	/**
	 * 左外连接
	 */
	@Test
	public void testLeftJoin(){
		String hql="FROM Department d LEFT JOIN d.emps";
		
		Query<Object[]> query=session.createQuery(hql, Object[].class);
		
		List<Object[]> result=query.list();//返回的是Department对象和Employee对象组成的数组；根据配置文件来决定Employee集合的检索策略

		for(Object[] objs:result){
			System.out.println(Arrays.asList(objs));
		}
	}
	
	/**
	 * 迫切内连接
	 */
	@Test
	public void testLeftInnerFetch(){
		//String hql="SELECT DISTINCT d FROM Department d INNER JOIN FETCH d.emps";//DISTINCT用来去重
				String hql="FROM Department d INNER JOIN FETCH d.emps";//不用DISTINCT的话，可以用另外一种去重方法。
				Query<Department> query=session.createQuery(hql, Department.class);
				
				List<Department> depts=query.list();//返回的是集合对象Department；Employee集合都被初始化
				depts=new ArrayList<>(new LinkedHashSet<>(depts));//第二种去重方法
				System.out.println(depts.size());
				
				for(Department dept:depts){
					System.out.println(dept.getName()+"-"+dept.getEmps().size());
				}
	}
	
	/**
	 * 迫切左外连接(推荐使用),多对一
	 */
	@Test
	public void testLeftJoinFetch2(){
		String hql="FROM Employee e LEFT JOIN FETCH e.dept";
		Query<Employee> query=session.createQuery(hql, Employee.class);
		
		List<Employee> emps=query.list();
		System.out.println(emps.size());
		
		for(Employee emp:emps){
			System.out.println(emp.getName()+", "+emp.getDept().getName());
		}
	}
	
	//HQL查询语句中若显示指定了检索策略，则会覆盖映射文件中配置的检索策略
	
}
