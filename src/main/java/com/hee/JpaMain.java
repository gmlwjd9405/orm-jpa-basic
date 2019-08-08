package com.hee;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class JpaMain {

    public static void main(String[] args) {
        /* EntityManagerFactory
         * application(WebServer) loading 시점에 DB 당 딱 하나만 생성되어야 함. */
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("hello");
        /* EntityManager
         * 실제 Transaction 단위를 수행할 때마다 생성 (고객의 요청이 올 때마다 사용했다가 닫음)
         * thread 간에 공유하면 안 됨. (사용하고 버려야 함) */
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        // Data 를 "변경"하는 모든 작업은 반드시 Transaction 안에서 이루어져야 함.
        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();

        try {
            // 영속 상태 (Persistence Context 에 의해 Entity 가 관리되는 상태)
            Member member = new Member(200L, "A");
            entityManager.persist(member);

            entityManager.flush(); // 강제 호출 (쿼리가 DB 에 반영됨)

            System.out.println("DB INSERT Query 가 즉시 나감. -- flush() 호출 후 --  Transaction commit 됨.");
            tx.commit(); // DB에 insert query 가 날라가는 시점 (Transaction commit)
        } catch (Exception e) {
            tx.rollback();
        } finally {
            entityManager.close(); // 중요!
        }
        entityManagerFactory.close();
    }
}
