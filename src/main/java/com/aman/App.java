package com.aman;

import com.aman.controller.ControllerSender;
import com.aman.service.ServiceSender;

public final class App {

    public static void main(String[] args) {
        ControllerSender controller = new ControllerSender(new ServiceSender());
        controller.getOrders();
    }
}
