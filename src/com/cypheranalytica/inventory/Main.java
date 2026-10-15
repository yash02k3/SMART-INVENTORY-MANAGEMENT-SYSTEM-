package com.cypheranalytica.inventory;

import com.cypheranalytica.inventory.dao.DAOFactory;
import com.cypheranalytica.inventory.ui.ConsoleMenu;

/**
 * Main Application Entry Point.
 * 
 * Smart Inventory Management System using Core Java
 * Developed by: Payal (Roll No: 248036)
 * B.Tech AI & DS, 3rd Year
 * Global Institute of Technology & Management, Gurugram
 * Internship Partner: Cypher Analytica, Indore (June - August 2026)
 */
public class Main {

    private static WebServer webServer;

    public static void main(String[] args) {
        printBanner();

        // 1. Initialize In-Memory / Persistence DAO layer with presentation sample data
        DAOFactory.initialize();

        // 2. Start Embedded Web Server for browser frontend (Slides 15 & 16)
        int port = 8080;
        try {
            webServer = new WebServer(port);
            webServer.start();
        } catch (Exception e) {
            System.err.println("Notice: Web server could not bind to port " + port + " (" + e.getMessage() + ").");
            System.out.println("You can still use the interactive Console Menu below, or open web/index.html in any browser!");
        }

        // Check if started in web-only mode
        if (args.length > 0 && (args[0].equalsIgnoreCase("--web") || args[0].equalsIgnoreCase("-w") || args[0].equalsIgnoreCase("server"))) {
            System.out.println(">> Running in dedicated Web Server mode. Press [Ctrl + C] in terminal to stop.");
            try {
                Thread.currentThread().join();
            } catch (InterruptedException ignored) {
            }
            return;
        }

        // 3. Start interactive console UI in terminal
        ConsoleMenu menu = new ConsoleMenu();
        menu.start();
    }

    private static void printBanner() {
        System.out.println("********************************************************************************");
        System.out.println("*                     GLOBAL INSTITUTE OF TECHNOLOGY & MANAGEMENT              *");
        System.out.println("*                                Gurugram, Haryana                             *");
        System.out.println("*                          SUMMER INTERNSHIP PROJECT                           *");
        System.out.println("*                    Smart Inventory Management System (Core Java)             *");
        System.out.println("*                                                                              *");
        System.out.println("*  Submitted by : Payal (Roll No: 248036) | B.Tech AI & DS 3rd Year            *");
        System.out.println("*  Internship   : Cypher Analytica, Indore (02 June 2026 - 02 Aug 2026)        *");
        System.out.println("*  Core Topics  : OOP, Collections, Exception Handling, Multithreading, UI    *");
        System.out.println("********************************************************************************");
    }
}
