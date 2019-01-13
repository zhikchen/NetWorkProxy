package timeutils;

import IPModel.DatabaseMessage;
import IPModel.IPMessage;
import database.DataBaseDemo;
import htmlparse.URLFecter;
import ipfilter.IPFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.out;

/**
 * Created by paranoid on 17-4-13.
 */

public class MyTimeJob  {
    public void execute() throws Exception {
        List<String> Urls = new ArrayList<String>();
        List<DatabaseMessage> databaseMessages = new ArrayList<DatabaseMessage>();
        List<IPMessage> list = new ArrayList<IPMessage>();
        List<IPMessage> ipMessages = new ArrayList<IPMessage>();
        String url = "http://www.xicidaili.com/nn";
        String IPAddress;
        String IPPort;
        int k, j;

        //首先使用本机ip进行爬取
        try {
            list = URLFecter.urlParse(url, list);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        //对ip进行测试，不可用的从数组中删除
        //list = IPUtils.IPIsable(list);
        
        if(list.isEmpty()) {
        	//如果第一页没有抓到相对应的代理ip的时候，可以再利用本机地址再抓一页。
        }
        
        
        //对得到的IP进行筛选，选取链接速度前100名的
        list = IPFilter.Filter(list);

        
        //构造种子Url
        for (int i = 2; i <= 6; i++) {
            Urls.add("http://www.xicidaili.com/nn/" + i);
        }

      
        //得到所需要的数据
        for (k = 0, j = 0; j < Urls.size(); k++) {
            url = Urls.get(j);

            IPAddress = list.get(k).getIPAddress();
            IPPort = list.get(k).getIPPort();
            //每次爬取前的大小
            int preIPMessSize = ipMessages.size();
            try {
                ipMessages = URLFecter.urlParse(url, IPAddress, IPPort, ipMessages);
                //每次爬取后的大小
                //每次爬取完后，理应有数据，比原来的数据更多。但是如果，出现异常，可能导致并没有爬到，因此要换一个ip重新爬取。
                int lastIPMessSize = ipMessages.size();
                if(preIPMessSize != lastIPMessSize){
                    j++;
                }

                //对IP进行轮寻调用
                if (k >= list.size()) {
                    k = 0;
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        //对ip进行测试，不可用的从数组中删除
        ipMessages = IPUtils.IPIsable(ipMessages);
        
        //对得到的IP进行筛选，选取链接速度前100名的
        ipMessages = IPFilter.Filter(ipMessages);


        for(IPMessage ipMessage : ipMessages){
            out.println(ipMessage.getIPAddress());
            out.println(ipMessage.getIPPort());
            out.println(ipMessage.getServerAddress());
            out.println(ipMessage.getIPType());
            out.println(ipMessage.getIPSpeed());
        }

        //将得到的IP存储在数据库中(每次先清空数据库)
        try {
            DataBaseDemo.delete();
            DataBaseDemo.add(ipMessages);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        //从数据库中将IP取到
        try {
            databaseMessages = DataBaseDemo.query();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        for (DatabaseMessage databaseMessage: databaseMessages) {
            out.println(databaseMessage.getId());
            out.println(databaseMessage.getIPAddress());
            out.println(databaseMessage.getIPPort());
            out.println(databaseMessage.getServerAddress());
            out.println(databaseMessage.getIPType());
            out.println(databaseMessage.getIPSpeed());
        }
    }
    
    
    public static void main(String[] args) {
		try {
			while(true) {
				new MyTimeJob().execute();
				Thread.sleep(20000);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
