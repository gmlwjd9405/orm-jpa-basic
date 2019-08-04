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
            /* code 작성 */
            // JPQL(SQL 을 추상화한 객체 지향 쿼리 언어) 사용: 테이블이 아닌 "Entity 객체"를 대상으로 검색
            List<Member> result = entityManager.createQuery("select m from Member as m", Member.class)
                    .setFirstResult(5) // 5번부터
                    .setMaxResults(8) // 8개 가져오기 (페이징)
                    .getResultList();
            for (Member member : result) {
                System.out.println("member.getName() = " + member.getName());
            }

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            entityManager.close(); // 중요!
        }
        entityManagerFactory.close();
    }
}
