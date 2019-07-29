package ru.arlen.task.server;

import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.arlen.task.server.clientpart.ClientInstance;
import ru.arlen.task.server.core.InMemoryStore;
import ru.arlen.task.server.serverpart.ServerInstance;

public class Server {
  private static final Logger logger = LoggerFactory.getLogger(Server.class);

  public static void main(String[] args) {
    InMemoryStore store = new InMemoryStore();

    ServerInstance server = new ServerInstance(8000, store);
    ClientInstance client = new ClientInstance("127.0.0.1", 5555, store);
    server.start();
    client.start();

    try (Scanner scanner = new Scanner(System.in)) {
      logger.info("(Server started, use Enter to stop and go back to the console...)");
      scanner.nextLine();
      client.interrupt();
      server.interrupt();
    }
  }
}
