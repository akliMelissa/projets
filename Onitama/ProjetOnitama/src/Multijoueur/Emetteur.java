package Multijoueur;

import java.io.ObjectOutputStream;
import java.util.concurrent.BlockingQueue;

public class Emetteur implements Runnable {
    private final BlockingQueue<Command> qSend;
    private final ObjectOutputStream oos;

    public Emetteur(BlockingQueue<Command> qSend, ObjectOutputStream oos) {
        this.qSend = qSend;
        this.oos = oos;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Command c = qSend.take();
                try {
                    oos.writeObject(c);
                    oos.flush();
                    System.out.println("[ENVOI] " + c);
                } catch (java.net.SocketException se) {
                    System.err.println("Connexion réseau perdue (Socket fermée) : " + se);
                    break; // On quitte la boucle proprement
                } catch (Exception ex) {
                    System.err.println("Erreur lors de l'envoi du message : " + ex);
                    ex.printStackTrace();
                    break;
                }
            }
        } catch (InterruptedException ie) {
            System.out.println("Emetteur interrompu (arrêt normal)");
        } catch (Exception e) {
            System.err.println("Erreur inattendue dans Emetteur : " + e);
            e.printStackTrace();
        }
        System.out.println("Thread Emetteur arrêté.");
    }
}
