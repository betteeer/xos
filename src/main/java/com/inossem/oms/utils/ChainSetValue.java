package com.inossem.oms.utils;

import com.inossem.sco.common.core.utils.StringUtils;

import java.util.List;
import java.util.function.Consumer;

public class ChainSetValue<T> {

    public static <T> ChainSetValue<T> of(T object) {
        return new ChainSetValue(object);
    }

    private T object;

    public ChainSetValue(T object) {
        this.object = object;
    }

    public ChainSetValue<T> update(
            Consumer<String> consumer, String value) {
        consumer.accept(value);
        return this;
    }

    public <D> ChainSetValue<T> update(
            Consumer<? super D> consumer, D value) {
        consumer.accept(value);
        return this;
    }
    public <D> ChainSetValue<T> update(
            Consumer<? super List<?>> consumer, List<?> value) {
        consumer.accept(value);
        return this;
    }
    public ChainSetValue<T> updateIfNotNull(
            Consumer<String> consumer, String value) {

        if (StringUtils.isNotEmpty(value)) {
            consumer.accept(value);
        }
        return this;
    }

    public <D> ChainSetValue<T> updateIfNotNull(
            Consumer<? super D> consumer, D value) {

        if (StringUtils.isNotNull(value)) {
            consumer.accept(value);
        }
        return this;
    }
    public <D> ChainSetValue<T> updateIfNotNull(
            Consumer<? super List<?>> consumer, List<?> value) {
        if (StringUtils.isNotEmpty(value)) {
            consumer.accept(value);
        }
        return this;
    }
    public T val() {
        return (T)object;
    }

//    public static void main(String[] args) {
//        StoHeader stoHeader = new StoHeader();
//        stoHeader.setStoNotes("12312");
//        StoHeader sto = com.inossem.oms.utils.ChainSetValue.of(stoHeader)
//                .updateIfNotNull(stoHeader::setStoNumber, "123")
//                .updateIfNotNull(stoHeader::setIsDeleted, 1)
//                .updateIfNotNull(stoHeader::setCreateDate, new Date())
//                .updateIfNotNull(stoHeader::setStoNotes, "")
//                .updateIfNotNull(stoHeader::setItems, new ArrayList<>())
//                .val();
//        System.out.println(sto);
//    }
}





