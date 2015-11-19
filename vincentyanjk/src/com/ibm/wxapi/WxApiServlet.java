package com.ibm.wxapi;



import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;











import com.ibm.util.MsgType;
import com.ibm.util.MsgXmlUtil;
import com.ibm.util.PropertiesConfigUtil;
import com.ibm.wxapi.service.impl.MyserviceImpl;
import com.ibm.wxapi.vo.Article;
import com.ibm.wxapi.vo.MsgRequest;
import com.ibm.wxapi.vo.MsgResponseNews;
import com.ibm.wxapi.vo.MsgResponseText;


/**
 * 微信接口响应servlet
 */
public class WxApiServlet extends HttpServlet {
	Logger logger = Logger.getLogger(WxApiServlet.class.getName()); 
	private static final long serialVersionUID = 1L;
       
    public WxApiServlet() {
        super();
    }
    
    //url ，token 验证
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logger.info("##### valid url ");
		Properties prop = PropertiesConfigUtil.getProperties("account.properties");
	
		if(request.getParameter("timestamp") == null){//如果是空打印出首页
			response.getWriter().write("No information developing!");
		}else{
			String signature = request.getParameter("signature");// 微信加密签名
			String timestamp = request.getParameter("timestamp");// 时间戳
			String nonce = request.getParameter("nonce");// 随机数
			String echostr = request.getParameter("echostr");// 随机字符串
			
			// 校验成功返回  echostr，成功成为开发者；否则返回error，接入失败
			if (validSign(signature, prop.getProperty("token"), timestamp, nonce)) {
				response.getWriter().write(echostr);
			}
			response.getWriter().write("error");
		}
		
	}

	
	//微信服务器和开发者服务器消息交互
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logger.info("##### process msg  ");
		try {
			MsgRequest msgRequest = MsgXmlUtil.parseXml(request);//获取发送的消息
	        String msgtype = msgRequest.getMsgType();//接收到的消息类型
	        Properties prop = PropertiesConfigUtil.getProperties("account.properties");
	        String loger = null;
	        MyserviceImpl MyserviceImpl = new MyserviceImpl();
	        loger =  MyserviceImpl.processMsg(msgRequest);
			logger.info("##### response.write = " + loger);
			response.getWriter().write(new String(loger.getBytes("UTF-8"),"ISO8859-1"));
			
	/*		if(msgtype.equals(MsgType.Text.toString())){//订阅消息(subscribe)，回复一条图文消息
				MsgResponseText reponseText = new MsgResponseText();
				reponseText.setToUserName(msgRequest.getFromUserName());
				reponseText.setFromUserName(msgRequest.getToUserName());
				reponseText.setMsgType(MsgType.Text.toString());
				reponseText.setCreateTime(new Date().getTime());
				reponseText.setContent("欢迎您关注Vincent Yan，请回复 1 ");
				
				String str = MsgXmlUtil.textToXml(reponseText);
				logger.info("##### response.write = " + str);
				
				response.getWriter().write(new String(str.getBytes("UTF-8"),"ISO8859-1"));
			}else{//否则返回图文消息
				
				MsgResponseNews news = new MsgResponseNews();
				news.setToUserName(msgRequest.getFromUserName());
				news.setFromUserName(msgRequest.getToUserName());
				news.setMsgType("news");
				news.setCreateTime(new Date().getTime());
				
				List<Article> articles = new ArrayList<Article>();
				Article a1 = new Article();
				a1.setTitle("Vincent_familyan : 这里有你想要的");
				a1.setPicUrl("http://www.jeeweixin.com/res/upload/1426908565922.jpg");
				a1.setUrl("http://weixinpy.com/vp/jiaocheng/index/");
				articles.add(a1);
				
				Article a2 = new Article();
				a2.setTitle("加入Vincent_familyan，成就你的开发梦");
				a2.setPicUrl("http://www.jeeweixin.com/res/upload/1426908381642.jpg");
				a1.setUrl("http://www.weixinpy.com/wx/act/103/msgdetail/?mid=1367&mtype=news");
				articles.add(a2);
				
				news.setArticleCount(articles.size());
				news.setArticles(articles);
				
				String str = MsgXmlUtil.newsToXml(news);
				
				logger.info("##### response.write = " + str);
				response.getWriter().write(new String(str.getBytes("UTF-8"),"ISO8859-1"));
			}*/
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	//验证sign
	public static boolean validSign(String signature, String tocken, String timestamp, String nonce) {
		String[] paramArr = new String[] { tocken, timestamp, nonce };
		//对token、timestamp、nonce 进行字典排序，并拼接成字符串
		Arrays.sort(paramArr);
		StringBuilder sb = new StringBuilder(paramArr[0]);
		sb.append(paramArr[1]).append(paramArr[2]);
		String ciphertext = null;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			byte[] digest = md.digest(sb.toString().getBytes());// 对接后的字符串进行sha1加密
			ciphertext = byteToStr(digest);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		// 将sha1加密后的字符串与  signature 进行比较
		return ciphertext != null ? ciphertext.equals(signature.toUpperCase()) : false;
	}

	private static String byteToStr(byte[] byteArray) {
		String rst = "";
		for (int i = 0; i < byteArray.length; i++) {
			rst += byteToHex(byteArray[i]);
		}
		return rst;
	}
	
	private static String byteToHex(byte b) {
		char[] Digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		char[] tempArr = new char[2];
		tempArr[0] = Digit[(b >>> 4) & 0X0F];
		tempArr[1] = Digit[b & 0X0F];
		String s = new String(tempArr);
		return s;
	}

}


