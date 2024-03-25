package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.AccountPrivacy;
import com.example.entity.vo.request.PrivacyVO;
import com.example.mapper.AccountPrivacyMapper;
import com.example.service.AccountPrivacyService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountPrivacyImp extends ServiceImpl<AccountPrivacyMapper, AccountPrivacy> implements AccountPrivacyService {

    @Override
    public boolean saveAccountPrivacy(int id, PrivacyVO vo) {
        AccountPrivacy accountPrivacy = new AccountPrivacy();
        BeanUtils.copyProperties(vo, accountPrivacy);
        accountPrivacy.setUid(id);
        return this.saveOrUpdate(accountPrivacy);
    }

    @Override
    public AccountPrivacy getAccountPrivacyById(int id) {
        return Optional.ofNullable(getById(id)).orElseGet(AccountPrivacy::new);
    }
}
