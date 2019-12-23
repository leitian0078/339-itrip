package cn.bdqn.dao;

import cn.bdqn.entity.ItripHotelVO;
import cn.bdqn.entity.Page;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

import java.io.IOException;
import java.util.List;

public class BaseSolr {

    String url="http://localhost:8080/solr-4.9.1/hotel-Core";
    HttpSolrClient solrClient;
    public BaseSolr()
    {
        solrClient=new HttpSolrClient(url);
        solrClient.setParser(new XMLResponseParser());
        solrClient.setConnectionTimeout(500);
    }

    public List<ItripHotelVO> GetList(SolrQuery solrQuery) throws IOException, SolrServerException {
        solrQuery.setQuery("*:*");
        solrQuery.setStart(0);
        solrQuery.setRows(6);
        QueryResponse response=solrClient.query(solrQuery);
        List<ItripHotelVO> list=response.getBeans(ItripHotelVO.class);
        return list;


    }

    public Page<ItripHotelVO> GetlistBypage(SolrQuery solrQuery,Integer pageNo,Integer pageSize) throws Exception, SolrServerException {
       //设定分页的开始位置和每页多少条数据
        solrQuery.setQuery("*:*");
        solrQuery.setStart((pageNo-1)*pageSize);
        solrQuery.setRows(pageSize);

        //通过response接收返回的数据
        QueryResponse response = solrClient.query(solrQuery);
        List<ItripHotelVO> list = ((QueryResponse) response).getBeans(ItripHotelVO.class);

        //没有分页之前的的总条数查询出来
        SolrDocumentList solrDocuments = ((QueryResponse) response).getResults();
        Integer count = new Long(solrDocuments.getNumFound()).intValue();

        //构建page类中的所有数据
        Page<ItripHotelVO> page = new Page(pageNo,pageSize,count);
        page.setRows(list);

        return page;
    }

}
