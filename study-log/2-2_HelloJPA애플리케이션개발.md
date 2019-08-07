## Hello JPA - 애플리케이션 개발 
- **EntityManagerFactory**
    - JPA 는 EntityManagerFactory 를 만들어야 한다.
    - application loading 시점에 DB 당 딱 하나만 생성되어야 한다.
    - `EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("hello");`
        - persistence.xml 의 <persistence-unit name="hello"> 와 동일한 이름으로 인자값을 설정해야 한다.
        - 그래야 설정 파일의 정보들을 읽어와 해당 객체를 만들 수 있다.
    - `entityManagerFactory.close();`
        - WAS 가 종료되는 시점에 EntityManagerFactory 를 닫는다.
        - 그래야 내부적으로 Connection pooling 에 대한 Resource 가 Release 된다.
- **EntityManager** 
    - 실제 Transaction 단위를 수행할 때마다 생성한다. 
        - 즉, 고객의 요청이 올 때마다 사용했다가 닫는다.
    - thread 간에 공유하면 안된다. (사용하고 버려야 한다.)
    - `entityManager.close();`
        - Transaction 수행 후에는 반드시 EntityManager 를 닫는다. 
        - 그래야 내부적으로 DB Connection 을 반환한다.
- **EntityTransaction**
    - Data 를 "변경"하는 모든 작업은 반드시 Transaction 안에서 이루어져야 한다.
        - `EntityTransaction tx = entityManager.getTransaction();`
        - 단순한 조회의 경우는 상관없음.
    - `tx.begin();` : Transaction 시작 
    - `tx.commit();` : Transaction 수행 
    - `tx.rollback();` : 작업에 문제가 생겼을 시 
    
### persistence.xml 내용  
```xml
<?xml version="1.0" encoding="UTF-8"?>
<persistence version="2.2"
             xmlns="http://xmlns.jcp.org/xml/ns/persistence" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/persistence http://xmlns.jcp.org/xml/ns/persistence/persistence_2_2.xsd">
    <persistence-unit name="hello">
        <properties>
            <!-- 필수 속성 -->
            <property name="javax.persistence.jdbc.driver" value="org.h2.Driver"/>
            <property name="javax.persistence.jdbc.user" value="sa"/>
            <property name="javax.persistence.jdbc.password" value=""/>
            <property name="javax.persistence.jdbc.url" value="jdbc:h2:tcp://localhost/~/test"/>
            <property name="hibernate.dialect" value="org.hibernate.dialect.H2Dialect"/>

            <!-- 옵션 -->
            <property name="hibernate.show_sql" value="true"/>
            <property name="hibernate.format_sql" value="true"/>
            <property name="hibernate.use_sql_comments" value="true"/>
            <!--<property name="hibernate.hbm2ddl.auto" value="create" />-->
        </properties>
    </persistence-unit>
</persistence>
```

### 기본적인 JPA 사용 코드 
```java
public class JpaMain {
    public static void main(String[] args) {
        // EntityManagerFactory 
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("hello");
        // EntityManager 
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        // Data 를 "변경"하는 모든 작업은 반드시 Transaction 안에서 이루어져야 함.
        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();

        try {
            /* [아래 참고] Data 를 "변경"하는 모든 작업에 대한 code 작성 */
            
            tx.commit();
        } catch (Exception e) {
            tx.rollback();  
        } finally {
            entityManager.close(); // 중요!
        }
        entityManagerFactory.close();
    }
}
```

#### try-catch 내부 코드 1 - entityManager 를 통한 기본 CRUD 코드 

```java
Member member = new Member();
member.setId(1L);
member.setName("HelloA");
entityManager.persist(member); // save
```
```java
Member findMember = entityManager.find(Member.class, 1L); // select
System.out.println("findMember.id = " + findMember.getId());
System.out.println("findMember.name = " + findMember.getName());
```
```java
Member findMember = entityManager.find(Member.class, 1L); // select
findMember.setName("changeName"); // update
// update 시 persist 불필요 (transaction commit 전에 변경사항을 모두 확인하여 update 하기 때문)
// entityManager.persist(findMember); 
```
```java
Member findMember = entityManager.find(Member.class, 1L); // select
entityManager.remove(findMember); // delete
```

#### try-catch 내부 코드 2 - JPQL 사용 
- JPQL
    - JPQL은 SQL 을 추상화한 객체 지향 쿼리 언어이다.
    - 테이블이 아닌 "Entity 객체"를 대상으로 검색할 수 있다.

```java
List<Member> result = entityManager.createQuery("select m from Member as m", Member.class)
        .setFirstResult(5) // 5번부터
        .setMaxResults(8) // 8개 가져오기 (페이징)
        .getResultList();
for (Member member : result) {
    System.out.println("member.getName() = " + member.getName());
}
```