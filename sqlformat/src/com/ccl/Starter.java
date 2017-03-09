package com.ccl;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@SuppressWarnings("javadoc")
public class Starter
{


	public static void main(final String[] args) throws Exception
	{

		// 1������HANA���ݿ�SQL-->��MYS�ѣ����ݿ�ģӣѣ��ʷŵ���Constant.IN_PUT_PATH����
		//createHanaSQL();
		// 2����֡�insert�����
		//splitDataSQL();
		// 3��������
		//createTable();
		// 4����������
		//createIndex();
		// 5����������
		//insertData();


		DBUtil.close();
	}

	public static void insertData() throws Exception
	{
		final File filePath = Tool.createFile(Constant.OUT_PUT_TEMP);
		final File[] dataFiles = filePath.listFiles();
		if (dataFiles.length > 0)
		{
			for (final File dataFile : dataFiles)
			{
				excuteSQL(Tool.fileContent(dataFile));
				Tool.deleteFile(dataFile);
			}
		}
		else
		{
			Tool.deleteFile(filePath);
		}

	}

	public static void createIndex() throws Exception
	{
		excuteSQL(Tool.fileContent(Tool.createFile(Constant.OUT_PUT_PATH, Constant.HANA_SQL_INDEX)));
	}

	public static void createTable() throws Exception
	{
		excuteSQL(Tool.fileContent(Tool.createFile(Constant.OUT_PUT_PATH, Constant.HANA_SQL_TABLE)));
	}

	public static void splitDataSQL()
	{
		final String sql = Tool.fileContent(Tool.createFile(Constant.OUT_PUT_PATH, Constant.HANA_SQL_DATA));
		final StringBuilder sqlTemp = new StringBuilder();
		int count = 0;
		final String[] sqlArr = sql.split(";");
		final StringBuilder fileName = new StringBuilder();
		boolean clearPath = true;
		for (int i = 0; i < sqlArr.length; i++)
		{
			count++;
			sqlTemp.append(sqlArr[i]).append(";\n");
			if (count == Constant.TEMP_SIZE || i == sqlArr.length - 1)
			{
				count = 0;
				Tool.outPutFile(sqlTemp.toString(), Constant.OUT_PUT_TEMP,
						fileName.append(Constant.CURRENT_TIME).append("_").append(i).append(".tmp").toString(), true, clearPath);
				clearPath = false;
				fileName.delete(0, fileName.length());
				sqlTemp.delete(0, sqlTemp.length());
			}
		}
	}

	public static void createHanaSQL()
	{
		SQLUtil.createTableSQL();
		SQLUtil.createIndexSQL();
		SQLUtil.createDataSQL();
	}

	public static void excuteSQL(final String sql) throws Exception
	{
		try
		{
			DBUtil.excute(new DBUtil.Excute()
			{
				@Override
				public void formatSQL(final List<Map<String, Object[]>> sqls)
				{
					final boolean combin = sql.contains(");");
					for (final String sql : combin ? sql.split("\\);") : sql.split(";"))
					{
						final Map<String, Object[]> sqlMap = new HashMap<String, Object[]>();
						sqlMap.put(sql + (combin ? ")" : ""), null);
						sqls.add(sqlMap);
					}
				}
			});
		}
		catch (final Exception e)
		{
			if (!Tool.stopThrow(e))
			{
				throw e;
			}
		}
	}



}
