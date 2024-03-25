package com.example.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.example.entity.vo.request.WeatherVO;
import com.example.service.WeatherService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;
@Service
public class WeatherImp implements WeatherService {
    @Resource
    RestTemplate restTemplate;
    @Resource
    StringRedisTemplate template;
    @Value("${spring.weather.key}")
    String key;
    @Override
    public WeatherVO fetchWeatherData(double longitude, double latitude) {
        byte[] data = restTemplate.getForObject("https://geoapi.qweather.com/v2/city/lookup?location=" + longitude + "," + latitude + "&key=" + key,
                byte[].class);
        if(data==null) return null;
        JSONObject geo = gzipData(data);
        if (geo != null) {
            JSONObject location = geo.getJSONArray("location").getJSONObject(0);
            int id=location.getInteger("id");
            String key="weather: "+id;
            String cache=template.opsForValue().get(key);
            if(cache!=null) return JSONObject.parseObject(cache,WeatherVO.class);
            WeatherVO vo=fetchFromAPI(id,location);
            template.opsForValue().set(key, JSONObject.toJSONString(vo),1, TimeUnit.HOURS);
            return vo;
        }
        return null;
    }
    private WeatherVO fetchFromAPI(int id,JSONObject location){
        WeatherVO weatherVO = new WeatherVO();
        weatherVO.setLocation(location);
        byte[] data = restTemplate.getForObject("https://devapi.qweather.com/v7/weather/now?location=" + id + "&key=" + key,
                byte[].class);
        JSONObject now = gzipData(data);
        if(now != null){
            weatherVO.setNow(now.getJSONObject("now"));
        }
        byte[] hourlyData = restTemplate.getForObject("https://devapi.qweather.com/v7/weather/24h?location=" + id + "&key=" + key,
                byte[].class);
        JSONObject hourly = gzipData(hourlyData);
        if(hourly != null){
            weatherVO.setHourly(new JSONArray(hourly.getJSONArray("hourly").stream().limit(5).toList()));
        }
        return weatherVO;
    }
    private JSONObject gzipData(byte[] data){
        try(GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(data));
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream()){
            byte[] bytes = new byte[1024];
            int read;
            while((read = gzip.read(bytes)) != -1)
                outputStream.write(bytes, 0, read);
            outputStream.flush();
            return JSONObject.parseObject(outputStream.toString());
        }catch (IOException e) {
            return null;
        }
    }
}

