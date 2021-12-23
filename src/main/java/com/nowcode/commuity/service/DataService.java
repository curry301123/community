package com.nowcode.commuity.service;

import com.nowcode.commuity.util.RedisLikeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class DataService {
    @Qualifier("redisTemplate")
    @Autowired
    private RedisTemplate template;

    private SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");

    //将指定的ip计入UV
    public void recordUV(String ip){
        String uVkey = RedisLikeUtil.getUVkey(sf.format(new Date()));
        template.opsForHyperLogLog().add(uVkey,ip);
    }
    //统计指定日期范围内的UV
    public long calculateUV(Date start,Date end){
        if(start == null || end == null){
            throw new IllegalArgumentException("参数不能为空");
        }
        //整理日期范围内的key
        List<String> keyList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        while (!calendar.getTime().after(end)){
            String uVkey = RedisLikeUtil.getUVkey(sf.format(calendar.getTime()));
            keyList.add(uVkey);
            calendar.add(Calendar.DATE,1);
        }
        //合并这些数据
        String unionUAkey = RedisLikeUtil.getUnionUVkey(sf.format(start), sf.format(end));
        template.opsForHyperLogLog().union(unionUAkey,keyList.toArray());

        return template.opsForHyperLogLog().size(unionUAkey);
    }

    //将指定用户计入DAU
    public void recordDAU(int userId){
        String redisKey = RedisLikeUtil.getDAUKey(sf.format(new Date()));
        template.opsForValue().setBit(redisKey,userId,true);
    }
    //统计指定范围内的DAU

    public long calculateDAU(Date start,Date end){
        if(start == null || end == null){
            throw new IllegalArgumentException("参数不能为空");
        }
        //整理日期范围内的key
        List<byte[]> keyList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        while (!calendar.getTime().after(end)){
            String key = RedisLikeUtil.getDAUKey(sf.format(calendar.getTime()));
            keyList.add(key.getBytes());
            calendar.add(Calendar.DATE,1);
        }
        return (long) template.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                String redisKey = RedisLikeUtil.getUnionDAUkey(sf.format(start),sf.format(end));
                connection.bitOp(RedisStringCommands.BitOperation.OR,redisKey.getBytes(),keyList.toArray(new byte[0][0]));

                return connection.bitCount(redisKey.getBytes());
            }
        });

    }

}
