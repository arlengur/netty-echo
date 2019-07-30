package ru.arlen.task.server;

import static ru.arlen.task.server.utils.Constants.SERVER_PORT;
import static ru.arlen.task.server.utils.Constants.UPSTREAM_HOST;
import static ru.arlen.task.server.utils.Constants.UPSTREAM_PORT;

import java.lang.invoke.MethodHandles;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.arlen.task.server.clientpart.ServerClientInstance;
import ru.arlen.task.server.core.InMemoryStore;
import ru.arlen.task.server.serverpart.ServerInstance;

public class Server {
  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  public static void main(String[] args) {
    InMemoryStore store = new InMemoryStore();

    ServerInstance server = new ServerInstance(SERVER_PORT, store);
    ServerClientInstance client = new ServerClientInstance(UPSTREAM_HOST, UPSTREAM_PORT, store);
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
