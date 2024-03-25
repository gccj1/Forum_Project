package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.dto.Account;
import com.example.entity.vo.request.ConfirmResetVO;
import com.example.entity.vo.request.EmailRegisterVO;
import com.example.entity.vo.request.passResetVO;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AccountService extends IService<Account>, UserDetailsService {
    Account findAccountByNameOrEmail(String text);
    Account findAccountById(int id);
    String emailVerifyCode(String type, String email, String address);
    String registerEmailAccount(EmailRegisterVO info);
    String resetEmailAccountPassword(passResetVO info);
    String resetConfirm(ConfirmResetVO info);
    String emailModify(int id, ConfirmResetVO vo);
}
