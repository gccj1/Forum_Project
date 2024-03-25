package com.example.utils;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
public class CacheUtils {
    @Resource
    StringRedisTemplate template;


    public <T> T takeFromCache(String key, Class<T> clazz) {
        String s=template.opsForValue().get(key);
        if(s==null) return  null;
        return JSONObject.parseObject(s).to(clazz);
    }
    public <T> List<T> takeListFromCache(String key, Class<T> clazz) {
        String s=template.opsForValue().get(key);
        if(s==null) return  null;
        return JSONArray.parseArray(s).toList(clazz);
    }
    public <T> void putToCache(String key, T value,long expire) {
        template.opsForValue().set(key, JSONObject.toJSONString(value), expire, java.util.concurrent.TimeUnit.SECONDS);
    }
    public <T> void putListToCache(String key, List<T>  value,long expire) {
        template.opsForValue().set(key, JSONArray.toJSONString(value), expire, java.util.concurrent.TimeUnit.SECONDS);
    }
    public void deleteCache(String key){
        template.delete(key);
    }
    public void deleteCachePattern(String key){
        Set<String> keys = Optional.ofNullable(template.keys(key)).orElse(Collections.emptySet());
        template.delete(keys);
    }
}
