package com.timerg.mapper;

public interface Mapper <F, T> {
    T from(F object);
}
