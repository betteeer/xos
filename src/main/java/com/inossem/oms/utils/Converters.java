package com.inossem.oms.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.google.common.collect.Lists;

import org.springframework.beans.BeanUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 转换beanBaseException
 * @author czh
 * @date 2022/11/18
 */
public class Converters {

    /**
     * 转换bean
     * @param source    要转换的bean
     * @param destClass 结果bean class类型
     * @return
     */
    public static <S, D> D convert(S source, Class<D> destClass) throws Exception {
        if (source == null) {
            return null;
        }
        D dest;
        try {
            dest = BeanUtils.instantiateClass(destClass);
            BeanUtils.copyProperties(source, dest, destClass);
        } catch (Exception e) {
            throw new Exception("parse fail");
        }
        return dest;
    }

    /**
     * 保留原来的属性，把新的属性值赋值到老的对象中
     * @param source    要转换的bean
     * @param destClass 结果bean class类型
     * @return
     */
    public static <S, D> D convert(S source, D destClass) throws Exception {
        if (source == null) {
            return null;
        }
        try {
            BeanUtil.copyProperties(source, destClass, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
        } catch (Exception e) {
            throw new Exception("parse fail");
        }
        return destClass;
    }



    /**
     * 转换bean List
     * @param sourceList 要转换bean的集合
     * @param destClass  目标bean class类型
     * @return
     */
    public static <S, D> List<D> convertList(List<S> sourceList, Class<D> destClass) throws Exception {
        List<D> list = Lists.newArrayList();
        if (sourceList == null || sourceList.isEmpty()) {
            return list;
        }
        return sourceList.parallelStream().map(s -> {
            try {
                return convert(s, destClass);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
    }

    /**
     * 将对象字段映射到map中
     * @param source    要转换的bean
     * @return
     */
    public static <S> Map convertObjToMap(S source) throws Exception {
        if (source == null) {
            return null;
        }
        Map<String,Object> map = new HashMap<>(16);
        try {
            BeanUtil.copyProperties(source, map);
        } catch (Exception e) {
            throw new Exception("parse fail");
        }
        return map;
    }

}
