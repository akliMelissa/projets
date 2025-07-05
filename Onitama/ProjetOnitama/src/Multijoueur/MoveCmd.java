package Multijoueur;

import java.io.Serializable;

public class MoveCmd implements Command, Serializable {

    private final byte ci;      // index carte
    private final byte fx;      // from x
    private final byte fy;      // from y
    private final byte tx;      // to x
    private final byte ty;      // to y
    private final byte joueur;  // 0 = serveur, 1 = client

    public MoveCmd(byte ci, byte fx, byte fy, byte tx, byte ty, byte joueur) {
        this.ci = ci;
        this.fx = fx;
        this.fy = fy;
        this.tx = tx;
        this.ty = ty;
        this.joueur = joueur;
    }

    public byte getCi() {
        return ci;
    }

    public byte getFx() {
        return fx;
    }

    public byte getFy() {
        return fy;
    }

    public byte getTx() {
        return tx;
    }

    public byte getTy() {
        return ty;
    }

    public byte getJoueur() {
        return joueur;
    }

    @Override
    public String toString() {
        return "MoveCmd[carte=" + ci +
                ", from=(" + fx + "," + fy + ")" +
                ", to=(" + tx + "," + ty + ")" +
                ", joueur=" + joueur + "]";
    }
}

