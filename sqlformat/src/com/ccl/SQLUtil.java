package com.ccl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * 文件操作工具方法
 */
public class SQLUtil
{

	/**
	 *
	 * @return HANA SQL(插入数据)
	 */
	public static String createDataSQL()
	{
		String sql = Tool.fileContent();

		sql = sql.replaceAll("((/\\*)[\\s\\S]*?(\\*/))", "");
		sql = sql.replaceAll("(SET )[\\s\\S]*?;", "");
		sql = sql.replaceAll("DROP TABLE IF EXISTS", "-- DROP TABLE IF EXISTS");
		sql = sql.replaceAll("(-- )[\\s\\S]*?\n", "");
		sql = sql.replaceAll("CREATE TABLE `[\\s\\S]*?(ENGINE=InnoDB DEFAULT CHARSET=utf8;)", "");
		sql = sql.replaceAll("`", "\"");
		sql = sql.replaceAll("\\\\'", "''");

		//**************BLOB类型的数据 导出来的展现是一段以【0x】开头的加密字符串***************
		final Set<String> blobs = Tool.getStrWithPattern("0x[0-9|A-Z|a-z]{1,}", sql);
		final StringBuilder blobTemp = new StringBuilder();
		for (final String blob : blobs)
		{
			sql = sql.replaceAll(blob, blobTemp.append("'").append(blob).append("'").toString());
			blobTemp.delete(0, blobTemp.length());
		}

		Tool.outPutFile(sql, Constant.HANA_SQL_DATA);
		return sql;
	}

	/**
	 *
	 * @return HANA SQL(建立索引)
	 */
	public static String createIndexSQL()
	{
		final StringBuilder sqlBuilder = new StringBuilder();
		final Map<String, List<String>> uniqueKeys = uniqueKeyFormat();
		for (final String tableName : uniqueKeys.keySet())
		{
			for (final String uniqueKey : uniqueKeys.get(tableName))
			{
				final String[] keyArr = uniqueKey.split(Constant.SPLIT_SPARATOR);
				final StringBuilder sb = new StringBuilder("CREATE UNIQUE INDEX ");
				sb.append(keyArr[0]).append(" ON \"").append(tableName).append("\" ").append(keyArr[1]).append(";\n");
				sqlBuilder.append(sb);
			}
		}

		Tool.outPutFile(sqlBuilder.toString(), Constant.HANA_SQL_INDEX);
		return sqlBuilder.toString();
	}

	/**
	 *
	 * @return HANA SQL(创建表)
	 */
	public static String createTableSQL()
	{
		String sql = Tool.fileContent();

		sql = sql.replaceAll("(INSERT INTO `)[\\s\\S]*?\n", "");
		sql = sql.replaceAll("UNIQUE KEY `", "KEY `");
		sql = sql.replaceAll("(KEY `)[\\s\\S]*?\n", "");
		sql = sql.replaceAll("`", "\"");
		sql = sql.replaceAll("ENGINE=InnoDB DEFAULT CHARSET=utf8", "UNLOAD PRIORITY 5 AUTO MERGE");
		sql = sql.replaceAll("DROP TABLE IF EXISTS", "-- DROP TABLE IF EXISTS");
		sql = sql.replaceAll("((/\\*)[\\s\\S]*?(\\*/))", "");
		sql = sql.replaceAll("(SET )[\\s\\S]*?;", "");
		sql = sql.replaceAll("(-- )[\\s\\S]*?\n", "");
		sql = sql.replaceAll(",\\s*\\) UNLOAD PRIORITY 5 AUTO MERGE;", "\n ) UNLOAD PRIORITY 5 AUTO MERGE;");

		sql = sql.replaceAll("CREATE TABLE", "CREATE COLUMN TABLE");
		sql = sql.replaceAll("PRIMARY KEY", "PRIMARY KEY INVERTED VALUE");

		sql = sql.replaceAll(" bigint\\(\\d{0,}\\)", " BIGINT CS_FIXED");
		sql = sql.replaceAll(" datetime\\(\\d{0,}\\)", " LONGDATE CS_LONGDATE");
		sql = sql.replaceAll(" mediumtext", " NCLOB MEMORY THRESHOLD 1000");
		sql = sql.replaceAll(" varchar", " NVARCHAR");
		sql = sql.replaceAll(" smallint\\(\\d{0,}\\)", " SMALLINT CS_INT");
		sql = sql.replaceAll(" tinyint\\(1\\)", " DECIMAL(1,0) CS_FIXED");
		sql = sql.replaceAll(" int\\(\\d{0,}\\)", " BIGINT CS_FIXED");
		sql = sql.replaceAll(" text", " NVARCHAR(5000)");
		sql = sql.replaceAll(" longblob", " BLOB MEMORY THRESHOLD 1000");
		final Set<String> decimals = Tool.getStrWithPattern("decimal\\(\\d{0,},\\d{0,}\\)", sql);
		for (final String decimal : decimals)
		{
			final String temp = decimal.replaceAll("decimal", "DECIMAL") + " CS_FIXED";
			sql = sql.replaceAll(" " + decimal.replaceAll("\\(", "\\\\(").replaceAll("\\)", "\\\\)"), " " + temp);
		}
		sql = sql.replaceAll(" double", " DECIMAL(30,8) CS_FIXED");
		final Set<String> floats = Tool.getStrWithPattern("float\\(\\d{0,},\\d{0,}\\)", sql);
		for (final String ft : floats)
		{
			final String temp = ft.replaceAll("float", "DECIMAL") + " CS_FIXED";
			sql = sql.replaceAll(" " + ft.replaceAll("\\(", "\\\\(").replaceAll("\\)", "\\\\)"), " " + temp);
		}
		sql = sql.replaceAll(" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP", " LONGDATE CS_LONGDATE");
		//sql = sql.replaceAll(" timestamp", " LONGDATE CS_LONGDATE");

		Tool.outPutFile(sql, Constant.HANA_SQL_TABLE);
		return sql;
	}

	/**
	 * 格式化索引.
	 *
	 * @return
	 */
	private static Map<String, List<String>> uniqueKeyFormat()
	{
		final Map<String, List<String>> keys = new HashMap<String, List<String>>();
		String mySQLsql = Tool.fileContent();
		mySQLsql = mySQLsql.replaceAll("(INSERT INTO `)[\\s\\S]*?\n", "");
		final Set<String> tables = Tool.getStrWithPattern("CREATE TABLE `[\\s\\S]*?(ENGINE=InnoDB DEFAULT CHARSET=utf8;)",
				mySQLsql);
		for (final String table : tables)
		{
			final Set<String> uniqueKeys = Tool.getStrWithPattern("(UNIQUE KEY `)[\\s\\S]*?\n", table);
			if (uniqueKeys.isEmpty())
			{
				continue;
			}
			String tableName = null;
			final Set<String> tableNames = Tool.getStrWithPattern("(CREATE TABLE `)[\\s\\S]*?\n", table);
			for (final String tableNameTemp : tableNames)
			{
				tableName = tableNameTemp.replaceAll("CREATE TABLE `", "").replaceAll("` \\(", "").replaceAll(" ", "")
						.replaceAll("\n", "").replaceAll("[\\t\\n\\r]", "");
			}
			if (tableName == null)
			{
				continue;
			}
			final List<String> uniqueKeyList = new ArrayList<String>();
			for (final String uniqueKey : uniqueKeys)
			{
				uniqueKeyList
						.add(uniqueKey.replaceAll("UNIQUE KEY ", "").replaceAll("\\),", ")").replaceAll(" ", Constant.SPLIT_SPARATOR)
								.replaceAll(",", " ASC,").replaceAll("\\)", " ASC)").replaceAll("`", "\"").replaceAll("[\\t\\n\\r]", ""));
			}
			keys.put(tableName, uniqueKeyList);
		}
		return keys;
	}
}
