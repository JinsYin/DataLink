package cn.guruguru.datalink.ddl.converter;

import cn.guruguru.datalink.ddl.table.Affix;
import cn.guruguru.datalink.ddl.table.AffixStrategy;
import cn.guruguru.datalink.ddl.table.CaseStrategy;
import cn.guruguru.datalink.ddl.table.JdbcDialect;
import cn.guruguru.datalink.ddl.result.FlinkDdlConverterResult;
import cn.guruguru.datalink.ddl.table.TableDuplicateStrategy;
import cn.guruguru.datalink.ddl.table.TableField;
import cn.guruguru.datalink.ddl.table.TableSchema;
import cn.guruguru.datalink.utils.SqlUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FlinkDdlConverterTest {

    private static final FlinkDdlConverter flinkSqlConverter = new FlinkDdlConverter();

    @Test
    public void testDmDDL() {
        String ddl = "CREATE TABLE DATALAKE_TEST.CORP_INFO_INVESTOR (\n"
                + " \"id\" NUMBER(15,5) NOT NULL,\n"
                + " \"lastupdateddt\" TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL\n"
                + ");";
        FlinkDdlConverterResult actualResult = flinkSqlConverter.convertSql(
                JdbcDialect.DMDB, "P1_CATALOG1", "DB1", ddl, CaseStrategy.UPPERCASE);
        String actualDDL = SqlUtil.compress(actualResult.getSql());
        String expectedDDL = "CREATE DATABASE IF NOT EXISTS `P1_CATALOG1`.`DATALAKE_TEST`;" +
                "CREATE TABLE IF NOT EXISTS `P1_CATALOG1`.`DATALAKE_TEST`.`CORP_INFO_INVESTOR` (" +
                "`ID` DECIMAL(15, 5) NOT NULL, " +
                "`LASTUPDATEDDT` TIMESTAMP(6) NOT NULL" +
                ");";
        System.out.println(actualDDL);
        Assert.assertEquals(expectedDDL, actualDDL);
    }

    @Test
    public void testOracleDDL() {
        String ddl = "CREATE TABLE DATALAKE_TEST.CORP_INFO_INVESTOR (\n"
                + " \"id\" NUMBER(38,0) NOT NULL,\n"
                + " \"lastupdateddt\" TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL\n"
                + ");";
        FlinkDdlConverterResult actualResult = flinkSqlConverter.convertSql(
                JdbcDialect.Oracle, "P1_CATALOG1", "DB1", ddl, CaseStrategy.UPPERCASE);
        String actualDDL = SqlUtil.compress(actualResult.getSql());
        String expectedDDL = "CREATE DATABASE IF NOT EXISTS `P1_CATALOG1`.`DATALAKE_TEST`;" +
                "CREATE TABLE IF NOT EXISTS `P1_CATALOG1`.`DATALAKE_TEST`.`CORP_INFO_INVESTOR` (" +
                "`ID` DECIMAL(38, 0) NOT NULL, " +
                "`LASTUPDATEDDT` TIMESTAMP(6) NOT NULL" +
                ");";
        System.out.println(actualDDL);
        Assert.assertEquals(expectedDDL, actualDDL);
    }

    @Test
    public void testConvertSingleStatementForOracle() {
        String createSQL =
            "CREATE TABLE \"ADM_BDPP\".\"PARAMSYS\" \n"
                + "   (\t\"PARAM_SEQUENCE\" NUMBER(15,0) NOT NULL ENABLE, \n"
                + "  TASKNAME VARCHAR2(50) NOT NULL DEFAULT '123', \n"
                + "\t\"TASKINTERVAL\" VARCHAR2(10 CHAR) NOT NULL DEFAULT 0, \n"
                + "SUPPLEMENTAL LOG DATA (ALL) COLUMNS"
                + "   )";
        FlinkDdlConverterResult actualResult = flinkSqlConverter.convertSql(
                JdbcDialect.Oracle, "P1_CATALOG1", "DB1", createSQL);
        String actualDDL = SqlUtil.compress(actualResult.getSql());
        String expectedDDL = "CREATE DATABASE IF NOT EXISTS `P1_CATALOG1`.`ADM_BDPP`;"
                + "CREATE TABLE IF NOT EXISTS `P1_CATALOG1`.`ADM_BDPP`.`PARAMSYS` ("
                + "`PARAM_SEQUENCE` DECIMAL(15, 0) NOT NULL, "
                + "`TASKNAME` STRING NOT NULL, "
                + "`TASKINTERVAL` STRING NOT NULL"
                + ");";
        System.out.println(actualDDL);
        Assert.assertEquals(expectedDDL, actualDDL);
    }

    @Test
    public void testConvertMultiStatementsForOracle() {
        String sqls = "-- 123\n" +
                "CREATE TABLE \"API_OPER\".\"EDG25_APP_MESSAGE\" \n" +
                "   (    \"ID\" VARCHAR2(32 CHAR) NOT NULL ENABLE, -- abc\n" +
                "    \"AID\" VARCHAR2(32 CHAR), \n" +
                "    \"INFO\" VARCHAR2(2048 CHAR), \n" +
                "     PRIMARY KEY (\"ID\")\n" +
                "  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS \n" +
                "  TABLESPACE \"SRC_DATA\"  ENABLE\n" +
                "   ) SEGMENT CREATION DEFERRED \n" +
                "  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 \n" +
                " NOCOMPRESS LOGGING\n" +
                "  TABLESPACE \"SRC_DATA\" ;\n" +
                "\n" +
                "COMMENT ON COLUMN \"API_OPER\".\"EDG25_APP_MESSAGE\".\"ID\" IS '主键';\n" +
                "   COMMENT ON COLUMN \"API_OPER\".\"EDG25_APP_MESSAGE\".\"AID\" IS 'appList主键';\n" +
                "   COMMENT ON COLUMN \"API_OPER\".\"EDG25_APP_MESSAGE\".\"INFO\" IS '发送日期';";

        FlinkDdlConverterResult actualResult = flinkSqlConverter.convertSql(
                JdbcDialect.Oracle, "P1_CATALOG1", "DB1", sqls);
        String actualDDL = SqlUtil.compress(actualResult.getSql());
        String expectedDDL =
                "CREATE DATABASE IF NOT EXISTS `P1_CATALOG1`.`API_OPER`;"
                        + "CREATE TABLE IF NOT EXISTS `P1_CATALOG1`.`API_OPER`.`EDG25_APP_MESSAGE` ("
                        + "`ID` STRING NOT NULL COMMENT '主键', "
                        + "`AID` STRING COMMENT 'appList主键', "
                        + "`INFO` STRING COMMENT '发送日期'"
                        + ");";
        System.out.println(actualDDL);
        Assert.assertEquals(expectedDDL, actualDDL);
    }

    @Test
    public void testConvertTableSchema() {
        TableField idColumn = new TableField("ID", "VARCHAR2", null, null, "主键", false, false, false);
        TableField aidColumn = new TableField("AID", "VARCHAR2", null, null, null,false, false, false);
        TableField infoColumn = new TableField("INFO", "VARCHAR2", null, null, "发送日期", false, false, false);
        List<TableField> fields = Arrays.asList(idColumn, aidColumn, infoColumn);
        TableSchema tableSchema = TableSchema.builder()
                .catalog("P1_CATALOG1")
                .database("API_OPER")
                .tableName("EDG25_APP_MESSAGE")
                .tableComment("Test Table")
                .fields(fields)
                .build();
        List<TableSchema> tableSchemas = Collections.singletonList(tableSchema);
        FlinkDdlConverterResult result = flinkSqlConverter.convertSchema(
                JdbcDialect.Oracle,
                tableSchemas,
                new Affix(AffixStrategy.SUFFIX, "_fIx"),
                new Affix(AffixStrategy.PREFIX, "pRe_"),
                TableDuplicateStrategy.IGNORE,
                CaseStrategy.LOWERCASE);
        String actualDDL = SqlUtil.compress(result.getSql());
        String expectedDDL = "CREATE DATABASE IF NOT EXISTS `p1_catalog1`.`api_oper_fix`;"
                + "CREATE TABLE IF NOT EXISTS `p1_catalog1`.`api_oper_fix`.`pre_edg25_app_message` ("
                + "`id` STRING '主键', "
                + "`aid` STRING, "
                + "`info` STRING '发送日期'"
                + ") COMMENT 'Test Table';";
        System.out.println(actualDDL);
        Assert.assertEquals(expectedDDL, actualDDL);
    }
}
