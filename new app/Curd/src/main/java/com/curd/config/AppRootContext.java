package com.curd.config;

import java.beans.PropertyVetoException;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;

import com.mchange.v2.c3p0.ComboPooledDataSource;

@Configuration
@ComponentScan(basePackages="com.curd.model",excludeFilters=@ComponentScan.Filter(Controller.class))
@PropertySource(value="classpath:DBsettings.properties")
public class AppRootContext {

	@Autowired
	Environment env;
	
	@Bean
	public DataSource c3p0DataSource(){
		ComboPooledDataSource ds = new ComboPooledDataSource();
		try {
			ds.setDriverClass("org.sqlite.JDBC");
			ds.setJdbcUrl("jdbc:sqlite:E:\\arun files\\e books\\SpringHibernateDB\\curd.sqlite");
			ds.setUser("sa");
			ds.setPassword("123");
			ds.setProperties(this.c3p0Properies());
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		return ds;
	}
	private Properties c3p0Properies() {
		Properties p = this.dbProperies();
		p.setProperty("hibernate.c3p0.min_size", "5");
		p.setProperty("hibernate.c3p0.max_size", "10");
		p.setProperty("hibernate.c3p0.timeout", "20");
		p.setProperty("hibernate.c3p0.max_statements", "50");
		p.setProperty("hibernate.c3p0.idle_test_period", "3000");
		return p;
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(){
		LocalContainerEntityManagerFactoryBean bean = new LocalContainerEntityManagerFactoryBean();
		bean.setDataSource(this.c3p0DataSource());
		bean.setPackagesToScan("com.curd.model");
		bean.setJpaProperties(this.dbProperies());
		bean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		return bean;
	}
	
	@Bean
	@Autowired
	public PlatformTransactionManager platformTransactionManager(EntityManagerFactory factory){
		JpaTransactionManager bean = new JpaTransactionManager();
		bean.setEntityManagerFactory(factory);
		return bean;
	}
	
	@Bean
	public PersistenceAnnotationBeanPostProcessor exceptionTranslation(){
		return new PersistenceAnnotationBeanPostProcessor();
	}
	
	private Properties dbProperies() {
		Properties p = new Properties();
		/*p.setProperty("hibernate.connection.driver_class", env.getProperty("hibernate.connection.driver_class"));
		p.setProperty("hibernate.dialect",env.getProperty("hibernate.dialect"));
		p.setProperty("hibernate.connection.url", env.getProperty("hibernate.connection.url"));
		p.setProperty("hibernate.connection.username", env.getProperty("hibernate.connection.username"));
		p.setProperty("hibernate.connection.password", env.getProperty("hibernate.connection.password"));*/
		p.setProperty("hibernate.dialect","com.enigmabridge.hibernate.dialect.SQLiteDialect");
		p.setProperty("hibernate.hbm2ddl.auto", "create");
		p.setProperty("hibernate.show_sql", "true");
		return p;
	}
}
