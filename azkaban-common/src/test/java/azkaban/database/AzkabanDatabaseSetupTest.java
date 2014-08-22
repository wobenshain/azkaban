/*
 * Copyright 2014 LinkedIn Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package azkaban.database;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.io.FileUtils;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import azkaban.utils.Props;

@Ignore
public class AzkabanDatabaseSetupTest {
  @BeforeClass
  public static void setupDB() throws IOException, SQLException {
    File dbDir = new File("h2dbtest");
    if (dbDir.exists()) {
      FileUtils.deleteDirectory(dbDir);
    }

    dbDir.mkdir();

    clearUnitTestDB();
  }

  @AfterClass
  public static void teardownDB() {
  }

  @Test
  public void testH2Query() throws Exception {
    Props h2Props = getH2Props();
    AzkabanDatabaseSetup setup = new AzkabanDatabaseSetup(h2Props);

    // First time will create the tables
    setup.loadTableInfo();
    setup.printUpgradePlan();
    setup.updateDatabase(true, true);
    Assert.assertTrue(setup.needsUpdating());

    // Second time will update some tables. This is only for testing purpose and
    // obviously we
    // wouldn't set things up this way.
    setup.loadTableInfo();
    setup.printUpgradePlan();
    setup.updateDatabase(true, true);
    Assert.assertTrue(setup.needsUpdating());

    // Nothing to be done
    setup.loadTableInfo();
    setup.printUpgradePlan();
    Assert.assertFalse(setup.needsUpdating());
  }

  @Test
  public void testMySQLQuery() throws Exception {
    Props mysqlProps = getMySQLProps();
    AzkabanDatabaseSetup setup = new AzkabanDatabaseSetup(mysqlProps);

    // First time will create the tables
    setup.loadTableInfo();
    setup.printUpgradePlan();
    setup.updateDatabase(true, true);
    Assert.assertTrue(setup.needsUpdating());

    // Second time will update some tables. This is only for testing purpose
    // and obviously we wouldn't set things up this way.
    setup.loadTableInfo();
    setup.printUpgradePlan();
    setup.updateDatabase(true, true);
    Assert.assertTrue(setup.needsUpdating());

    // Nothing to be done
    setup.loadTableInfo();
    setup.printUpgradePlan();
    Assert.assertFalse(setup.needsUpdating());
  }

  @Test
  public void testPostgreSQLQuery() throws Exception {
    Props postgresql = getPostgreSQLProps();
    AzkabanDatabaseSetup setup = new AzkabanDatabaseSetup(postgresql);

    setup.loadTableInfo();
    setup.printUpgradePlan();
    setup.updateDatabase(true, true);
    Assert.assertTrue(setup.needsUpdating());

    setup.loadTableInfo();
    setup.printUpgradePlan();
    setup.updateDatabase(true, true);
    Assert.assertTrue(setup.needsUpdating());

    setup.loadTableInfo();
    setup.printUpgradePlan();
    Assert.assertFalse(setup.needsUpdating());
  }

  private static Props getH2Props() {
    Props props = new Props();
    props.put("database.type", "h2");
    props.put("h2.path", "h2dbtest/h2db");
    props.put("database.sql.scripts.dir", "unit/sql");

    return props;
  }

  private static Props getMySQLProps() {
    Props props = new Props();

    props.put("database.type", "mysql");
    props.put("mysql.port", "3306");
    props.put("mysql.host", "localhost");
    props.put("mysql.database", "azkabanunittest");
    props.put("mysql.user", "root");
    props.put("database.sql.scripts.dir", "unit/sql");
    props.put("mysql.password", "");
    props.put("mysql.numconnections", 10);

    return props;
  }

  private static Props getPostgreSQLProps() {
    Props props = new Props();

    props.put("database.type", "postgresql");
    props.put("postgresql.port", "5432");
    props.put("postgresql.host", "localhost");
    props.put("postgresql.database", "azkabanunittest");
    props.put("postgresql.user", "azkaban");
    props.put("database.sql.scripts.dir", "unit/sql");
    props.put("postgresql.password", "azpass");
    props.put("postgresql.numconnections", 10);

    return props;
  }

  private static void clearUnitTestDB() throws SQLException {
    Props props = getMySQLProps();
    props.put("mysql.database", "");

    DataSource datasource = DataSourceUtils.getDataSource(props);
    QueryRunner runner = new QueryRunner(datasource);
    try {
      runner.update("drop database azkabanunittest");
    } catch (SQLException e) {
    }
    runner.update("create database azkabanunittest");

    props = getPostgreSQLProps();
    props.put("postgresql.database", "");

    datasource = DataSourceUtils.getDataSource(props);
    runner = new QueryRunner(datasource);
    try {
      runner.update("drop database azkabanunittest");
    } catch (SQLException e) {
    }
    runner.update("create database azkabanunittest");
  }
}
