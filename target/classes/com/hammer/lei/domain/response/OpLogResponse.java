package com.hammer.lei.domain.response;

import com.hammer.lei.domain.response.component.WechatHttpResponseBase;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpLogResponse extends WechatHttpResponseBase {
}