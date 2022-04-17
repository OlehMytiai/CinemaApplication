package cinema.dao.impl;

import cinema.dao.OrderDao;
import cinema.exception.DataProcessingException;
import cinema.lib.Dao;
import cinema.model.Order;
import cinema.model.User;
import cinema.util.HibernateUtil;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;

@Dao
public class OrderDaoImpl implements OrderDao {
    @Override
    public Order add(Order order) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            session.save(order);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new DataProcessingException("Can't insert order: " + order, e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return order;
    }

    @Override
    public List<Order> getByUser(User user) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Order o "
                            + "JOIN FETCH o.tickets t "
                            + "JOIN FETCH o.user "
                            + "JOIN FETCH t.movieSession ms "
                            + "JOIN FETCH ms.movie "
                            + "JOIN FETCH ms.cinemaHall "
                            + "WHERE o.user.id = :userId", Order.class)
                    .setParameter("userId", user.getId())
                    .getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Can't get orders by user: " + user, e);
        }
    }
}
