package com.hee;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

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
            /* code 작성 */
            Member findMember = entityManager.find(Member.class, 1L); // select
            System.out.println("findMember.id = " + findMember.getId());
            System.out.println("findMember.name = " + findMember.getName());

            findMember.setName("changeName"); // update
//            entityManager.persist(findMember); // update 시 불필요 (transaction commit 전에 변경사항을 모두 확인하여 update 하기 때문)

//            entityManager.remove(findMember); // delete

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            entityManager.close(); // 중요!
        }
        entityManagerFactory.close();
    }
}
