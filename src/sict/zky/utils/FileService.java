package sict.zky.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;

import android.content.Context;
import android.os.Environment;

public class FileService
{
	private Context context;

	public FileService()
	{

	}

	public FileService(Context context)
	{
		super();
		this.context = context;
	}

	public void saveToSDCard(String filename, String content) throws Exception
	{
		File file = new File(Environment.getExternalStorageDirectory(),
				filename);
		FileOutputStream outStream = new FileOutputStream(file);
		outStream.write(content.getBytes());
		outStream.close();

	}

	public void TestFile(String str) throws Exception
	{

		// 在d盘上创建一个名为testfile的文本文件
		File f = new File(Environment.getExternalStorageDirectory()
				+ File.separator + "data2.txt");
		// 用FileOutputSteam包装文件，并设置文件可追加
		OutputStream out = new FileOutputStream(f, true);
		// 字符数组
		// String[] str = {"shanghai","beijing","guangdong","xiamen"};

		out.write(str.getBytes()); // 向文件中写入数据
		out.write('\r'); // \r\n表示换行
		out.write('\n');
		//
		out.close(); // 关闭输出流
	}

	// 追加方式保存文件
	public void saveFileAppend(String file, String conent)
	{
		BufferedWriter out = null;
		try
		{
			out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file, true)));
			out.write(conent);
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				out.close();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	// 得到手机根路径
	public String getSDPath()
	{
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		if (sdCardExist)
		{
			sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
		}
		return sdDir.toString();
	}

}
