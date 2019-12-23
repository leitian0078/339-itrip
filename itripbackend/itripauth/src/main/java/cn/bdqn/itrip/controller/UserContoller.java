package cn.bdqn.itrip.controller;

import cn.itrip.common.DtoUtil;
import cn.itrip.common.MD5;
import cn.itrip.common.RedisHelp;
import cn.itrip.dao.itripUser.ItripUserMapper;
import cn.itrip.dto.Dto;
import cn.itrip.pojo.ItripUser;
import cn.itrip.vo.ItripTokenVO;
import cn.itrip.vo.ItripUserVO;
import com.alibaba.fastjson.JSONArray;
import com.cloopen.rest.sdk.CCPRestSmsSDK;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;


@Api(value = "appinfo",description = "用户模块")
@Controller
@RequestMapping("api")
public class UserContoller {

    @Resource
    RedisHelp redisHelp;

    @Resource
    ItripUserMapper dao;

    @RequestMapping(value="/dologin",method=RequestMethod.POST,produces= "application/json")


    @ApiImplicitParams({
            @ApiImplicitParam(paramType="query",required=true,value="用户名",name="name",defaultValue="itrip@163.com"),
            @ApiImplicitParam(paramType="query",required=true,value="密码",name="password",defaultValue="111111"),
    })
    public @ResponseBody Dto Getlist(String name, String password, HttpServletRequest request)
    {
        Map<String,Object> map=new HashMap<String, Object>();
        map.put("n",name);
        map.put("p",password);
        ItripUser user=dao.GetLogin(map);

        if(user==null)
        {
            return DtoUtil.returnFail("登录失败","1000");
        }
        //当用户登录成功，把用户名存入Redis中
        //key为token=PC：“前缀PC-USERCODE-USERID-CREATIONDATE-RONDEM[6位]”
        //value用户的对象转成字符串 fastjson
        String newtoken="";

            newtoken=Token(user,request.getHeader("User-Agent"));
            redisHelp.setRedis(newtoken, JSONArray.toJSONString(user), 60*60*2);

        ItripTokenVO tokenVO=new ItripTokenVO(newtoken,Calendar.getInstance().getTimeInMillis()+60*60*2,Calendar.getInstance().getTimeInMillis());

        return DtoUtil.returnDataSuccess(tokenVO);
    }


    @RequestMapping(value="/registerbyphone",method=RequestMethod.POST,produces= "application/json")
    public @ResponseBody Dto Register(@RequestBody ItripUserVO userVO)
    {
        try {
            userVO.setUserPassword(MD5.getMd5(userVO.getUserPassword(),32));
            //第一步插入到数据库中，未激活
            int i=dao.loginInsert(userVO);
            if(i>0)
            {
                //给用户发送验证码  怎么样给Maven打包
                Random random=new Random();
                int j=random.nextInt(9999);
                SentSms(userVO.getUserCode(),""+j);
                //把手机号及验证码存入redis
                redisHelp.setRedis(userVO.getUserCode(),""+j,60);

            }

            return DtoUtil.returnDataSuccess("注册成功");
        }
        catch (Exception ex)
        {
             return DtoUtil.returnFail("注册失败","1000");
        }

    }


    @RequestMapping(value="/validatephone",method=RequestMethod.PUT,produces= "application/json")
    public @ResponseBody Dto Register_1(String user,String code) throws Exception {
          String key=redisHelp.getkey(user);

          if(key.equals(code))
          {
              //证明验证码正确 修改数据库的标识
              dao.updateStatus(user);
              return DtoUtil.returnSuccess("激活成功");
          }
          return DtoUtil.returnFail("激活失败","1000");
    }

    //构建发短信的方法
    public static boolean SentSms(String phone,String Context)
    {
        HashMap<String, Object> result = null;

        //初始化SDK
        CCPRestSmsSDK restAPI = new CCPRestSmsSDK();

        //******************************注释*********************************************
        //*初始化服务器地址和端口                                                       *
        //*沙盒环境（用于应用开发调试）：restAPI.init("sandboxapp.cloopen.com", "8883");*
        //*生产环境（用户应用上线使用）：restAPI.init("app.cloopen.com", "8883");       *
        //*******************************************************************************
        restAPI.init("app.cloopen.com", "8883");

        //******************************注释*********************************************
        //*初始化主帐号和主帐号令牌,对应官网开发者主账号下的ACCOUNT SID和AUTH TOKEN     *
        //*ACOUNT SID和AUTH TOKEN在登陆官网后，在“应用-管理控制台”中查看开发者主账号获取*
        //*参数顺序：第一个参数是ACOUNT SID，第二个参数是AUTH TOKEN。                   *
        //*******************************************************************************
        restAPI.setAccount("8a216da86cd1bc98016cd6206d030520", "3ff2d4bb6cd04d3ebbf671705eda7a6f");


        //******************************注释*********************************************
        //*初始化应用ID                                                                 *
        //*测试开发可使用“测试Demo”的APP ID，正式上线需要使用自己创建的应用的App ID     *
        //*应用ID的获取：登陆官网，在“应用-应用列表”，点击应用名称，看应用详情获取APP ID*
        //*******************************************************************************
        restAPI.setAppId("8a216da86cd1bc98016cd6206d570527");


        //******************************注释****************************************************************
        //*调用发送模板短信的接口发送短信                                                                  *
        //*参数顺序说明：                                                                                  *
        //*第一个参数:是要发送的手机号码，可以用逗号分隔，一次最多支持100个手机号                          *
        //*第二个参数:是模板ID，在平台上创建的短信模板的ID值；测试的时候可以使用系统的默认模板，id为1。    *
        //*系统默认模板的内容为“【云通讯】您使用的是云通讯短信模板，您的验证码是{1}，请于{2}分钟内正确输入”*
        //*第三个参数是要替换的内容数组。																														       *
        //**************************************************************************************************

        //**************************************举例说明***********************************************************************
        //*假设您用测试Demo的APP ID，则需使用默认模板ID 1，发送手机号是13800000000，传入参数为6532和5，则调用方式为           *
        //*result = restAPI.sendTemplateSMS("13800000000","1" ,new String[]{"6532","5"});																		  *
        //*则13800000000手机号收到的短信内容是：【云通讯】您使用的是云通讯短信模板，您的验证码是6532，请于5分钟内正确输入     *
        //*********************************************************************************************************************
        result = restAPI.sendTemplateSMS(phone,"1" ,new String[]{Context,"1"});

        System.out.println("SDKTestGetSubAccounts result=" + result);
        if("000000".equals(result.get("statusCode"))){
            //正常返回输出data包体信息（map）
			/*HashMap<String,Object> data = (HashMap<String, Object>) result.get("data");
			Set<String> keySet = data.keySet();
			for(String key:keySet){
				Object object = data.get(key);
				System.out.println(key +" = "+object);

			}*/
            return true;

        }else{
            //异常返回输出错误码和错误信息
            System.out.println("错误码=" + result.get("statusCode") +" 错误信息= "+result.get("statusMsg"));
            return false;

        }
    }

    public static String Token(ItripUser user,String Agent)
    {
         StringBuilder builder=new StringBuilder();
         builder.append("PC-");
         builder.append(user.getUserCode()+"-");
         builder.append(user.getId()+"-");
         builder.append(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
         builder.append(MD5.getMd5("",6));
         return  builder.toString();

    }
}
