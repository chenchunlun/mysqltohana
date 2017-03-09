package com.ccl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * ������
 */
public class Tool
{
	/**
	 * �쳣�Ƿ�ֹͣ����.
	 *
	 * @param e
	 * @return true false
	 */
	public static boolean stopThrow(final Exception e)
	{
		final String[] msgs =
		{ "unique constraint violated" };
		boolean rs = false;
		for (final String msg : msgs)
		{
			rs = e.getMessage().contains(msg);
			if (rs)
			{
				break;
			}
		}
		return rs;
	}

	/**
	 * ��ȡ����Ŀ¼�µ��ı��ļ�����.
	 *
	 * @return �ļ��ı�����
	 */
	public static String fileContent()
	{
		final StringBuilder sb = new StringBuilder();
		final File path = createFile(Constant.IN_PUT_PATH);
		if (path.exists() && path.isDirectory())
		{
			for (final File file : path.listFiles())
			{
				sb.append(fileContent(file));
			}
		}
		return sb.toString();
	}

	/**
	 * ��ȡ�ļ����ı�����.
	 *
	 * @param file
	 *           �ļ�
	 * @return �ļ��ı�����
	 */
	public static String fileContent(final File file)
	{
		final StringBuilder sb = new StringBuilder();

		if (!file.isDirectory())
		{
			try
			{
				final FileInputStream fin = new FileInputStream(file);
				int len = 1024;
				final byte[] bytes = new byte[1024];
				while ((len = fin.read(bytes, 0, len)) != -1)
				{
					sb.append(new String(bytes, 0, len, Constant.ENCODE_FORMAT));
				}
				fin.close();
			}
			catch (final Exception e)
			{
				e.printStackTrace();
			}
		}

		return sb.toString();
	}

	/**
	 * �����ļ���Ŀ¼.
	 *
	 * @param fileName
	 *           �ļ���Ŀ¼ȫ��
	 * @return �ļ���Ŀ¼����
	 */
	public static File createFile(final String fileName)
	{
		return new File(fileName);
	}

	/**
	 * �����ļ�.
	 *
	 * @param path
	 *           �ļ�·��
	 * @param fileName
	 *           �ļ���
	 * @return �ļ�
	 */
	public static File createFile(final String path, final String fileName)
	{
		return new File(new StringBuilder().append(path).append(File.separator).append(fileName).toString());
	}

	/**
	 * �ַ���д��ָ���ļ�.
	 *
	 * @param str
	 *           ��д�ַ���
	 * @param fileName
	 *           �ļ�����
	 * @param append
	 *           �Ƿ�׷��
	 */
	public static void outPutFile(final String str, final String fileName, final boolean append)
	{
		outPutFile(str, Constant.OUT_PUT_PATH, fileName, append);
	}

	/**
	 * �ַ���д��ָ���ļ�.
	 *
	 * @param str
	 *           ��д�ַ���
	 * @param outPath
	 *           �ļ�·��
	 * @param fileName
	 *           �ļ�����
	 * @param append
	 *           �Ƿ�׷��
	 */
	public static void outPutFile(final String str, final String outPath, final String fileName, final boolean append)
	{
		Tool.outPutFile(str, outPath, fileName, append, false);
	}

	/**
	 * �ַ���д��ָ���ļ�.
	 *
	 * @param str
	 *           ��д�ַ���
	 * @param outPath
	 *           �ļ�·��
	 * @param fileName
	 *           �ļ�����
	 * @param append
	 *           �Ƿ�׷��
	 * @param clearPath
	 *           �Ƿ����Ŀ¼
	 */
	public static void outPutFile(final String str, final String outPath, final String fileName, final boolean append,
			final boolean clearPath)
	{
		try
		{
			final File path = createFile(outPath);
			if (clearPath && path.exists())
			{
				deleteFile(path);
			}
			if (!path.exists())
			{
				path.mkdirs();
			}
			final File file = createFile(outPath, fileName);
			if (!append && file.exists())
			{
				file.delete();
			}
			if (!file.exists())
			{
				file.createNewFile();
			}

			final byte[] bytes = str.getBytes(Constant.ENCODE_FORMAT);
			final FileOutputStream outStream = new FileOutputStream(file, append);
			outStream.write(bytes);
			outStream.close();
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
	}


	/**
	 * �ַ���д��ָ���ļ�.
	 *
	 * @param str
	 *           ��д�ַ���
	 * @param fileName
	 *           �Ƿ�׷��
	 */
	public static void outPutFile(final String str, final String fileName)
	{
		outPutFile(str, fileName, false);
	}

	/**
	 * �ַ���д��ָ���ļ�.
	 *
	 * @param str
	 *           ��д�ַ���
	 * @param outPath
	 *           �ļ�·��
	 * @param fileName
	 *           �ļ�����
	 */
	public static void outPutFile(final String str, final String outPath, final String fileName)
	{
		outPutFile(str, outPath, fileName, false);
	}


	/**
	 * ɾ���ļ�.
	 *
	 * @param file
	 */
	public static void deleteFile(final File file)
	{
		if (file.exists())
		{
			if (file.isDirectory())
			{
				for (final File f : file.listFiles())
				{
					deleteFile(f);
				}
			}
			else
			{
				file.delete();
			}
		}
	}


	/**
	 * ��ȡƥ���ַ���.
	 *
	 * @param pattern
	 *           ������ʽ
	 * @param str
	 *           Ŀ���ַ���
	 * @return ƥ������
	 */
	public static Set<String> getStrWithPattern(final String pattern, final String str)
	{
		final Pattern p = Pattern.compile(pattern);
		final Matcher m = p.matcher(str);
		final Set<String> strList = new HashSet<String>();
		while (m.find())
		{
			strList.add(m.group(0));
		}
		return strList;
	}

	/**
	 * ��ʽ������
	 *
	 * @param date
	 *           ���ڶ���
	 * @return �����ַ���
	 */
	public static String formatDate(final Date date)
	{
		String dateStr = null;
		dateStr = new SimpleDateFormat(Constant.DATE_FORMAT).format(date);
		return dateStr;
	}

	/**
	 * ��ʽ����ǰ����
	 *
	 * @return �����ַ���
	 */
	public static String formatDate()
	{
		return formatDate(new Date());
	}


}
