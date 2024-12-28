package uz.gym.crm;


import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.ComponentScan;

import uz.gym.crm.config.HibernateUtil;
import uz.gym.crm.domain.User;

@ComponentScan(basePackages = "uz.gym.crm")
public class App {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        Session session = null;
        Transaction transaction = null;

        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            System.out.println("aa"+App.class.getClassLoader().getResource("hibernate.cfg.xml"));

            // Create a test User entity
            User user = new User();
            user.setFirstName("Mash");
            user.setLastName("Ivanov");
            user.setUsername("masha.ivanova2");
            user.setPassword("password123");
            user.setActive(true);

            // Save the User entity to the database
            session.save(user);

            transaction.commit();
            System.out.println("User saved successfully!");

        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
            HibernateUtil.shutdown();
        }
    }
}