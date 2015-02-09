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

		// ��d���ϴ���һ����Ϊtestfile���ı��ļ�
		File f = new File(Environment.getExternalStorageDirectory()
				+ File.separator + "data2.txt");
		// ��FileOutputSteam��װ�ļ����������ļ���׷��
		OutputStream out = new FileOutputStream(f, true);
		// �ַ�����
		// String[] str = {"shanghai","beijing","guangdong","xiamen"};

		out.write(str.getBytes()); // ���ļ���д������
		out.write('\r'); // \r\n��ʾ����
		out.write('\n');
		//
		out.close(); // �ر������
	}

	// ׷�ӷ�ʽ�����ļ�
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

	// �õ��ֻ���·��
	public String getSDPath()
	{
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED); // �ж�sd���Ƿ����
		if (sdCardExist)
		{
			sdDir = Environment.getExternalStorageDirectory();// ��ȡ��Ŀ¼
		}
		return sdDir.toString();
	}

}
