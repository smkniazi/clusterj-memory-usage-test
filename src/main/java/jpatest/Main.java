package jpatest;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.io.*;

public class Main {

  public static void main(String[] args) throws java.io.IOException {

    EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(
            "hop_salman");
    EntityManager em = entityManagerFactory.createEntityManager();
    EntityTransaction userTransaction = em.getTransaction();

    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));


    userTransaction.begin();
    Employee emp = new Employee();
    emp.setId(1);
    emp.setDepartment(666);
    emp.setFirst("Billy");
    emp.setLast("Fish");
    emp.setStarted("1st February 2009");
    em.persist(emp);
    userTransaction.commit();

    userTransaction.begin();
    Employee theEmployee = em.find(Employee.class, 1);
    userTransaction.commit();

    System.out.println("New emp" + theEmployee.toString());


    userTransaction.begin();
    theEmployee.setCity("London");
    theEmployee.setDepartment(1);
    userTransaction.commit();


    Department dept;

    userTransaction.begin();
    dept = new Department();
    dept.setId(1);
    dept.setSite("Building-" + 0);
    em.persist(dept);
    userTransaction.commit();


    System.out.println("Chance to check the City is set in the database");
    System.out.println("Hit return when you are done");
    br.readLine();

    userTransaction.begin();
    Query q = em.createQuery("select x from employee x where x.department=1");
    Query qd;

    for (Employee m : (List<Employee>) q.getResultList()) {
      System.out.println(m.toString());
      qd = em.createQuery("select x from department x where x.id=1");
      for (Department d : (List<Department>) qd.getResultList()) {
        System.out.println(d.toString());
      }
    }
    userTransaction.commit();

    em.close();
    entityManagerFactory.close();
  }
}
