package One;
import java.util.Map;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.JsonFilePipeline;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.processor.PageProcessor;
public class WebSpider implements PageProcessor{
	private Site site=Site.me().setRetryTimes(3).setSleepTime(100);
	
	
	@Override
	public void process(Page page) {
		// TODO Auto-generated method stub
		//���Ŀ������ҳ�棬������δ�����õ���������ʽ������ʾƥ������"https://github.com/code4craft/webmagic"���������ӡ�
		page.addTargetRequests(page.getHtml().links().regex("(https://github\\.com/\\w+/\\w+)").all());
		//��ȡ����Ϣ--author���ת��ΪString�������ݣ�ע�⣺putField�����ݿ��Ը�������ı䣡��
		page.putField("author",page.getUrl().regex("https://github\\.com/(\\w+)/.*").toString());
		//������δ���ʹ����XPath��������˼�ǡ���������class����Ϊ'entry-title public'��h1Ԫ�أ����ҵ�����strong�ӽڵ��a�ӽڵ㣬����ȡa�ڵ���ı���Ϣ����
		page.putField("name",page.getUrl().xpath("//h1[@class='entry-title public']/strong/a/text()").toString());
		
		if(page.getResultItems().get("name")==null) {
			//���page��name==null�����������page
			page.setSkip(true);
		}
		page.putField("readme",page.getHtml().xpath("//div[@id='readme']/tidyText()"));
		
	}
	@Override
	public Site getSite() {
		// TODO Auto-generated method stub
		return site;
	}
	public static void main(String[] args){
		Spider.create(new WebSpider())
		.addUrl("https://github.com/code4craft")  //�ӵ�ǰ������ָ����վ��ʼץȡ
		.addPipeline(new JsonFilePipeline("E:\\webmagic\\"))   //ץȡ�Ľ����pipeline����Ϊjson�ĸ�ʽ
		.thread(5)   //����5���߳���ץȡ
		.run();     //��������
	}
	public class ConsolePipeline implements Pipeline {

	    @Override
	    public void process(ResultItems resultItems, Task task) {
	        System.out.println("get page: " + resultItems.getRequest().getUrl());
	        //�������н�������������̨�����������е�"author"��"name"��"readme"����һ��key���������Ƕ�Ӧ��value
	        for (Map.Entry<String, Object> entry : resultItems.getAll().entrySet()) {
	            System.out.println(entry.getKey() + ":\t" + entry.getValue());
	        }
	    }
	}
	
}
