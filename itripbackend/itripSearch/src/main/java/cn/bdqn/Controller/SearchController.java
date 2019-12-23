package cn.bdqn.Controller;

import cn.bdqn.dao.BaseSolr;
import cn.bdqn.entity.ItripHotelVO;
import cn.bdqn.entity.Page;
import cn.bdqn.entity.SearchHotCityVO;
import cn.bdqn.entity.SearchHotelVO;
import cn.itrip.common.DtoUtil;
import cn.itrip.dto.Dto;
import com.alibaba.fastjson.JSONArray;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Controller
public class SearchController {

    @RequestMapping("/text")
    @ResponseBody
    public Object text(HttpServletResponse response) throws Exception {

        return JSONArray.toJSONString("成功");
    }


    @RequestMapping(value = "/api/hotellist/searchItripHotelListByHotCity", produces = "application/json", method = RequestMethod.POST)
    @ResponseBody
    public  Dto GetObj(@RequestBody SearchHotCityVO vo) throws IOException, SolrServerException {
        BaseSolr baseSolr=new BaseSolr();
        SolrQuery solrQuery=new SolrQuery();
        solrQuery.addFilterQuery("cityId:"+vo.getCityId());
        return DtoUtil.returnDataSuccess(baseSolr.GetList(solrQuery));
    }

    //分页
    @RequestMapping(value = "/api/hotellist/searchItripHotelPage", produces = "application/json", method = RequestMethod.POST)
    @ResponseBody
    public Dto<Page<ItripHotelVO>> GetObj(@RequestBody SearchHotelVO vo) throws Exception {
        SolrQuery solrQuery = new SolrQuery();
        BaseSolr baseSolr = new BaseSolr();

        if (vo.getKeywords()!=null){
            solrQuery.addFilterQuery("keyword:"+vo.getKeywords());
        }
        if (vo.getDestination()!=null){
            solrQuery.addFilterQuery("destination:"+vo.getDestination());
        }
        if (vo.getFeatureIds()!=null){
            String [] list = vo.getFeatureIds().split(",");

            for (int i=0;i<list.length;i++){
               if (i==0){
                   solrQuery.addFilterQuery("featureIds:*"+list[i]+"*");
               }else {
                   solrQuery.addFilterQuery(" or featureIds:*"+list[i]+"*");
               }
            }
        }

        if (vo.getTradeAreaIds()!=null){
            String [] list = vo.getTradeAreaIds().split(",");

            for (int i=0;i<list.length;i++){
                if (i==0){
                    solrQuery.addFilterQuery("tradingAreaIds:*"+list[i]+"*");
                }else {
                    solrQuery.addFilterQuery(" or tradingAreaIds:*"+list[i]+"*");
                }
            }
        }

        if (vo.getMinPrice()!=null){
            solrQuery.addFilterQuery("minPrice:["+vo.getMinPrice()+" TO *]");
        }
        if (vo.getMinPrice()!=null){
            solrQuery.addFilterQuery("minPrice:[* TO "+vo.getMaxPrice()+"]");
        }
        if (vo.getHotelLevel()!=null){
            solrQuery.addFilterQuery("hotelLevel:"+vo.getHotelLevel());
        }
        if (vo.getPageNo()==null){
            vo.setPageNo(1);
        }
        if (vo.getPageSize()==null){
            vo.setPageSize(6);
        }
        Page<ItripHotelVO> page = baseSolr.GetlistBypage(solrQuery,vo.getPageNo(),vo.getPageSize());

        return DtoUtil.returnDataSuccess(page);
    }
}
