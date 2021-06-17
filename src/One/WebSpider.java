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
		//添加目标请求页面，下面这段代码就用到了正则表达式，它表示匹配所有"https://github.com/code4craft/webmagic"这样的链接。
		page.addTargetRequests(page.getHtml().links().regex("(https://github\\.com/\\w+/\\w+)").all());
		//获取的信息--author最后转换为String类型数据，注意：putField的内容可以根据需求改变！！
		page.putField("author",page.getUrl().regex("https://github\\.com/(\\w+)/.*").toString());
		//下面这段代码使用了XPath，它的意思是“查找所有class属性为'entry-title public'的h1元素，并找到他的strong子节点的a子节点，并提取a节点的文本信息”。
		page.putField("name",page.getUrl().xpath("//h1[@class='entry-title public']/strong/a/text()").toString());
		
		if(page.getResultItems().get("name")==null) {
			//如果page的name==null，则跳过这个page
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
		.addUrl("https://github.com/code4craft")  //从当前的链接指向网站开始抓取
		.addPipeline(new JsonFilePipeline("E:\\webmagic\\"))   //抓取的结果用pipeline保存为json的格式
		.thread(5)   //开启5个线程来抓取
		.run();     //启动爬虫
	}
	public class ConsolePipeline implements Pipeline {

	    @Override
	    public void process(ResultItems resultItems, Task task) {
	        System.out.println("get page: " + resultItems.getRequest().getUrl());
	        //遍历所有结果，输出到控制台，上面例子中的"author"、"name"、"readme"都是一个key，其结果则是对应的value
	        for (Map.Entry<String, Object> entry : resultItems.getAll().entrySet()) {
	            System.out.println(entry.getKey() + ":\t" + entry.getValue());
	        }
	    }
	}
	
}
