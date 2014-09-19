package com.sinaapp.expr3d;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import javax.servlet.ServletContext;  
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.logging.log4j.Logger;  
import org.apache.logging.log4j.LogManager;


public class Parser extends HttpServlet {
//	static Logger logger = LogManager.getLogger();
	protected final static Logger logger = LogManager.getLogger();
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		//super.doGet(req, resp);
		
		String signature 	= 	req.getParameter("signature");
		String timestamp	= 	req.getParameter("timestamp");
		String nonce		=	req.getParameter("nonce");
		String echostr		=	req.getParameter("echostr");
		String token		=	"MyExprParser";
		String strArray[] = {token, timestamp, nonce};
		Arrays.sort(strArray);
		String tmpstr		=	EncoderHandler.implodeArray(strArray,"");
		String output		=	EncoderHandler.encode("SHA1", tmpstr);
		
		if(signature.equalsIgnoreCase(output)){		
			PrintWriter out = resp.getWriter();
			out.print(echostr);
		}

	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
        logger.trace("doPost begins");
		InputStream xmlIs = req.getInputStream();
		String content = "";
//		String xml = EncoderHandler.convertStreamToString(xmlIs);
		try  
		{  
			WeChatMessage wcMsg = EncoderHandler.parseWeChatMsg(xmlIs);
			content = wcMsg.content;  
			logger.debug(content);
			ServletContext sc = super.getServletContext();
			  
			String value = EncoderHandler.evaluateExpression(wcMsg.content, sc);
			String resXml = EncoderHandler.constructWeChatMsg(wcMsg, value);
			resp.setContentType("text/xml;charset=UTF-8");    
			resp.setStatus(HttpServletResponse.SC_OK);
			resp.getWriter().write(resXml);
			resp.getWriter().flush();
			resp.getWriter().close();		      
			logger.debug(resXml);
		} catch (Exception e) {  
			logger.error(e);
		}		  
		logger.trace("doPost ends.");		  
	}
	
	
}
