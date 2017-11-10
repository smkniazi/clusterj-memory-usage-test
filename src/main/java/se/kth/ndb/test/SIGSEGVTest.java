/*
 * Copyright (C) 2015 hops.io.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package se.kth.ndb.test;

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

import java.util.List;
import java.util.Properties;
import java.util.Random;

public class SIGSEGVTest {

  @Option(name = "-schema", usage = "DB schemma name. Default is test")
  private String schema = "test";
  @Option(name = "-dbHost", usage = "com.mysql.clusterj.connectstring. Default is bbc2")
  private String dbHost = "bbc2.sics.se";
  @Option(name = "-rows", usage = "Num of rows in the table. Default 45.")
  private int num_rows = 45;
  @Option(name = "-sessions", usage = "Num of sessions created. Default 1000.")
  private int num_sessions = 1000;
  @Option(name = "-help", usage = "Print usages")
  private boolean help = false;



  private SessionFactory sf;
  private Session simpleSessionPool[];
  private Random rand = new Random();

  private void setUpDBConnection() throws Exception {
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
    createSessions();
  }

  private void populateDB() throws Exception {
    Session session = sf.getSession();
    session.currentTransaction().begin();
    for (int i = 0; i < num_rows; i++) {
      Table.SIGTable row = session.newInstance(Table.SIGTable.class);
      row.setId(i);
      row.setCol1(i+1);
      row.setCol2("test"+i);
      session.savePersistent(row);
    }
    session.currentTransaction().commit();
    session.close();
    System.out.println("Test data created.");
  }


  private void runTest(){
    long startTime = System.currentTimeMillis();
    int round =0;
    while(true){
      Session session = getSession();
      for(int i=1;i<num_rows; i++){
        session.currentTransaction().begin();
        session.setLockMode(LockMode.EXCLUSIVE);
        QueryBuilder qb = session.getQueryBuilder();
        QueryDomainType<Table.SIGTable> dobj = qb.createQueryDefinition(
            Table.SIGTable.class);
        Predicate pred1 = dobj.get("col1").equal(dobj.param("param"));
        dobj.where(pred1);
        Query<Table.SIGTable> query = session.createQuery(dobj);
        query.setParameter("param", i);
        List<Table.SIGTable> results = query.getResultList();
        session.currentTransaction().commit();
      }
      round++;
      System.out.println("Round " + round + "  ---- Total Time Taken = " + (
          (System.currentTimeMillis() - startTime)/1000) + " seconds");
    }
  }


  private void deleteAllData() throws Exception {
    Session session = sf.getSession();
    session.deletePersistentAll(Table.SIGTable.class);
    session.close();
  }

  private void parseArgs(String[] args) {
    CmdLineParser parser = new CmdLineParser(this);
    parser.setUsageWidth(80);
    try {
      parser.parseArgument(args);
    } catch (Exception e) {
      System.err.println(e.getMessage());
      parser.printUsage(System.err);
      System.err.println("Make Sure to create a SIGTable for test\n " + Table
          .CREATE_SIGTABLE);
      System.err.println();
      System.exit(-1);
    }

    if (help) {
      parser.printUsage(System.err);
      System.err.println("Make Sure to create a SIGTable for test\n " + Table
          .CREATE_SIGTABLE);
      System.exit(0);
    }
  }

  private void createSessions(){
    simpleSessionPool = new Session[num_sessions];
    for(int s=0; s<num_sessions; s++){
      simpleSessionPool[s]  = sf.getSession();
    }
  }

  private Session getSession(){
    return simpleSessionPool[rand.nextInt(num_sessions)];
  }

  public void start(String[] argv) throws Exception {
    parseArgs(argv);
    setUpDBConnection();
    deleteAllData();
    populateDB();
    runTest();
  }
}
