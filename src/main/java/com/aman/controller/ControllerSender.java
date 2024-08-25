package com.aman.controller;

import com.aman.service.IServiceSender;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ControllerSender {
    private final IServiceSender serviceSender;
    
    public String getOrders(){
        return serviceSender.obtainOrders();
    };
}
