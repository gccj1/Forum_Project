package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.dto.Account;
import com.example.entity.dto.AccountDetails;
import com.example.entity.dto.AccountPrivacy;
import com.example.entity.vo.request.AccountDetailsVO;
import com.example.entity.vo.request.ConfirmResetVO;
import com.example.entity.vo.request.PrivacyVO;
import com.example.entity.vo.response.AccountVO;
import com.example.service.AccountDetailService;
import com.example.service.AccountPrivacyService;
import com.example.service.AccountService;
import com.example.utils.Const;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class AccountController {

    @Resource
    AccountService service;
    @Resource
    AccountDetailService accountDetailService;
    @Resource
    AccountPrivacyService privacyService;

    @GetMapping("/info")
    public RestBean<AccountVO> info(@RequestAttribute(Const.ATTR_USER_ID) int id){
        Account account = service.findAccountById(id);
        return RestBean.success(account.asViewObject(AccountVO.class));
    }
    @PostMapping("/email-modify")
    public  RestBean<Void> modifyEmail(@RequestAttribute(Const.ATTR_USER_ID) int id,
                                       @Valid @RequestBody ConfirmResetVO vo){
        String result = service.emailModify(id,vo);
        return result==null ?  RestBean.success() : RestBean.failure(400,result);
    }
    @GetMapping("/details")
    public RestBean<AccountDetailsVO> details(@RequestAttribute(Const.ATTR_USER_ID) int id) {
        AccountDetails details = Optional
                .ofNullable(accountDetailService.getDetailsById(id))
                .orElseGet(AccountDetails::new);
        return RestBean.success(details.asViewObject(AccountDetailsVO.class));
    }
    @GetMapping("/privacy")
    public RestBean<PrivacyVO> privacy(@RequestAttribute(Const.ATTR_USER_ID) int id) {
        AccountPrivacy accountPrivacy = privacyService.getAccountPrivacyById(id);
        return RestBean.success(accountPrivacy.asViewObject(PrivacyVO.class));
    }
    @PostMapping("/save-details")
    public RestBean<Void> saveDetails(@RequestAttribute(Const.ATTR_USER_ID) int uid,
                                       @RequestBody @Valid AccountDetailsVO vo){
       // 这里应该有详细的校验逻辑
        boolean saved = accountDetailService.saveAccountDetails(uid, vo);
        return saved? RestBean.success():RestBean.failure(400,"用户名已被注册");
    }

    @PostMapping("/save-privacy")
    public RestBean<Void> savePrivacy(@RequestAttribute(Const.ATTR_USER_ID) int id,
                                      @RequestBody @Valid PrivacyVO vo){
        // 这里应该有详细的校验逻辑
        boolean saved = privacyService.saveAccountPrivacy(id, vo);
        return saved? RestBean.success():RestBean.failure(400,"保存隐私发生一些错误,请稍后再试");
    }
}
