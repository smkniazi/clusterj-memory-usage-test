package se.kth.ndb.test;

import com.google.common.primitives.SignedBytes;
import com.mysql.clusterj.ClusterJHelper;
import com.mysql.clusterj.LockMode;
import com.mysql.clusterj.Query;
import com.mysql.clusterj.Session;
import com.mysql.clusterj.SessionFactory;
import com.mysql.clusterj.query.Predicate;
import com.mysql.clusterj.query.QueryBuilder;
import com.mysql.clusterj.query.QueryDomainType;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.util.*;

public class ProjectionTest {
  @Option(name = "-schema", usage = "DB schemma name. Default is hop_salman")
  static private String schema = "hop_salman";
  @Option(name = "-createData", usage = "Number of rows. Default 1000")
  static private boolean createData = false;
  @Option(name = "-numRows", usage = "Number of rows. Default 1000")
  static private int numRows = 1000;
  @Option(name = "-batchSize",
          usage = "BatchSize. Row to read after projected scan in batch op. Default 1000")
  static private int batchSize = 1000;
  @Option(name = "-dbHost", usage = "com.mysql.clusterj.connectstring. Default is localhost")
  static private String dbHost = "localhost";

  @Option(name = "-partitionId", usage = "partitionId")
  long partitionId = 0;

  @Option(name = "-parentId", usage = "parentId")
  long parentId = 0;

  @Option(name = "-userId", usage = "userId")
  int userId = 0;

  @Option(name = "-groupId", usage = "groupId")
  int groupId = 0;

  @Option(name = "-help", usage = "Print usages")
  private boolean help = false;

  Random rand = new Random(System.currentTimeMillis());
  SessionFactory sf = null;

  // ----

  public void startApplication(String[] args) throws Exception {
    parseArgs(args);

    setUpDBConnection();

    populateDB();

    System.out.println("Press enter to start execution");
    System.in.read();
    startTest();

    System.out.println("Press enter to shut down");
    System.in.read();
    sf.close();
  }

  private void parseArgs(String[] args) {
    CmdLineParser parser = new CmdLineParser(this);
    parser.setUsageWidth(80);
    try {
      // parse the arguments.
      parser.parseArgument(args);

      if (batchSize > numRows) {
        System.err.println("Batch size can not greater that number of rows ");
        System.exit(1);
      }

    } catch (Exception e) {
      System.err.println(e.getMessage());
      parser.printUsage(System.err);
      System.err.println();
      System.exit(1);
    }

    if (help) {
      parser.printUsage(System.err);
      System.exit(0);
    }
  }

  public void setUpDBConnection() throws Exception {
    Properties props = new Properties();
    props.setProperty("com.mysql.clusterj.connectstring", dbHost);
    props.setProperty("com.mysql.clusterj.database", schema);
    props.setProperty("com.mysql.clusterj.connect.retries", "4");
    props.setProperty("com.mysql.clusterj.connect.delay", "5");
    props.setProperty("com.mysql.clusterj.connect.verbose", "1");
    props.setProperty("com.mysql.clusterj.connect.timeout.before", "30");
    props.setProperty("com.mysql.clusterj.connect.timeout.after", "20");
    props.setProperty("com.mysql.clusterj.max.transactions", "1024");
    props.setProperty("com.mysql.clusterj.connection.pool.size", "1");
    sf = ClusterJHelper.getSessionFactory(props);
  }


  public void startTest() throws Exception {
    Session session = sf.getSession();


    System.out.println("---- Without Projection ----");
    long time = System.currentTimeMillis();
    List<INodePK> list1 = readDataWithoutProjection(session, parentId, partitionId);
    System.out.println("Time to read all rows : " + (System.currentTimeMillis() - time));
    time = System.currentTimeMillis();
    Collections.sort(list1);
    System.out.println("Time to sort : " + (System.currentTimeMillis() - time));

    System.out.println("\n\n---- With Projection ----");
    time = System.currentTimeMillis();
    List<INodePK> list2 = readDataWithProjection(session, parentId, partitionId);
    System.out.println("Time to read all rows : " + (System.currentTimeMillis() - time));
    time = System.currentTimeMillis();
    Collections.sort(list2);
    System.out.println("Time to sort : " + (System.currentTimeMillis() - time));
    time = System.currentTimeMillis();
    batchRead(list2);
    System.out.println("Time to read 1000 rows using batch op took : " + (System.currentTimeMillis() - time));


    session.close();
  }

  private void deleteAllData() throws Exception {
    Session session = sf.getSession();
    session.deletePersistentAll(Table.InodeDTO.class);
    session.close();
  }


  private void populateDB() throws Exception {
    if (!createData) {
      System.out.println("Skipped Data Creation Phase");
      return;
    }

    //deleteAllData();

    long inodeId = Long.MAX_VALUE;
    Session session = sf.getSession();
    for (int j = 0; j < numRows; j++) {
      session.currentTransaction().begin();
      long id = inodeId--;
      Table.InodeDTO row = session.newInstance(Table.InodeDTO.class);
      row.setPartitionId(partitionId);
      row.setParentId(parentId);
      row.setId(id);
      row.setName("name_" + id);

      row.setIsDir((byte) 0);
      row.setModificationTime(System.currentTimeMillis());
      row.setATime(System.currentTimeMillis());
      row.setUserID(userId);
      row.setGroupID(groupId);
      row.setPermission((short) 0755);
      //row.setClientName("client");
      //row.setClientMachine("machine");
      row.setGenerationStamp(0);
      row.setHeader(0);
      //row.setSymlink("");
      row.setQuotaEnabled((byte) 0);
      row.setUnderConstruction((byte) 0);
      row.setSubtreeLocked((byte) 0);
      row.setSubtreeLockOwner((byte) 0);
      row.setMetaEnabled((byte) 0);
      row.setSize((byte) 0);
      row.setFileStoredInDd((byte) 0);
      row.setLogicalTime((byte) 0);
      row.setStoragePolicy((byte) 0);
      row.setChildrenNum((byte) 0);
      row.setNumAces(0);
      row.setNumXAttrs((byte) 0);

      session.savePersistent(row);
      session.currentTransaction().commit();
    }
    session.close();
    System.out.println("Test data created.");
  }

  public class INodePK implements Comparable<INodePK> {
    long id;
    long paritionId;
    long parentid;
    String name;

    public INodePK(long id, long paritionId, long parentid, String name) {
      this.id = id;
      this.paritionId = paritionId;
      this.parentid = parentid;
      this.name = name;
    }

    @Override
    public final int compareTo(INodePK other) {
      String left = name == null ? "" : name;
      String right = other.name == null ? "" : other.name;
      return SignedBytes.lexicographicalComparator()
              .compare(left.getBytes(), right.getBytes());
    }
  }

  void batchRead(List<INodePK> list) {
    Session session = sf.getSession();

    session.currentTransaction().begin();
    List<Table.InodeDTO> dtos = new ArrayList<Table.InodeDTO>();
    for (int i = 0; i < batchSize; i++) {
      Table.InodeDTO dto = session.newInstance(Table.InodeDTO.class, new Object[]{
              list.get(i).paritionId,
              list.get(i).parentid,
              list.get(i).name});
      dto.setId(-1);
      dto = session.load(dto);
      dtos.add(dto);
    }
    session.flush();

    for(Table.InodeDTO dto : dtos){
      if(dto.getId() == -1 ){
        throw new IllegalStateException("Unable able to read row ");
      }
    }

    session.currentTransaction().commit();
    session.close();
  }

  public List<INodePK> readDataWithProjection(Session session, long parentId, long partitionId) throws Exception {
    QueryBuilder qb = session.getQueryBuilder();
    QueryDomainType<Table.ProjectedInodeDTO> dobj = qb.createQueryDefinition(Table.ProjectedInodeDTO.class);
    Predicate pred1 = dobj.get("partitionId").equal(dobj.param("partitionIDParam"));
    Predicate pred2 = dobj.get("parentId").equal(dobj.param("parentIDParam"));
    dobj.where(pred1.and(pred2));
    Query<Table.ProjectedInodeDTO> query = session.createQuery(dobj);
    query.setParameter("partitionIDParam", partitionId);
    query.setParameter("parentIDParam", parentId);

    List<Table.ProjectedInodeDTO> results = null;
    try {
      results = query.getResultList();
      List<INodePK> inodeList = convertProj(results);
      return inodeList;
    } finally {
      session.release(results);
    }
  }

  public List<INodePK> readDataWithoutProjection(Session session, long parentId, long partitionId) throws Exception {
    QueryBuilder qb = session.getQueryBuilder();
    QueryDomainType<Table.InodeDTO> dobj = qb.createQueryDefinition(Table.InodeDTO.class);
    Predicate pred1 = dobj.get("partitionId").equal(dobj.param("partitionIDParam"));
    Predicate pred2 = dobj.get("parentId").equal(dobj.param("parentIDParam"));
    dobj.where(pred1.and(pred2));
    Query<Table.InodeDTO> query = session.createQuery(dobj);
    query.setParameter("partitionIDParam", partitionId);
    query.setParameter("parentIDParam", parentId);

    List<Table.InodeDTO> results = null;
    try {
      results = query.getResultList();
      List<INodePK> inodeList = convert(results);
      return inodeList;
    } finally {
      session.release(results);
    }
  }

  private List<INodePK> convertProj(List<Table.ProjectedInodeDTO> list) throws Exception {
    List<INodePK> inodes = new ArrayList<INodePK>();
    for (Table.ProjectedInodeDTO persistable : list) {
      inodes.add(convert(persistable.getId(), persistable.getPartitionId(),
              persistable.getParentId(), persistable.getName()));
    }
    return inodes;

  }

  private List<INodePK> convert(List<Table.InodeDTO> list) throws Exception {
    List<INodePK> inodes = new ArrayList<INodePK>();
    for (Table.InodeDTO persistable : list) {
      inodes.add(convert(persistable.getId(), persistable.getPartitionId(),
              persistable.getParentId(), persistable.getName()));
    }
    return inodes;

  }

  protected INodePK convert(long id, long partId, long parentId, String name) {
    return new INodePK(id, partId, parentId, name);
  }
}

