package cn.itrip.common;

import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;

@Repository
public class RedisHelp {

    public void setRedis(String key,String value,int expire)
    {
        Jedis redis=new Jedis("182.61.3.194",6379);
        redis.auth("123456");

        redis.setex(key,expire,value);
    }

    public String getkey(String key)
    {
        Jedis redis=new Jedis("182.61.3.194",6379);
        redis.auth("123456");

        return redis.get(key);
    }
}
