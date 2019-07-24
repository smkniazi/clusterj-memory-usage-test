package se.kth.ndb.test;

import com.mysql.clusterj.annotation.*;

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


  static final String TABLE_NAME = "hdfs_inodes";
  static final String ID = "id";
  static final String NAME = "name";
  static final String PARENT_ID = "parent_id";
  static final String PARTITION_ID = "partition_id";
  static final String IS_DIR = "is_dir";
  static final String MODIFICATION_TIME = "modification_time";
  static final String ACCESS_TIME = "access_time";
  static final String USER_ID = "user_id";
  static final String GROUP_ID = "group_id";
  static final String PERMISSION = "permission";
  static final String CLIENT_NAME = "client_name";
  static final String CLIENT_MACHINE = "client_machine";
  static final String GENERATION_STAMP = "generation_stamp";
  static final String HEADER = "header";
  static final String SYMLINK = "symlink";
  static final String QUOTA_ENABLED = "quota_enabled";
  static final String UNDER_CONSTRUCTION = "under_construction";
  static final String SUBTREE_LOCKED = "subtree_locked";
  static final String SUBTREE_LOCK_OWNER = "subtree_lock_owner";
  static final String META_ENABLED = "meta_enabled";
  static final String SIZE = "size";
  static final String FILE_STORED_IN_DB = "file_stored_in_db";
  static final String LOGICAL_TIME = "logical_time";
  static final String STORAGE_POLICY = "storage_policy";
  static final String CHILDREN_NUM = "children_num";
  static final String NUM_ACES = "num_aces";
  static final String NUM_XATTRS = "num_xattrs";

  @PersistenceCapable(table = TABLE_NAME)
  @PartitionKey(column = PARTITION_ID)
  public interface InodeDTO {
    @PrimaryKey
    @Column(name = PARTITION_ID)
    long getPartitionId();

    void setPartitionId(long partitionId);

    //id of the parent inode
    @PrimaryKey
    @Column(name = PARENT_ID)
    @Index(name = "pidex")
    long getParentId();     // id of the inode

    void setParentId(long parentid);

    @PrimaryKey
    @Column(name = NAME)
    String getName();     //name of the inode

    void setName(String name);

    @Column(name = ID)
    @Index(name = "inode_idx")
    long getId();     // id of the inode

    void setId(long id);

    @Column(name = IS_DIR)
    byte getIsDir();

    void setIsDir(byte isDir);

    // Inode
    @Column(name = MODIFICATION_TIME)
    long getModificationTime();

    void setModificationTime(long modificationTime);

    // Inode
    @Column(name = ACCESS_TIME)
    long getATime();

    void setATime(long modificationTime);

    // Inode
    @Column(name = USER_ID)
    int getUserID();

    void setUserID(int userID);

    // Inode
    @Column(name = GROUP_ID)
    int getGroupID();

    void setGroupID(int groupID);

    // Inode
    @Column(name = PERMISSION)
    short getPermission();

    void setPermission(short permission);

    // InodeFileUnderConstruction
    @Column(name = CLIENT_NAME)
    String getClientName();

    void setClientName(String isUnderConstruction);

    // InodeFileUnderConstruction
    @Column(name = CLIENT_MACHINE)
    String getClientMachine();

    void setClientMachine(String clientMachine);

    //  marker for InodeFile
    @Column(name = GENERATION_STAMP)
    int getGenerationStamp();

    void setGenerationStamp(int generation_stamp);

    // InodeFile
    @Column(name = HEADER)
    long getHeader();

    void setHeader(long header);

    //INodeSymlink
    @Column(name = SYMLINK)
    String getSymlink();

    void setSymlink(String symlink);

    @Column(name = QUOTA_ENABLED)
    byte getQuotaEnabled();

    void setQuotaEnabled(byte quotaEnabled);

    @Column(name = UNDER_CONSTRUCTION)
    byte getUnderConstruction();

    void setUnderConstruction(byte underConstruction);

    @Column(name = SUBTREE_LOCKED)
    byte getSubtreeLocked();

    void setSubtreeLocked(byte locked);

    @Column(name = SUBTREE_LOCK_OWNER)
    long getSubtreeLockOwner();

    void setSubtreeLockOwner(long leaderId);

    @Column(name = META_ENABLED)
    byte getMetaEnabled();

    void setMetaEnabled(byte metaEnabled);

    @Column(name = SIZE)
    long getSize();

    void setSize(long size);

    @Column(name = FILE_STORED_IN_DB)
    byte getFileStoredInDd();

    void setFileStoredInDd(byte isFileStoredInDB);

    @Column(name = LOGICAL_TIME)
    int getLogicalTime();

    void setLogicalTime(int logicalTime);

    @Column(name = STORAGE_POLICY)
    byte getStoragePolicy();

    void setStoragePolicy(byte storagePolicy);

    @Column(name = CHILDREN_NUM)
    int getChildrenNum();

    void setChildrenNum(int childrenNum);

    @Column(name = NUM_ACES)
    int getNumAces();

    void setNumAces(int numAces);

    @Column(name = NUM_XATTRS)
    byte getNumXAttrs();

    void setNumXAttrs(byte numXAttrs);
  }

  @Projection
  @PersistenceCapable(table = TABLE_NAME)
  @PartitionKey(column = PARTITION_ID)
  public interface ProjectedInodeDTO {
    @PrimaryKey
    @Column(name = PARTITION_ID)
    long getPartitionId();

    void setPartitionId(long partitionId);

    //id of the parent inode
    @PrimaryKey
    @Column(name = PARENT_ID)
    @Index(name = "pidex")
    long getParentId();     // id of the inode

    void setParentId(long parentid);

    @PrimaryKey
    @Column(name = NAME)
    String getName();     //name of the inode

    void setName(String name);

    @Column(name = ID)
    @Index(name = "inode_idx")
    long getId();     // id of the inode

    void setId(long id);
  }
}
