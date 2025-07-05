package Multijoueur;

import java.io.IOException;

import java.io.ObjectInputStream;
import java.util.concurrent.BlockingQueue;

public class Recepteur implements Runnable {
    private final BlockingQueue<Command> qLocale;
    private final ObjectInputStream      ois;

    public Recepteur(BlockingQueue<Command> q, ObjectInputStream in) {
        this.qLocale = q;
        this.ois     = in;
    }
    @Override
    public void run() {
        try {
            while (true) {
                Object obj = ois.readObject();
                if (obj instanceof Command) {
                    Command cmd = (Command) obj;
                    System.out.println("[RECU] " + cmd);
                    qLocale.put(cmd);
                } else {
                    System.err.println("Objet inattendu : " + obj.getClass());
                }
            }
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            System.out.println("Déconnexion ou erreur réseau : " + e.getMessage());

        }
    }

}
