package jpatest;

import javax.persistence.*;

@Entity(name = "department")
public class Department {
  private int Id;
  private String Site;

  public Department(){}

  @Id public int getId() {return Id;}
  public void setId(int id) {Id=id;}

  @Column(name="location")
  public String getSite() {return Site;}
  public void setSite(String site) {Site=site;}

  public String toString() {
  return "Department: " + getId() + " based in " + getSite();
 }
}

