package com.likelion.DSFest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;

//Content type 'application/octet-stream' not supported 오류로 인해서 작성해둔 컴포넌트
//https://velog.io/@sckwon770/Swagger%EC%97%90%EC%84%9C-MultipartFile%EA%B3%BC-DTO-%ED%95%9C-%EB%B2%88%EC%97%90-%EB%B0%9B%EB%8A%94-RequestPart-%EC%9A%94%EC%B2%AD%EC%9D%84-%EC%8B%A4%ED%96%89%ED%95%A0-%EC%88%98-%EC%9E%88%EB%8F%84%EB%A1%9D-%EB%A7%8C%EB%93%A4%EA%B8%B0

@Component
public class MultipartJackson2HttpMessageConverter extends AbstractJackson2HttpMessageConverter {

    public MultipartJackson2HttpMessageConverter(ObjectMapper objectMapper) {
        super(objectMapper, MediaType.APPLICATION_OCTET_STREAM);
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        return false;
    }

    @Override
    public boolean canWrite(Type type, Class<?> clazz, MediaType mediaType) {
        return false;
    }

    @Override
    protected boolean canWrite(MediaType mediaType) {
        return false;
    }
}
