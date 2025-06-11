package com.iyuba.core.me.protocol;

import com.iyuba.core.common.protocol.BaseJSONResponse;

import org.json.JSONObject;


class GetExampleHomePageResponse extends BaseJSONResponse {

    @Override
    protected boolean extractBody(JSONObject headerEleemnt, String bodyElement) {
        return false;
    }
}
