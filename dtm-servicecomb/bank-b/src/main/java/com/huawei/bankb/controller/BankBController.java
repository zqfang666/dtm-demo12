/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020. All rights reserved.
 */

package com.huawei.bankb.controller;

import com.huawei.common.BankService;
import com.huawei.common.error.ExceptionUtil;
import com.huawei.middleware.dtm.client.annotations.DTMTxBegin;

import com.netflix.config.DynamicIntProperty;
import com.netflix.config.DynamicPropertyFactory;

import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.apache.servicecomb.provider.springmvc.reference.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.core.MediaType;

@Component
@RestSchema(schemaId = "bankb")
@RequestMapping(path = "/bank", produces = MediaType.APPLICATION_JSON)
public class BankBController {
    private final RestTemplate restInvoker = RestTemplateBuilder.create();

    private final BankService bankBService;

    @GetMapping(value = "/err-rate")
    public int getErrRate() {
        return errorRate.get();
    }

    DynamicIntProperty errorRate = DynamicPropertyFactory.getInstance().getIntProperty("dtm.error", 50);

    public BankBController(BankService bankBService) {
        this.bankBService = bankBService;
    }
    /**
     * 概率抛出异常 同时 bankB转出
     * @param id 账号
     * @param transferMoney 钱数
     */
    @DTMTxBegin(appName = "noninvasive-cse-dtmProviderB-transferIn")
    @GetMapping(value = "/transfer")
    public void dbtransfer(@RequestParam(name = "transferMoney") int transferMoney, @RequestParam(name = "id") int id, @RequestParam(name = "errRate") int errRate) {
        ExceptionUtil.addRuntimeException(errRate);
        bankBService.transferOut(id, transferMoney);  //DtmProviderA
    }
}
