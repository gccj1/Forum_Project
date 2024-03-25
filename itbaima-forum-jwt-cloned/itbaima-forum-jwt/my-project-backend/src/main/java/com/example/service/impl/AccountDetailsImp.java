package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.Account;
import com.example.entity.dto.AccountDetails;
import com.example.entity.vo.request.AccountDetailsVO;
import com.example.mapper.AccountDetailsMapper;
import com.example.service.AccountDetailService;
import com.example.service.AccountService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class AccountDetailsImp extends ServiceImpl<AccountDetailsMapper, AccountDetails> implements AccountDetailService  {
    @Resource
    AccountService service;
    @Override
    public AccountDetails getDetailsById(int id) {
        return this.getById(id);
    }

    @Override
    public boolean saveAccountDetails(int id, AccountDetailsVO VO) {
        Account account = service.findAccountByNameOrEmail(VO.getUsername());
        if(account==null || account.getUid()==id){
            service.update()
                    .eq("uid",id)
                    .set("username",VO.getUsername())
                    .update();
            this.saveOrUpdate(new AccountDetails(id,VO.getGender(),VO.getPhone(),VO.getQq(),VO.getWx(),VO.getText()));
            return true;
        }
        return false;
    }


}
