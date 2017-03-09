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
 * 工具类
 */
public class Tool
{
	/**
	 * 异常是否停止外抛.
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
	 * 获取配置目录下的文本文件内容.
	 *
	 * @return 文件文本内容
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
	 * 读取文件的文本内容.
	 *
	 * @param file
	 *           文件
	 * @return 文件文本内容
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
	 * 创建文件或目录.
	 *
	 * @param fileName
	 *           文件或目录全名
	 * @return 文件或目录对象
	 */
	public static File createFile(final String fileName)
	{
		return new File(fileName);
	}

	/**
	 * 创建文件.
	 *
	 * @param path
	 *           文件路径
	 * @param fileName
	 *           文件名
	 * @return 文件
	 */
	public static File createFile(final String path, final String fileName)
	{
		return new File(new StringBuilder().append(path).append(File.separator).append(fileName).toString());
	}

	/**
	 * 字符串写入指定文件.
	 *
	 * @param str
	 *           待写字符串
	 * @param fileName
	 *           文件名称
	 * @param append
	 *           是否追加
	 */
	public static void outPutFile(final String str, final String fileName, final boolean append)
	{
		outPutFile(str, Constant.OUT_PUT_PATH, fileName, append);
	}

	/**
	 * 字符串写入指定文件.
	 *
	 * @param str
	 *           待写字符串
	 * @param outPath
	 *           文件路径
	 * @param fileName
	 *           文件名称
	 * @param append
	 *           是否追加
	 */
	public static void outPutFile(final String str, final String outPath, final String fileName, final boolean append)
	{
		Tool.outPutFile(str, outPath, fileName, append, false);
	}

	/**
	 * 字符串写入指定文件.
	 *
	 * @param str
	 *           待写字符串
	 * @param outPath
	 *           文件路径
	 * @param fileName
	 *           文件名称
	 * @param append
	 *           是否追加
	 * @param clearPath
	 *           是否清空目录
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
	 * 字符串写入指定文件.
	 *
	 * @param str
	 *           待写字符串
	 * @param fileName
	 *           是否追加
	 */
	public static void outPutFile(final String str, final String fileName)
	{
		outPutFile(str, fileName, false);
	}

	/**
	 * 字符串写入指定文件.
	 *
	 * @param str
	 *           待写字符串
	 * @param outPath
	 *           文件路径
	 * @param fileName
	 *           文件名称
	 */
	public static void outPutFile(final String str, final String outPath, final String fileName)
	{
		outPutFile(str, outPath, fileName, false);
	}


	/**
	 * 删除文件.
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
	 * 获取匹配字符串.
	 *
	 * @param pattern
	 *           正则表达式
	 * @param str
	 *           目标字符串
	 * @return 匹配结果集
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
	 * 格式化日期
	 *
	 * @param date
	 *           日期对象
	 * @return 日期字符串
	 */
	public static String formatDate(final Date date)
	{
		String dateStr = null;
		dateStr = new SimpleDateFormat(Constant.DATE_FORMAT).format(date);
		return dateStr;
	}

	/**
	 * 格式化当前日期
	 *
	 * @return 日期字符串
	 */
	public static String formatDate()
	{
		return formatDate(new Date());
	}


}
