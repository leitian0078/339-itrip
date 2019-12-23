package cn.bdqn.text;

import cn.bdqn.entity.ItripHotelVO;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.response.QueryResponse;

import java.io.IOException;
import java.util.List;

public class TextSolr {
    public static void main(String[] args) throws IOException, SolrServerException {
        HttpSolrClient solrClient = new HttpSolrClient("http://localhost:8080/solr-4.9.1/hotel-Core");
        solrClient.setParser(new XMLResponseParser());
        solrClient.setConnectionTimeout(500);

       SolrQuery solrQuery = new SolrQuery();
       solrQuery.setQuery("*:*");
        /*solrQuery.addFilterQuery("keyword:","北京");*/

        solrQuery.setStart(0);
        solrQuery.setRows(100);

        /*solrQuery.setSort("id",SolrQuery.ORDER.asc);*/

        QueryResponse response = solrClient.query(solrQuery);

        List<ItripHotelVO> list = response.getBeans(ItripHotelVO.class);

        for (ItripHotelVO i:list){
            System.out.println(i.getId()+i.getHotelName()+i.getExtendPropertyNames());
        }
    }
}
