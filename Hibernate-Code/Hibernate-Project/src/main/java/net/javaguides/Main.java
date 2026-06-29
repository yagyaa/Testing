package net.javaguides;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        SessionFactory factory = new Configuration().configure()
                .addAnnotatedClass(Department.class)
                .addAnnotatedClass(Employee.class).buildSessionFactory();

        Session session = factory.openSession();
        session.beginTransaction();

        Department dept = new Department("Medical");
        Employee emp = new Employee("Alice");
        emp.setDepartment(dept);
        dept.getEmployees().add(emp);

        session.persist(dept);
        //session.get("1",Employee.class);
        session.getTransaction().commit();
        session.close();


        Session session2 = factory.openSession();

        // Query 1: Fetch all departments (This is the "1" in N+1)
        List<Department> departments = session2.createQuery("from Department", Department.class).list();

        for (Department dep : departments) {
           // System.out.println("Test");
            // Query N: For EACH department, Hibernate fires a new SQL query
            // to load employees because the collection is Lazy.
           System.out.println("Dept: " + dep.getName() + " has " + dep.getEmployees().size() + " employees.");
        }

        session2.close();
    }
}
