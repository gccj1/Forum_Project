package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.dto.AccountDetails;
import com.example.entity.vo.request.AccountDetailsVO;

public interface AccountDetailService extends IService<AccountDetails> {
    AccountDetails getDetailsById(int id);
    boolean saveAccountDetails(int id, AccountDetailsVO accountDetailsVO);

}
