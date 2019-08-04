package com.hee;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JpaMain {

    public static void main(String[] args) {
        // EntityManagerFactory: application loading 시점에 딱 하나만 생성되어야 함.
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("hello");
        // EntityManager: 실제 Transaction 단위를 수행할 때마다 생성
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        // Data 를 변경하는 모든 작업은 반드시 Transaction 안에서 이루어져야 함.
        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();

        try {
            /* code 작성 */
            Member findMember = entityManager.find(Member.class, 1L); // select
            System.out.println("findMember.id = " + findMember.getId());
            System.out.println("findMember.name = " + findMember.getName());

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            entityManager.close(); // 중요!
        }
        entityManagerFactory.close();
    }
}
