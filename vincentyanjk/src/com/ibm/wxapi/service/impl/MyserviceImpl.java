package com.ibm.wxapi.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import com.ibm.util.MsgType;
import com.ibm.util.MsgXmlUtil;
import com.ibm.util.PropertiesConfigUtil;
import com.ibm.wxapi.service.MyService;
import com.ibm.wxapi.vo.Article;
import com.ibm.wxapi.vo.MsgRequest;
import com.ibm.wxapi.vo.MsgResponseNews;
import com.ibm.wxapi.vo.MsgResponseText;
import org.apache.log4j.Logger;

public class MyserviceImpl implements MyService {

	@Override
	public String processMsg(MsgRequest msgRequest) {
        String msgtype = msgRequest.getMsgType();//接收到的消息类型
        String msgcontent = msgRequest.getContent();//接收到的消息类型
        Properties prop = PropertiesConfigUtil.getProperties("account.properties");
        
        if(msgtype.equals(MsgType.Event.toString())){
			/**
			 * 文本消息，一般公众号接收到的都是此类型消息
			 */
			MsgResponseText reponseText = new MsgResponseText();
			reponseText.setToUserName(msgRequest.getFromUserName());
			reponseText.setFromUserName(msgRequest.getToUserName());
			reponseText.setMsgType(MsgType.Text.toString());
			reponseText.setCreateTime(new Date().getTime());
			reponseText.setContent("欢迎您关注Vincent Yan，请回复 1, ");
			String str = null;
        	return  str = MsgXmlUtil.textToXml(reponseText);
   
		}else if(msgtype.equals(MsgType.Text.toString()) && "1".equals(msgcontent) ){//事件消息
			/**
			 * 用户订阅公众账号、点击菜单按钮的时候，会触发事件消息
			 */
			MsgResponseNews news = new MsgResponseNews();
			news.setToUserName(msgRequest.getFromUserName());
			news.setFromUserName(msgRequest.getToUserName());
			news.setMsgType("news");
			news.setCreateTime(new Date().getTime());
			
			List<Article> articles = new ArrayList<Article>();
			Article a1 = new Article();
			a1.setTitle("Vincent_familyan : CRM系统");
			a1.setPicUrl("http://www.jeeweixin.com/res/upload/1426908565922.jpg");
			a1.setUrl("http://www.baidu.com/");
			articles.add(a1);
			
			Article a2 = new Article();
			a2.setTitle("加入Vincent_familyan，HR系统");
			a2.setPicUrl("http://www.jeeweixin.com/res/upload/1426908381642.jpg");
			a2.setUrl("http://www.baidu.com/");
			articles.add(a2);
			
			Article a3 = new Article();
			a3.setTitle("加入Vincent_familyan，财富系统");
			a3.setPicUrl("http://www.jeeweixin.com/res/upload/1426908381642.jpg");
			a3.setUrl("http://www.baidu.com/");
			articles.add(a3);
			news.setArticleCount(articles.size());
			news.setArticles(articles);
			String str = null;
			return  str = MsgXmlUtil.newsToXml(news);
	
			
		//其他消息类型，开发者自行处理
		}else if(msgtype.equals(MsgType.Image.toString())){//图片消息
			
		}else if(msgtype.equals(MsgType.Location.toString())){//地理位置消息
			
		}
        
        
        
		return null;
	}

}
