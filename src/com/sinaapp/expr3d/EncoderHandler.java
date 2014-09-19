package com.sinaapp.expr3d;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



public class EncoderHandler {

	private static final String ALGORITHM = "MD5";

	private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	/**
	 * encode string
	 *
	 * @param algorithm
	 * @param str
	 * @return String
	 */
	public static String encode(String algorithm, String str) {
		if (str == null) {
			return null;
		}
		try {
			MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
			messageDigest.update(str.getBytes());
			return getFormattedText(messageDigest.digest());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 * encode By MD5
	 *
	 * @param str
	 * @return String
	 */
	public static String encodeByMD5(String str) {
		if (str == null) {
			return null;
		}
		try {
			MessageDigest messageDigest = MessageDigest.getInstance(ALGORITHM);
			messageDigest.update(str.getBytes());
			return getFormattedText(messageDigest.digest());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 * Takes the raw bytes from the digest and formats them correct.
	 *
	 * @param bytes
	 *            the raw bytes from the digest.
	 * @return the formatted bytes.
	 */
	private static String getFormattedText(byte[] bytes) {
		int len = bytes.length;
		StringBuilder buf = new StringBuilder(len * 2);
		// 把密文转换成十六进制的字符串形式
		for (int j = 0; j < len; j++) {
			buf.append(HEX_DIGITS[(bytes[j] >> 4) & 0x0f]);
			buf.append(HEX_DIGITS[bytes[j] & 0x0f]);
		}
		return buf.toString();
	}
	

	/**
	* Method to join array elements of type string
	* @author Hendrik Will, imwill.com
	* @param inputArray Array which contains strings
	* @param glueString String between each array element
	* @return String containing all array elements seperated by glue string
	*/
	public static String implodeArray(String[] inputArray, String glueString) {

	/** Output variable */
	String output = "";

	if (inputArray.length > 0) {
		StringBuilder sb = new StringBuilder();
		sb.append(inputArray[0]);

		for (int i=1; i<inputArray.length; i++) {
			sb.append(glueString);
			sb.append(inputArray[i]);
		}

		output = sb.toString();
	}

	return output;
	}

	public static String convertStreamToString(InputStream inputStream) {
        Scanner scanner = new Scanner(inputStream, "UTF-8");
        String text = scanner.useDelimiter("\\A").next();
        scanner.close();
        return text;
	}	
	
	public static WeChatMessage parseWeChatMsg(InputStream inputStream) throws Exception{
		WeChatMessage msg  = new WeChatMessage();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
		DocumentBuilder builder;  
		builder = factory.newDocumentBuilder();  
		Document document = builder.parse( inputStream );  
		{
			NodeList nodes = document.getElementsByTagName("ToUserName");
			Element line = (Element) nodes.item(0);  
			Node child = line.getFirstChild();
			if (child instanceof CharacterData) {
				CharacterData cd = (CharacterData) child;
				msg.toUserName =  cd.getData();
			}	
		}
		
		{
			NodeList nodes = document.getElementsByTagName("FromUserName");
			Element line = (Element) nodes.item(0);  
			Node child = line.getFirstChild();
			if (child instanceof CharacterData) {
				CharacterData cd = (CharacterData) child;
				msg.fromUserName =  cd.getData();
			}	
		}
		
		{
			NodeList nodes = document.getElementsByTagName("CreateTime");
			Element line = (Element) nodes.item(0);  
			Node child = line.getFirstChild();
			if (child instanceof CharacterData) {
				CharacterData cd = (CharacterData) child;
				msg.createTime =  cd.getData();
			}	
		}
		
		{
			NodeList nodes = document.getElementsByTagName("MsgType");
			Element line = (Element) nodes.item(0);  
			Node child = line.getFirstChild();
			if (child instanceof CharacterData) {
				CharacterData cd = (CharacterData) child;
				msg.msgType =  cd.getData();
			}	
		}
		
		
		{
			NodeList nodes = document.getElementsByTagName("Content");
			Element line = (Element) nodes.item(0);  
			Node child = line.getFirstChild();
			if (child instanceof CharacterData) {
				CharacterData cd = (CharacterData) child;
				msg.content =  cd.getData();
			}	
		}
		
		{
			NodeList nodes = document.getElementsByTagName("MsgId");
			Element line = (Element) nodes.item(0);  
			Node child = line.getFirstChild();
			if (child instanceof CharacterData) {
				CharacterData cd = (CharacterData) child;
				msg.msgId =  cd.getData();
			}	
		}
		
		
		return msg;
	}
	
	public static String constructWeChatMsg(WeChatMessage wcMsg, String content){

		StringBuilder sb = new StringBuilder();
		sb.append("<xml>");
		sb.append("<ToUserName><![CDATA[" + wcMsg.fromUserName + "]]></ToUserName>");
	//	sb.append("<ToUserName><![CDATA[oIHR-uAjQ9GVZ6GsXCkfCNPk-Gag]]></ToUserName>");	
		sb.append("<FromUserName><![CDATA[" + wcMsg.toUserName + "]]></FromUserName>");
		long longTime2 = new java.util.Date().getTime();
		sb.append("<CreateTime>" + String.valueOf(longTime2) + "</CreateTime>");
		sb.append("<MsgType><![CDATA[text]]></MsgType>");
		sb.append("<Content><![CDATA[" + content + "]]></Content>");
		sb.append("</xml>");
		
		return sb.toString();
		
	}
	
	public static String evaluateExpression(String expression, ServletContext sc) throws Exception{
		InputStream   inputStream   =   new   ByteArrayInputStream(expression.getBytes());
		ANTLRInputStream input = new ANTLRInputStream(inputStream);
        ExprLexer lexer = new ExprLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ExprParser parser = new ExprParser(tokens);
        ParseTree tree = parser.prog(); // parse
        EvalVisitor eval = new EvalVisitor();
      
        Library3D library = new Library3D(eval);
        eval.setLibrary(library);
        List<List<Integer>> arr = DBHelper.Load3DData();
        library.attach3DData(arr);
        String ret = "";
        try{
         	NodeValue v = eval.visit(tree);
         	ret = v.toString();
        }
        catch(AppException e){
        	ret = e.getMessage();
        }        	
        return ret;
	}
}

