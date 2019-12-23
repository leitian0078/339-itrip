package cn.bdqn.itrip;

import cn.itrip.common.DtoUtil;
import cn.itrip.dao.itripAreaDic.ItripAreaDicMapper;
import cn.itrip.dao.itripLabelDic.ItripLabelDicMapper;
import cn.itrip.dto.Dto;
import cn.itrip.pojo.ItripAreaDic;
import com.alibaba.fastjson.JSONArray;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping("api")
public class CityContoller {

    @RequestMapping(value="/t1",method=RequestMethod.GET,produces= "application/json")
    @ResponseBody
    public  Object te()
    {
        System.out.println("后台代码----");
        return JSONArray.toJSONString("测试");
    }

    @Resource
    ItripAreaDicMapper dao;

    @RequestMapping(value = "/hotel/queryhotcity/{type}", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public  Dto GetHot(@PathVariable("type") Integer t)
    {
        List<ItripAreaDic> list=dao.gethot(t);
        return DtoUtil.returnDataSuccess(list);
    }

    @Resource
    ItripLabelDicMapper dao1;
    @RequestMapping(value = "/hotel/queryhotelfeature", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public  Dto GetH()
    {

        return DtoUtil.returnDataSuccess(dao1.getlist());
    }

    @RequestMapping(value = "/hotel/querytradearea/{cityId}", produces = "application/json", method = RequestMethod.GET)
    public @ResponseBody Dto GetArea(@PathVariable("cityId") Integer cityId){
        List<ItripAreaDic> list = dao.GetByParent(cityId);
        if(list!=null){
            return DtoUtil.returnDataSuccess(list);
        }
        return DtoUtil.returnFail("查询失败!","10202");
    }
}
