package com.example.entity.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.entity.BaseData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@TableName("db_account_privacy")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountPrivacy implements BaseData {
    @TableId
    Integer uid;
    boolean gender=true;
    boolean email=true;
    boolean phone=true;
    boolean qq=true;
    boolean wx=true;
}
