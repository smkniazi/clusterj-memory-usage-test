package se.kth.ndb.test;

import com.mysql.clusterj.annotation.Column;
import com.mysql.clusterj.annotation.Index;
import com.mysql.clusterj.annotation.PartitionKey;
import com.mysql.clusterj.annotation.PersistenceCapable;
import com.mysql.clusterj.annotation.PrimaryKey;

/**
 * Created by salman on 2016-09-02.
 */

/*

create table `test_table` (`partition_id` int, `id` int, int_col1 int, int_col2 int, str_col1 varchar(3000),
str_col2 varchar(3000), str_col3 varchar(3000), PRIMARY KEY (`partition_id`,`id`)) partition by key (partition_id);

create table `test_sigsegv` (`id` int, `col1` int, `col2` varchar(3000)
, PRIMARY KEY(`id`), KEY `col1_index` (`col1`));
*/

public class Table {
  @PersistenceCapable(table = "test_table")
  @PartitionKey(column = "partition_id")
  public interface TableDTO {
    @PrimaryKey
    @Column(name = "partition_id")
    int getPartitionId();
    void setPartitionId(int partitionId);

    @PrimaryKey
    @Column(name = "id")
    int getId();
    void setId(int id);

    @Column(name = "int_col1")
    int getIntCol1();
    void setIntCol1(int val);

    @Column(name = "int_col2")
    int getIntCol2();
    void setIntCol2(int val);

    @Column(name = "str_col1")
    String getStrCol1();
    void setStrCol1(String val);

    @Column(name = "str_col2")
    String getStrCol2();
    void setStrCol2(String val);

    @Column(name = "str_col3")
    String getStrCol3();
    void setStrCol3(String val);
  }
  static final String CREATE_SIGTABLE = "create table `test_sigsegv` (`id` int, " +
      "`col1` int, `col2` varchar(3000)" +
      ", PRIMARY KEY(`id`), KEY `col1_index` (`col1`));";

  @PersistenceCapable(table = "test_sigsegv")
  public interface SIGTable {

    @PrimaryKey
    @Column(name = "id")
    int getId();
    void setId(int id);

    @Column(name = "col1")
    @Index(name = "col1_index")
    int getCol1();
    void setCol1(int val);

    @Column(name = "col2")
    String getCol2();
    void setCol2(String val);
  }


}
