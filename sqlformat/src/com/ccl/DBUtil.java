package com.ccl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 用于操作数据库
 */
public class DBUtil
{

	/**
	 *
	 */
	public interface Excute
	{
		/**
		 *
		 * @param sqls
		 */
		void formatSQL(List<Map<String, Object[]>> sqls);
	}

	private static Connection con = null;



	/**
	 * 获取数据库连接
	 *
	 * @return
	 * @throws Exception
	 */
	private static Connection con() throws SQLException
	{
		if (con == null)
		{
			try
			{
				Class.forName(Constant.DRIVER);
				con = DriverManager.getConnection(Constant.URL, Constant.USER, Constant.PWD);
			}
			catch (final ClassNotFoundException e)
			{
				e.printStackTrace();
			}
		}

		return con;

	}

	/**
	 * 增、删、改.
	 *
	 * @param excute
	 * @throws Exception
	 */
	public static void excute(final Excute excute) throws Exception
	{
		final List<Map<String, Object[]>> sqls = new ArrayList<Map<String, Object[]>>();
		excute.formatSQL(sqls);

		final Connection con = con();
		final Statement statement = con.createStatement();
		if (statement == null || con == null)
		{
			return;
		}
		int batchSize = 0;
		for (final Map<String, Object[]> sqlMap : sqls)
		{
			for (final String sql : sqlMap.keySet())
			{
				{
					if (!(sql.toLowerCase().contains("insert") || sql.toLowerCase().contains("update")
							|| sql.toLowerCase().contains("delete") || sql.toLowerCase().contains("create")))
					{
						continue;
					}


					batchSize++;
					statement.addBatch(sql);
					if (batchSize == Constant.BATCH_SIZE)
					{
						statement.executeBatch();
						batchSize = 0;
					}
				}
			}
		}
		statement.executeBatch();
		statement.close();

	}

	/**
	 *
	 */
	public static void close()
	{
		if (con != null)
		{
			try
			{
				con.close();
			}
			catch (final SQLException e)
			{
				e.printStackTrace();
			}
		}
	}

}
