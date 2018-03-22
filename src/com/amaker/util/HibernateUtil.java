package com.amaker.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class HibernateUtil {
	//Hibernate3.0的写法
	public Session getSession3(){
		Configuration conf=new Configuration().configure();
		SessionFactory sessionFactory=conf.buildSessionFactory();//hibernate3.0的用法
		Session session=sessionFactory.openSession();
		return session;
	}
	
	
	
	//Hibernate4.0的写法
	public Session getSession4(){
		//1.创建一个SessionFactory对象
		SessionFactory sessionFactory=null;
			//1).创建Configuration对象：对应hibernate 的基本配置信息和 对象关系映射信息
		Configuration configuration=new Configuration().configure();
			//2).创建一个serviceRegistry对象：hibernate 4.x 新添加的对象。hibernate的任何配置和服务都需要在该对象中注册后才能有效
		ServiceRegistry serviceRegistry=new StandardServiceRegistryBuilder()
															.applySettings(configuration.getProperties())
															.build();
			//3).
		sessionFactory=configuration.buildSessionFactory(serviceRegistry);
		//2.创建一个Session对象
		Session session=sessionFactory.openSession();
		
		return session;
	}
	
	//Hibernate5.0的写法
	public Session getSession5(){
		//1.创建一个SessionFactory对象
		SessionFactory sessionFactory=null;
		//2.创建一个StandardServiceRegistry对象
		StandardServiceRegistry standardServiceRegistry=new StandardServiceRegistryBuilder().configure().build();
		sessionFactory=new MetadataSources(standardServiceRegistry).buildMetadata().buildSessionFactory();
		Session session=sessionFactory.openSession();
		return session;
	}
	
	//关闭Session
	public void closeSession(Session session){
		if(session!=null){
			session.close();
		}
	}
	
	//关闭SessionFactory对象
	public void closeSessionFactory(SessionFactory sessionFactory){
		if(sessionFactory!=null){
			sessionFactory.close();
		}
	}
}
