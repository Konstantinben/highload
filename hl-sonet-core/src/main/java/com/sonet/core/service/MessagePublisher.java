package com.sonet.core.service;

import liquibase.pro.packaged.T;

public interface  MessagePublisher<T extends Object> {
    void publish(T object);
}
