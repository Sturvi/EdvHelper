package com.example.edvhelper.service.idcheck;

import java.util.LinkedList;

public class RotatingQueue<T> extends LinkedList<T> {
    private final Object lock = new Object();

    @Override
    public T poll() {
        synchronized (lock) {
            T obj = super.poll();
            if (obj != null) {
                super.add(obj);
            }
            return obj;
        }
    }
}
