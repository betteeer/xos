package com.inossem.oms.utils;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        Field[] fields = metaObject.getOriginalObject().getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                Object value = metaObject.getValue(field.getName());
                if (value == null) {
                    Object defaultValue = field.getType().newInstance();
                    metaObject.setValue(field.getName(), defaultValue);
                }
            } catch (Exception e) {
                // handle exception
            }
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        Field[] fields = metaObject.getOriginalObject().getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                Object value = metaObject.getValue(field.getName());
                if (value == null) {
                    Object defaultValue = field.getType().newInstance();
                    metaObject.setValue(field.getName(), defaultValue);
                }
            } catch (Exception e) {
                // handle exception
            }
        }
    }
}