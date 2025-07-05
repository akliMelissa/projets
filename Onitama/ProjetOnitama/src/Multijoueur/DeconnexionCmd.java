package Multijoueur;


import java.io.Serializable;

public class DeconnexionCmd implements Command, Serializable {
    public final String message;
    public DeconnexionCmd(String msg) { this.message = msg; }
}
