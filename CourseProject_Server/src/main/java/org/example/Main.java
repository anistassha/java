package org.example;

import org.example.WorkWithClient.ClientThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    private static final int PORT = 5555;
    private static ServerSocket serverSocket;
    private static ClientThread clientHandler;
    private static Thread thread;

    public static void main(String[] args) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running on port " + PORT);
            while (true) {
                Socket socket = serverSocket.accept();
                String socketInfo = "Клиент " + socket.getInetAddress() + socket.getPort() + " подключен";
                System.out.println(socketInfo);
                clientHandler = new ClientThread(socket);
                thread = new Thread(clientHandler);
                thread.start();
                System.out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void stopServer() throws IOException {
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }
    }
}