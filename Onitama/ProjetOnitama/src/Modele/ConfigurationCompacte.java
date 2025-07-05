package Modele;

import java.util.ArrayList;
import java.util.List;

/**
 * Représentation compacte d’une configuration du jeu Onitama.
 * Stocke 96 bits dans 112 bits alloués (14 octets) :
 * - 5 cartes × 4 bits + 1 bit de vue
 * - Plateau 25 cases × 3 bits (63+12 bits)
 *
 * Toutes les opérations sont bit-level :
 * aucune instanciation de Jeu ni clonage d’objets lourds.
 */
public class ConfigurationCompacte {

    // Codes de case (3 bits)
    public static final byte VIDE       = 0;
    public static final byte PION_ROUGE = 1;
    public static final byte PION_BLEU  = 2;
    public static final byte ROI_ROUGE  = 3;
    public static final byte ROI_BLEU   = 4;

    // Constantes de bit-packing
    private static final int  NB_CARTES       = 5;
    private static final int  BITS_PAR_CARTE  = 4;
    private static final int  POS_BIT_VUE     = NB_CARTES * BITS_PAR_CARTE;
    private static final int  MASQUE_CARTE    = (1 << BITS_PAR_CARTE) - 1;

    private static final int  NB_CASES        = 25;
    private static final int  BITS_PAR_CASE   = 3;
    private static final int  NB_CASES_LOW    = 21;
    private static final long MASQUE_CASE     = (1L << BITS_PAR_CASE) - 1;

    // Drapeau static pour accéder aux patterns depuis Jeu
    private static CarteJeu[] referenceCartes;

    // Données internes compressées
    private final int    carteBits;
    private final long   plateauLow;
    private final short  plateauHigh;

    /**
     * Initialise le référentiel de cartes pour générer les patterns.
     * Doit être appelé depuis Jeu après distribution des cartes.
     */
    public static void initReferenceCartes(CarteJeu[] cartes) {
        referenceCartes = cartes;
    }

    /**
     * Constructeur principal : encode les cartes et le plateau.
     */
    public ConfigurationCompacte(int[] cartes, byte[] plateau, boolean vueBleue) {
        if (cartes.length != NB_CARTES || plateau.length != NB_CASES) {
            throw new IllegalArgumentException("5 cartes et 25 cases requises.");
        }
        this.carteBits    = encodeMeta(cartes, vueBleue);
        this.plateauLow  = encodePlateauLow(plateau);
        this.plateauHigh = encodePlateauHigh(plateau);
    }

    /**
     * Construit une configuration depuis un objet Jeu.
     */
    public static ConfigurationCompacte fromJeu(Jeu jeu, boolean vueBleue) {
        // Initialiser la référence des cartes
        initReferenceCartes(jeu.getCartes());

        // Cartes : récupérer leurs indices
        int[] cartes = new int[NB_CARTES];
        cartes[0] = indexOf(referenceCartes, jeu.getJoueur1().getCarte(0));
        cartes[1] = indexOf(referenceCartes, jeu.getJoueur1().getCarte(1));
        cartes[2] = indexOf(referenceCartes, jeu.getJoueur2().getCarte(0));
        cartes[3] = indexOf(referenceCartes, jeu.getJoueur2().getCarte(1));
        cartes[4] = indexOf(referenceCartes, jeu.getCartePARTIE());

        // Plateau : encoder chaque case
        byte[] plateau = new byte[NB_CASES];
        Pion[][] pions = jeu.getPlateau();
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                plateau[y * 5 + x] = encodePion(pions[y][x]);
            }
        }

        return new ConfigurationCompacte(cartes, plateau, vueBleue);
    }

    /**
     * Applique un coup (bit-level) : [fromX, fromY, toX, toY, indexCarte]
     * @return nouvelle configuration (vue inversée)
     */
    public ConfigurationCompacte appliquerCoup(int[] coup) {
        int fromX = coup[0], fromY = coup[1];
        int toX   = coup[2], toY   = coup[3];
        int ci    = coup[4];



        // 1. Extraire et swap des cartes
        int[] cartes = new int[NB_CARTES];
        for (int i = 0; i < NB_CARTES; i++) {
            cartes[i] = getCarte(i);
        }
        int tmp      = cartes[ci];
        cartes[ci]   = cartes[4];
        cartes[4]    = tmp;

        // 2. Copier l’ancien plateau
        byte[] plateau = new byte[NB_CASES];
        for (int i = 0; i < NB_CASES; i++) {
            plateau[i] = getCase(i % 5, i / 5);
        }

        // 3. Appliquer le déplacement correctement
        int idxFrom = fromY * 5 + fromX;
        int idxTo   = toY   * 5 + toX;
        byte piece  = plateau[idxFrom];
        plateau[idxFrom] = VIDE;
        plateau[idxTo]   = piece;

        // 4. Retourner la nouvelle config (vue inversée)
        return new ConfigurationCompacte(cartes, plateau, !vueBleue());
    }

    /* --------------------------------------------------------------------------
     *  Génère tous les coups légaux pour la configuration courante.
     *  Retour : [x0, y0, x1, y1, idxLocal]  avec idxLocal ∈ {0,1}
     * -------------------------------------------------------------------------- */
    public List<int[]> genererCoupsPossibles() {
        List<int[]> coups = new ArrayList<>();

        boolean bleu = vueBleue();                 // joueur courant
        int firstCardIdx = bleu ? 2 : 0;           // bleu : 2-3, rouge : 0-1

        byte[] board = new byte[NB_CASES];
        for (int i = 0; i < NB_CASES; i++)
            board[i] = getCase(i % 5, i / 5);

        for (int ci = firstCardIdx; ci < firstCardIdx + 2; ci++) {
            boolean[][] motif = referenceCartes[getCarte(ci)].getGrilleDeplacements();

            // 🔧 PRÉ-CALCUL DES DÉPLACEMENTS POUR CETTE CARTE
            List<int[]> deplacements = new ArrayList<>();
            for (int dy = 0; dy < 5; dy++) {
                for (int dx = 0; dx < 5; dx++) {
                    if (!motif[dy][dx]) continue;
                    int vx = dx - 2;
                    int vy = dy - 2;
                    deplacements.add(new int[]{vx, vy});
                }
            }

            // POUR CHAQUE PION ALLIÉ
            for (int y = 0; y < 5; y++) {
                for (int x = 0; x < 5; x++) {
                    byte p = board[y * 5 + x];
                    boolean ally = bleu
                            ? (p == PION_BLEU || p == ROI_BLEU)
                            : (p == PION_ROUGE || p == ROI_ROUGE);
                    if (!ally) continue;

                    for (int[] d : deplacements) {
                        int toX = vueBleue()? x + d[0] : x-d[0];
                        int toY = vueBleue()? y + d[1] : y-d[1];

                        if (toX < 0 || toX > 4 || toY < 0 || toY > 4) continue;

                        byte dest = board[toY * 5 + toX];
                        boolean destAlly = bleu
                                ? (dest == PION_BLEU || dest == ROI_BLEU)
                                : (dest == PION_ROUGE || dest == ROI_ROUGE);
                        if (destAlly) continue;
                        // ── AJOUT ──
                        // on a (x,y)->(toX,toY) en interne ;
                        // si on est en vue Bleue, on tourne pour la vue
                        int fx = x, fy = y, tx = toX, ty = toY;

                        coups.add(new int[]{ x, y, toX, toY, ci });


                    }
                }
            }
        }

        return coups;
    }

    // --- Encode / Decode interne ---
    private static int encodeMeta(int[] cartes, boolean vue) {
        int bits = 0;
        for (int i = 0; i < NB_CARTES; i++) {
            bits |= (cartes[i] & MASQUE_CARTE) << (i * BITS_PAR_CARTE);
        }
        if (vue) bits |= 1 << POS_BIT_VUE;
        return bits;
    }

    private long encodePlateauLow(byte[] plateau) {
        long low = 0L;
        for (int i = 0; i < NB_CASES_LOW; i++) {
            low |= (plateau[i] & MASQUE_CASE) << (i * BITS_PAR_CASE);
        }
        return low;
    }

    private short encodePlateauHigh(byte[] plateau) {
        int high = 0;
        for (int i = NB_CASES_LOW; i < NB_CASES; i++) {
            high |= (plateau[i] & (int)MASQUE_CASE) << ((i - NB_CASES_LOW) * BITS_PAR_CASE);
        }
        return (short)high;
    }

    // --- Accesseurs bit-level ---
    public int    getCarte(int idx)     { return (carteBits >> (idx * BITS_PAR_CARTE)) & MASQUE_CARTE; }
    public boolean vueBleue()           { return ((carteBits >> POS_BIT_VUE) & 1) != 0;               }
    public byte   getCase(int x, int y) {
        int idx = y * 5 + x;
        if (idx < NB_CASES_LOW) {
            return (byte)((plateauLow >> (idx * BITS_PAR_CASE)) & MASQUE_CASE);
        }
        return (byte)((plateauHigh >> ((idx - NB_CASES_LOW) * BITS_PAR_CASE)) & MASQUE_CASE);
    }

    /** @return true si la partie est terminée */
    public boolean estTerminee() {
        boolean rR = false, rB = false;
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                byte c = getCase(x, y);
                if (c == ROI_ROUGE) { rR = true; if (x==2 && y==4) return true; }
                if (c == ROI_BLEU)  { rB = true; if (x==2 && y==0) return true; }
            }
        }
        return !(rR && rB);
    }

    private static byte encodePion(Pion p) {
        if (p == null) return VIDE;
        return (p.getCouleur() == Couleur.ROUGE)
                ? (p.getRole()==Role.ROI ? ROI_ROUGE  : PION_ROUGE)
                : (p.getRole()==Role.ROI ? ROI_BLEU   : PION_BLEU);
    }
    public static CarteJeu[] getReferenceCartes() {
        return referenceCartes;
    }
    // En haut de la classe, après les déclarations des champs :
    private ConfigurationCompacte(int carteBits, long plateauLow, short plateauHigh) {
        this.carteBits   = carteBits;
        this.plateauLow  = plateauLow;
        this.plateauHigh = plateauHigh;
    }



    private static int indexOf(CarteJeu[] arr, CarteJeu c) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == c) return i;
        }
        throw new IllegalStateException("Carte inconnue");
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Configuration (vue ");
        sb.append(vueBleue() ? "Bleue" : "Rouge").append(")\n");
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                sb.append(getCase(x,y)).append(' ');
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}

