package com.ccl;

import java.io.File;


/**
 * ����.
 */
public class Constant
{
	/**
	 * JDK URL
	 */
	public static final String URL = "jdbc:sap://192.168.1.176:39013/SYSTEMDB/HXE";

	/**
	 * ���ݿ��û���
	 */
	public static final String USER = "SYSTEM";
	/**
	 * ���ݿ�����
	 */
	public static final String PWD = "123456";

	/**
	 * SQL��������
	 */
	public static final long BATCH_SIZE = 1000;

	/**
	 * temp�ļ�size.
	 */
	public static final int TEMP_SIZE = 2000;


	/**
	 * ���ڸ�ʽ��.
	 */
	public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	/**
	 * ��ǰʱ��
	 */
	public static final long CURRENT_TIME = System.currentTimeMillis();


	/**
	 * ������־�ļ�.
	 */
	public static final String ERROR_LOG = "error.log";

	/**
	 * �ַ����ָ���.
	 */
	public static final String SPLIT_SPARATOR = "&&&";

	/**
	 * �����ʽ
	 */
	public static final String ENCODE_FORMAT = "UTF-8";
	/**
	 * ���� �ļ�·��
	 */
	public static final String IN_PUT_PATH = "C:/mysql";
	/**
	 * ��� �ļ�·��
	 */
	public static final String OUT_PUT_PATH = new StringBuilder().append(IN_PUT_PATH).append(File.separator).append("sql")
			.toString();
	/**
	 * ��� Temp
	 */
	public static final String OUT_PUT_TEMP = new StringBuilder().append(IN_PUT_PATH).append(File.separator).append("temp")
			.toString();
	/**
	 * HANA SQL(����)
	 */
	public static final String HANA_SQL_TABLE = "hana_table.sql";

	/**
	 * HANA SQL(������)
	 */
	public static final String HANA_SQL_INDEX = "hana_index.sql";

	/**
	 * HANA SQL(����)
	 */
	public static final String HANA_SQL_DATA = "hana_data.sql";

	/**
	 * HANA JDK ����
	 */
	public static final String DRIVER = "com.sap.db.jdbc.Driver";

}
