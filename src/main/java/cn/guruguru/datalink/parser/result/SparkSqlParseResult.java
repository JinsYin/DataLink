package cn.guruguru.datalink.parser.result;

import cn.guruguru.datalink.parser.ParseResult;

/**
 * [TODO] Parser result for spark sql
 */
public class SparkSqlParseResult implements ParseResult {
    @Override
    public void execute() throws Exception {
        throw new UnsupportedOperationException("Spark engine not supported");
    }
}
