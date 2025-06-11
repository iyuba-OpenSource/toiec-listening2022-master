package com.iyuba.core.util;


import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import io.reactivex.annotations.Nullable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

/**
 * Created by iyuba on 2018/11/6.
 */
public class JsonOrXmlConverterFactory extends Converter.Factory {

    private final Converter.Factory xmlFactory = SimpleXmlConverterFactory.create();
    private final Converter.Factory jsonFactory = GsonConverterFactory.create();

    public static JsonOrXmlConverterFactory create() {
        return new JsonOrXmlConverterFactory();
    }

    @Nullable
    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        for (Annotation annotation : annotations) {
            if (!(annotation instanceof ResponseFormat)) {
                continue;
            }
            String value = ((ResponseFormat) annotation).value();
            if (ResponseFormat.JSON.equals(value)) {
                return jsonFactory.responseBodyConverter(type, annotations, retrofit);
            } else if (ResponseFormat.XML.equals(value)) {
                return xmlFactory.responseBodyConverter(type, annotations, retrofit);
            }
        }
        return null;
    }

    @Nullable
    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        return jsonFactory.requestBodyConverter(type, parameterAnnotations, methodAnnotations, retrofit);
    }
}
