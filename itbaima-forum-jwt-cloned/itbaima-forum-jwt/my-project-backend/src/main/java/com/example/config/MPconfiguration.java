package com.example.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MPconfiguration {
//    @Bean
//    public PaginationInnerInterceptor paginationInterceptor() {
//        PaginationInnerInterceptor paginationInnerInterceptor =new PaginationInnerInterceptor(DbType.MYSQL);
//        paginationInnerInterceptor.setMaxLimit(100L);
//        return paginationInnerInterceptor;
//    }
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        return interceptor;
    }


//    @Bean
//    public PaginationInnerInterceptor paginationInterceptor() {
//        return new PaginationInnerInterceptor();
//    }

}
