package org.example;

import clients.Client;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {

        System.out.print("Pls, enter your name here: ");
        Scanner in = new Scanner(System.in);
        String userName = in.nextLine();
        in.reset();

        Thread cl = new Thread(new Client("localhost", 8888, userName));
        cl.start();
    }
}