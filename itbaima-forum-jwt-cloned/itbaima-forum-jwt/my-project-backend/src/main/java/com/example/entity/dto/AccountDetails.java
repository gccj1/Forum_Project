package com.example.entity.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.entity.BaseData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@TableName("db_account_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDetails implements BaseData {
    @TableId
    Integer uid;
    int gender;
    String phone;
    String qq;
    String wx;
    String text;
}
