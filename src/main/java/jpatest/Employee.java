package jpatest;

import javax.persistence.*;
@Entity(name = "employee") //Name of the table
public class Employee {
 private int Id;
 private String First;
 private String Last;
 private String City;
 private String Started;
 private String Ended;
 private int Department;

 public Employee(){}

 @Id public int getId() {return Id;}
 public void setId(int id) {Id=id;}

 public String getFirst() {return First;}
 public void setFirst(String first) {First=first;}

 public String getLast() {return Last;}
 public void setLast(String last) {Last=last;}

 @Column(name="municipality")
 public String getCity() {return City;}
 public void setCity(String city) {City=city;}

 public String getStarted() {return Started;}
 public void setStarted(String date) {Started=date;}

 public String getEnded() {return Ended;}
 public void setEnded(String date) {Ended=date;}

 public int getDepartment() {return Department;}
 public void setDepartment(int department) {Department=department;}

 public String toString() {
  return getFirst() + " " + getLast() + " (Dept " +
  getDepartment()+ ") from " + getCity() +
  " started on " + getStarted() + " & left on " + getEnded();
 }
}

