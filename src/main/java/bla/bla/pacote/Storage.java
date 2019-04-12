package bla.bla.pacote;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import java.io.File;

public class Storage {
    final String STORAGE_NAME = "data.db";
    final String HOME_PATH = System.getProperty("user.home") + "/threadsfun";
    final String DB_PATH = HOME_PATH + "/" + STORAGE_NAME;
    private SessionFactory factory;
    private Session session = null;

    public Storage() {
        File home = new File(HOME_PATH);
        if (!home.exists()) {
            home.mkdir();
        }
        factory = getSessionFactory();
    }

    public void store(String key,Integer lastValue){
        StoredValue value = new StoredValue();
        value.setKey(key);
        value.setLastValue(lastValue);
        store(value);
    }

    public void store(StoredValue storedValue){
        try {
            Session mysession = getSession();
            Transaction transaction = mysession.getTransaction();
            transaction.setTimeout(1000);
            if(!transaction.isActive()){
                transaction.begin();
            }
            StoredValue dbStore = mysession.find(StoredValue.class,storedValue.getKey());
            if(dbStore!=null){
                dbStore.setLastValue(storedValue.getLastValue());
                mysession.merge(dbStore);
            }else{
                mysession.persist(storedValue);
            }
            transaction.commit();

        }catch (Exception ex){
            ex.printStackTrace();
        }

    }

    public SessionFactory getSessionFactory() {

        SessionFactory sf = null;
        Configuration configuration = new Configuration();
        configuration.setProperty("hibernate.connection.driver_class", "org.sqlite.JDBC");
        configuration.setProperty("hibernate.connection.url", "jdbc:sqlite:" + DB_PATH);
        configuration.setProperty("hibernate.connection.username", "");
        configuration.setProperty("hibernate.connection.password", "");
        configuration.setProperty("hibernate.dialect", "org.lemanoman.sqlite.dialect.SQLiteDialect");
        configuration.setProperty("hibernate.hbm2ddl.auto", "update");

        configuration.addAnnotatedClass(StoredValue.class);

        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().
                applySettings(configuration.getProperties());
        sf = configuration.buildSessionFactory(builder.build());

        return sf;
    }

    public Session getSession(){
        if(session==null){
            session = factory.openSession();
        }else{
            if(!session.isOpen()){
                session = factory.openSession();
            }
        }
        return session;
    }
}
