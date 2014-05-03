package com.gmail.ipshuvaev;

import com.gmail.ipshuvaev.controller.AssetController;
import com.gmail.ipshuvaev.controller.IndexController;
import com.gmail.ipshuvaev.controller.StartSearchController;
import com.gmail.ipshuvaev.controller.StopSearchController;
import com.gmail.ipshuvaev.server.CustomHttpServer;
import com.gmail.ipshuvaev.server.RequestDispatcher;

public class Main {
    public static void main(String[] args) throws Exception {
        RequestDispatcher requestDispatcher = new RequestDispatcher()
            .addHandler(new IndexController())
            .addHandler(new StartSearchController())
            .addHandler(new AssetController())
            .addHandler(new StopSearchController());

        System.out.println("Server running on port 8080");
        new CustomHttpServer(3000, requestDispatcher).run();
    }
}
