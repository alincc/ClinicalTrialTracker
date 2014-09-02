package com.yuanwei.android.rss.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//import java.util.Vector;

public class RSSFeed {
	private int itemcount = 0;// �����б���Ŀ
	private List<Article> itemList;// ����һ��RSSItem���͵ļ��϶���itemList����������item�б�
	private RssChannel rssChannel;
	
	public RSSFeed() {
		//itemList = new Vector<Article>(0);// ���캯����ʼ�� itemList
		 itemList=new ArrayList<Article>();
		 rssChannel=new RssChannel();
	}
	//private Map<String,String> mMap =new HashMap<String,String>();


	/**
	 * �÷�������һ��RSSItem���뵽RSSFeed����
	 * 
	 * @param item
	 * @return
	 */
	public int addItem(Article article) {
		
		itemList.add(article);
		itemcount++;
		System.out.println("Total"+itemcount);
		return itemcount;
		
	}

	/*public int addChannel(RSSChannel rssChannel)
	{
		rssChannels.add(rssChannel);
		itemcount++;
		return itemcount;
	}	
*/	

/**
	 * �÷�����������activity�и���������ȡ�����item�������ģ���activity����ת�л��õ�
	 * 
	 * @param location
	 * @return
	 */
	public Article getItem(int location) {
		return itemList.get(location);
	}
	public RssChannel getChannel(){
		return this.rssChannel;
	}
	/**
	 * ����ListView����Ҫ���б�����
	 * @return
	 */
	public List<Map<String, Article>> getAllItemsForListView()
	{
	
		List<Map<String,Article>> dataList=new ArrayList<Map<String,Article>>();
		for (int i = 0; i < itemList.size(); i++) {
			Map<String,Article> item=new HashMap<String, Article>();
			item.put("item", itemList.get(i));
			dataList.add(item);
		}
		return dataList;
	}
	public int getItemcount() {
		return itemcount;
	}

	public void setItemcount(int itemcount) {
		this.itemcount = itemcount;
	}

	public List<Article> getItemList() {
		return itemList;
	}

	public void setItemList(List<Article> itemList) {
		this.itemList = itemList;
	}
	public void setRssChannel(RssChannel rssChannel){
		this.rssChannel=rssChannel;
	}
	
}

