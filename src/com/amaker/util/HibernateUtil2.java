package com.amaker.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class HibernateUtil2 {
	
	private HibernateUtil2(){}
	
	private static HibernateUtil2 instance=new HibernateUtil2();//单实例模式
	
	public static HibernateUtil2 getInstance(){
		return instance;
	}
	
	private SessionFactory sessionFactory;
	
	public SessionFactory getSessionFactory(){
		if(sessionFactory==null){
			//Hibernate 5.0的写法
			ServiceRegistry serviceRegistry=new StandardServiceRegistryBuilder()
																.configure().build();//创建服务注册对象
			sessionFactory=new MetadataSources(serviceRegistry).buildMetadata()
																						.buildSessionFactory();
		}
		return sessionFactory;
	}
	
	public Session getSession(){
		return getSessionFactory().getCurrentSession();
	}
}
