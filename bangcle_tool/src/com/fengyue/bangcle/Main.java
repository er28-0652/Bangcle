package com.fengyue.bangcle;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Iterator;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 * 
 * @author Administrator
 *
 */
public class Main {
	private static final String DEX_APP_NAME = "com.storm.fengyue.StubApplication";
	private static  final String AES_KEY = "ghidraisthebest!";
	private static final SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyyMMddHHmmss");
	private static Config config;
	private static String soName="libdexload.so";
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) {
		System.out.println("----------Bangcle Automation----------------");
		System.out.println(System.getProperty("user.dir"));
		String cmd = args[0];
		if (!"b".equals(cmd)) {
			System.out.println("usage:java -jar Bangcle.jar b apkName");
			return;
		}
		
//		String apkName="msgnow-release.apk";
//		String apkName="unpack_permmgr.apk";
//		String apkName="com.aispeech.weiyu_2.apk";
		String apkPath = new File(args[1]).getAbsolutePath();
		String apkName = new File(apkPath).getName();
		System.out.println("apkPath: " + apkPath);
		System.out.println("apkName: " + apkName);
		
		
		// 反编译目录
		String workPath = getWorkPath();
		System.out.println("workPath: " + workPath);
		String toolsPath = workPath + File.separator + "tools";
		// int pos = apkName.lastIndexOf(".");
		// String decompiledDirName = apkName.substring(0, pos);
		String decompiledDirName = apkName.substring(0, apkName.lastIndexOf("."));
		System.out.println("apkPath: " + apkPath + " decompiledDirName: " + decompiledDirName);
		
		
		// 删除反编译目录
		File outputFolder = new File(workPath + File.separator + "output");
		if(!outputFolder.exists()){
			outputFolder.mkdir();
			System.out.println("Create build directory:" + outputFolder.getAbsolutePath());
		}

		File decompiledFile = new File(outputFolder.getAbsolutePath() + File.separator + decompiledDirName);
		String decompiledPath = decompiledFile.getAbsolutePath();
		if (decompiledFile.exists()) {
			FileUtil.delete(decompiledFile);
			System.out.println("deleted" + decompiledFile.getAbsolutePath());
		}
		
		// 创建反编译目录
		boolean decompiled = false;

		try {
			long startTime = System.currentTimeMillis();
			System.out.println("Decompiling " + apkPath);

			// 确保apktool.jar放在工作目录下
			SystemCommand.execute("java -jar " + toolsPath + "/apktool.jar d " + apkPath + " -o " + decompiledFile.getAbsolutePath()+" -s -f");
			System.out.println("End of decompilation, generate directory" + decompiledFile.getAbsolutePath());
			
			decompiled = true;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//extract classes.dex
		String assetDir = decompiledPath + File.separator + "assets";
		File file_asset = new File(assetDir);
		if(!file_asset.exists()){
			file_asset.mkdirs();
		}

		String libDir = decompiledPath + File.separator + "lib";
		File file_lib = new File(libDir);
		if(!file_lib.exists()){
			file_lib.mkdirs();
		}

		String abiDir = decompiledPath + File.separator + "lib" + File.separator + "armeabi-v7a";
		File file_abi = new File(abiDir);
		if(!file_abi.exists()){
			file_abi.mkdirs();
		}

		String rawdex = decompiledPath + File.separator + "classes.dex";
		
		//encrypt raw dex
		byte[] data = FileUtil.getFileByte(rawdex);
		byte[] encrypt_data = AESUtil.encrypt(data, AES_KEY);
		System.out.println("AES encrypt classes.dex finished");
		FileUtil.byteToFile(encrypt_data, assetDir, "jiami.dat");
		
		System.out.println("copy jiami.dat to assets dir finished");
		//delete orig raw dex
		FileUtil.delete(new File(rawdex));
		
		try {
			//将libdexload.so 复制到 assets目录下
			FileUtil.copyFile(toolsPath + File.separator + soName, abiDir + File.separator + soName);
			//FileUtil.copyFile(toolsPath + File.separator + soName + "_a64.so", assetDir + File.separator + soName + "_a64.so");
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		try {
			FileUtil.copyDir(toolsPath + File.separator + "smali", decompiledPath + File.separator + "smali");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		if (decompiled) {
			if (alterAndroidMainifest(decompiledFile.getAbsolutePath())) {
				try {
					String apkNewName = decompiledDirName + "_unsigned.apk";
					String apkNewSignName = decompiledDirName + "_signed.apk";
					String apkNewAlignName = decompiledDirName + "_align.apk";
					String outputPath = workPath + File.separator + "output" + File.separator + apkNewName;
					String outputAlignPath = workPath + File.separator + "output" + File.separator + apkNewAlignName;
					String outputSignPath = workPath + File.separator + "output" + File.separator + apkNewSignName;
					SystemCommand.execute("java -jar " + toolsPath + "/apktool.jar b " + decompiledPath + " -o " + outputPath);
					System.out.println("Compile completed");
					System.out.println("running zipalign");
					zipalign(outputPath, outputAlignPath);
					System.out.println("align completed");
					System.out.println("Signing Apk");
					signApk_x509(toolsPath, outputAlignPath, outputSignPath);
					System.out.println("Re-signature completed");
					System.out.println("Reinforced Apk Catalog:"+outputSignPath);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else {
			System.out.println("Decompilation failed");
		}
	}

	private static void zipalign(String unalignedApkPath, String alignedApkPath){
		try {
			String command = "zipalign  -f -v 4 " + unalignedApkPath + " "  + alignedApkPath;
			
			SystemCommand.execute(command);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
	}

	private static void signApk_x509(String toolsPath, String unsignedApkPath, String signedApkPath){
		try {
			String command = "java -jar " + toolsPath + "/apksigner.jar sign --cert " + toolsPath + "/testkey.x509.pem  --key " + toolsPath + "/testkey.pk8 --in "+ unsignedApkPath + "  --out " + signedApkPath;
			
			SystemCommand.execute(command);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
	}

	/**
	 * 修改AndroidMinifest.xml中的Application Class为脱壳的Application Class名
	 * 在Application标签中增加原Application Class名
	 * 
	 * @param workPath
	 */
	private static boolean alterAndroidMainifest(String workPath) {
		// TODO Auto-generated method stub
		String manifestFileName = "AndroidManifest.xml";
		File manifestFile = new File(workPath + File.separator + manifestFileName);
		if (!manifestFile.exists()) {
			System.err.println("Can't find " + manifestFile.getAbsolutePath());
			return false;
		}
		SAXReader reader = new SAXReader();
		reader.setEncoding("UTF-8");
		try {
			Document document = reader.read(manifestFile);
			Element root = document.getRootElement();

			System.out.println("Current package name:" + root.attribute("package").getText());
			Element applicationEle = root.element("application");
			System.out.println("Traverse the properties of the application tag:");
			Iterator<Attribute> attrIterator = applicationEle.attributeIterator();
			String APP_NAME = null;
			boolean find_application=false;
			while (attrIterator.hasNext()) {
				Attribute attr = attrIterator.next();
				System.out.println(attr.getNamespacePrefix() + ":"
						+ attr.getName() + " = " + attr.getValue());
				//寻找android:name标签
				if ("android".equals(attr.getNamespacePrefix())
						&& "name".equals(attr.getName())) {
					APP_NAME = attr.getValue();
					attr.setValue(DEX_APP_NAME);
					find_application = true;
					System.out.println("orig application name:" + APP_NAME);
					System.out.println("new application name:" + attr.getValue());
					break;
				}
			}
			//如果apk没有原始的application
			if(!find_application){
				System.out.println("no orig application");
				applicationEle.addAttribute("android:name",DEX_APP_NAME);
			}
			//保存原始的application
			else{
				Element mataDataEle = applicationEle.addElement("meta-data");
				mataDataEle.addAttribute("android:name", "APP_NAME");
				mataDataEle.addAttribute("android:value", APP_NAME);
			}
			

			manifestFile.delete();
			
			//处理中文字符的乱码
			//参考：https://blog.csdn.net/zhengdesheng19930211/article/details/64443572
//			java.io.Writer wr=new java.io.OutputStreamWriter(new java.io.FileOutputStream(manifestFile.getAbsolutePath()),"UTF-8");
//			document.write(wr);
//			wr.close();
			
			//下列方式不能处理中文字符 ，虽然这里设置了编码 但是保存的文件还是ANSI格式
//			OutputFormat format = OutputFormat.createPrettyPrint();
//			format.setEncoding("UTF-8");// 设置编码
//			Writer filewriter = new FileWriter(manifestFile.getAbsolutePath());
//			System.out.println("manifest path:"+manifestFile.getAbsolutePath());
//			XMLWriter xmlwriter = new XMLWriter(filewriter, format);
//			xmlwriter.write(document);
//			xmlwriter.close();
//			System.out.println("修改Manifest成功");
			
			//处理中文乱码
			//参考:https://blog.csdn.net/zl594389970/article/details/53353813
			System.out.println("Use method 3 to write xml");
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("UTF-8");// 设置编码
			OutputStream out =  new FileOutputStream(manifestFile.getAbsolutePath());
			System.out.println("manifest path:"+manifestFile.getAbsolutePath());
			XMLWriter xmlwriter = new XMLWriter(out, format);
			xmlwriter.write(document);
			xmlwriter.close();
			
			
			System.out.println("Successful modification of Manifest");
			return true;
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

	private static Config getConfig() {
		if (config != null) {
			return config;
		}

		File signerConfigFile = new File(getWorkPath() + "\\" + "config.xml");
		if (!signerConfigFile.exists()) {
			System.err.println("Can't find" + signerConfigFile.getAbsolutePath());
			return null;
		}
		// 读取XML
		SAXReader reader = new SAXReader();
		try {
			Document document = reader.read(signerConfigFile);
			Element root = document.getRootElement();
			Element signaturePathEle = root.element("signature-path");
			String signaturePath = signaturePathEle.getText();

			Element storePwdEle = root.element("store-pwd");
			String storePwd = storePwdEle.getText();

			Element aliasEle = root.element("alias");
			String alias = aliasEle.getText();

			Element aliasPwdEle = root.element("alias-pwd");
			String aliasPwd = aliasPwdEle.getText();

			System.out.println("signature-path:" + signaturePath
					+ " store-pwd:" + storePwd + " alias:" + alias
					+ " aliasPwd:" + aliasPwd );
			config = new Config();
			config.signaturePath = signaturePath;
			config.storePwd = storePwd;
			config.alias = alias;
			config.aliasPwd = aliasPwd;
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return config;
	}

	private static String getWorkPath() {
		return new File(ClassLoader.getSystemClassLoader().getResource(".").getPath()).getAbsolutePath();
	}

	static class Config {
		public String signaturePath;
		public String storePwd;
		public String alias;
		public String aliasPwd;
		public String winRARPath;
	}
}
