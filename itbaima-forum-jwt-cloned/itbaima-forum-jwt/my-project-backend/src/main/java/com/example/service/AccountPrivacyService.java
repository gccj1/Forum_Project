package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.dto.AccountPrivacy;
import com.example.entity.vo.request.PrivacyVO;
import org.apache.ibatis.annotations.Mapper;


public interface AccountPrivacyService extends IService<AccountPrivacy> {
    boolean saveAccountPrivacy(int id, PrivacyVO vo);
    AccountPrivacy getAccountPrivacyById(int id);
}
