package ru.arlen.task.client;

import static ru.arlen.task.server.utils.Constants.SERVER_HOST;
import static ru.arlen.task.server.utils.Constants.SERVER_PORT;

import java.lang.invoke.MethodHandles;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client {
  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  public static void main(String[] args) {
    ClientInstance client = new ClientInstance(SERVER_HOST, SERVER_PORT);
    client.start();

    try (Scanner scanner = new Scanner(System.in)) {
      logger.info("(Client started, use Enter to stop and go back to the console...)");
      scanner.nextLine();
      client.interrupt();
    }
  }
}
