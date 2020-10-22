# JDBC_LIB



## My JDBC Utility



**Sample Code**

```java
public class TestMain {
  // Must Be changed Data
  public static final String SERVER = "127.0.0.1";
  public static final String PORT = "3306";
  public static final String DBNAME = "SKB_VCS";
  public static final String USER = "skbvcs";
  public static final String PASSWD = "skbvcs.123";


  public static void main(String[] args) {
    // Connect DB
    DbManager dbManager = DbManager.getInstance();
    dbManager.start(SERVER, PORT, DBNAME, USER, PASSWD);

    // CREATE Query
    int result = dbManager.updateQueryResult("CREATE TABLE TEST_TABLE(" +
                                             "id int(11) NOT NULL PRIMARY KEY AUTO_INCREMENT,\n" +
                                             "memo mediumtext," +
                                             "number int(11))");

    System.out.println("UPDATE RESULT : " + result);

    // INSERT Query
    System.out.println(dbManager.updateQueryResult("INSERT INTO TEST_TABLE(memo) VALUES(?)", "InsertTest1") + " : record(s) updated.");
    System.out.println(dbManager.updateQueryResult("INSERT INTO TEST_TABLE(memo) VALUES(?)", "InsertTest2") + " : record(s) updated.");
    System.out.println(dbManager.updateQueryResult("INSERT INTO TEST_TABLE(memo) VALUES(?)", "InsertTest3") + " : record(s) updated.");
    System.out.println(dbManager.updateQueryResult("INSERT INTO TEST_TABLE(number) VALUES(33)")+ " : record(s) updated.");

    // SELECT Query
    String strResult = dbManager.doQueryResultAsString("SELECT * FROM TEST_TABLE");
    System.out.println(strResult);

    List<Map<String, Object>> resultMap = dbManager.doQueryResultAsListMap("SELECT * FROM TEST_TABLE");
    System.out.println("Result Map : "+resultMap);

    List<TestTable> tableReuslt = dbManager.doQueryResultAsListObj("SELECT * FROM TEST_TABLE", TestTable.class);
    System.out.println("Result List<T> : "+tableReuslt);

    // DELETE Query
    result = dbManager.updateQueryResult("DELETE FROM TEST_TABLE WHERE id<10");
    System.out.println("Delete Result :"+result);

    // Drop Query
    dbManager.doQueryResultAsString("DROP TABLE TEST_TABLE");
    sqlTest();
  }

  public static void sqlTest(){
    DbManager dbManager = DbManager.getInstance();
    while(true){
      Scanner sc = new Scanner(System.in);
      String input = sc.nextLine();
      if(input.equals("exit")) break;
      System.out.println(dbManager.doQueryResultAsString(input));
      System.out.println(dbManager.doQueryResultAsListMap(input));
    }
  }
}
```

