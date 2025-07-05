package Vue;

import Global.UCC;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Vue réseau choix de client ou serveur
 */

public class PlateauReseau extends JComponent {


    private final CollecteurEvenements collecteurEvenements;

    private String      cheminImageFond;
    private Image       imageFond;
    private JLabel      lblMessage;
    private JTextField lblIpInfo, lblHostInfo;
    private JLabel      lblIpPrompt, lblPortPrompt;
    private JTextField  txtIp, txtPort;
    private BoutonArrondi btnConnecter;

    private Theme   theme;
    private boolean estFrancais;
    private boolean estClient;

    // CONSTRUCTEUR
    public PlateauReseau(Theme theme,
                         boolean estClient,
                         CollecteurEvenements collecteur) {

        this.theme                 = theme;
        this.estFrancais           = true;
        this.estClient             = estClient;
        this.collecteurEvenements  = collecteur;

        setLayout(null);

        // fond
        cheminImageFond = theme.getImgSelectionMode();
        try { imageFond = ImageIO.read(UCC.ouvre(cheminImageFond)); }
        catch (IOException e) { e.printStackTrace(); }

        initialiserComposants();
        positionner();
        updateText();
        afficherInfo();
    }


    // INITIALISATION UI
    private void initialiserComposants() {
        lblMessage = new JLabel("", SwingConstants.CENTER);
        lblMessage.setFont(new Font("Georgia", Font.BOLD, 24));
        add(lblMessage);

        lblIpPrompt   = new JLabel("", SwingConstants.RIGHT);
        lblPortPrompt = new JLabel("", SwingConstants.RIGHT);
        lblIpPrompt.setFont(new Font("Georgia", Font.PLAIN, 18));
        lblPortPrompt.setFont(new Font("Georgia", Font.PLAIN, 18));
        add(lblIpPrompt);  add(lblPortPrompt);

        txtIp   = new JTextField();
        txtPort = new JTextField();
        add(txtIp); add(txtPort);

        btnConnecter = new BoutonArrondi(
                estFrancais ? "Se connecter" : "Connect",
                theme.getCouleurBoutonNormal(),
                theme.getCouleurBoutonSurvol(),
                theme.getCouleurBoutonPression(),
                theme.getCouleurBoutonConteur()
        );
        btnConnecter.setFont(new Font("Georgia", Font.BOLD, 20));

        btnConnecter.addActionListener(e -> {
            if (estClient) {
                collecteurEvenements.clic_bouton_se_connecter();
            } else {
                collecteurEvenements.clic_bouton_heberger();
            }
        });


        add(btnConnecter);

        lblIpInfo   = new JTextField("", SwingConstants.CENTER);
        lblHostInfo = new JTextField("", SwingConstants.CENTER);
        for (JTextField l : new JTextField[]{lblIpInfo, lblHostInfo}) {
            l.setFont(new Font("Georgia", Font.PLAIN, 20));
            l.setOpaque(true);
            l.setBackground(new Color(0, 0, 0, 210));
            l.setForeground(Color.WHITE);
            add(l);
        }
        txtPort.addActionListener(e -> btnConnecter.doClick());
        txtIp.addActionListener(e -> btnConnecter.doClick());

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override public void componentResized(java.awt.event.ComponentEvent e) {
                positionner();
            }
        });
    }

    // AFFICHAGE
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (imageFond != null) {
            g.drawImage(imageFond, 0, 0,
                    getWidth(), getHeight(),
                    (ImageObserver) null);
        }
    }

    private void positionner() {
        int w = getWidth(), h = getHeight();
        if (w <= 0 || h <= 0) return;

        int cy = h / 2 - 80;
        lblMessage.setBounds(30, cy - 60, w - 40, 40);

        if (!estClient) {  // mode serveur
            lblIpInfo  .setBounds(w/2 - 150, cy     , 300, 35);
            lblHostInfo.setBounds(w/2 - 150, cy + 45, 300, 35);

            lblIpInfo.setVisible(true);
            lblHostInfo.setVisible(true);
            lblIpPrompt.setVisible(false);
            lblPortPrompt.setVisible(false);
            txtIp.setVisible(false);
            txtPort.setVisible(false);
            btnConnecter.setVisible(false);

        } else {  // mode client
            lblIpInfo.setVisible(false);
            lblHostInfo.setVisible(false);
            lblIpPrompt.setVisible(true);
            lblPortPrompt.setVisible(true);
            txtIp.setVisible(true);
            txtPort.setVisible(true);
            btnConnecter.setVisible(true);

            lblIpPrompt .setBounds(w/2 - 200, cy , 120, 35);
            txtIp .setBounds(w/2 -  70, cy , 270, 35);
            lblPortPrompt.setBounds(w/2 - 200, cy + 45, 120, 35);
            txtPort .setBounds(w/2 -  70, cy + 45, 270, 35);
            btnConnecter.setBounds(w/2 - 30, cy +130, 200, 45);
        }
    }

    private void updateText() {
        if (estClient) {
            lblMessage  .setText(estFrancais ?
                    "Veuillez saisir l'adresse IP et le port"
                    : "Enter IP address and port");
            lblIpPrompt .setText(estFrancais ? "Adresse IP :" : "IP Address :");
            lblPortPrompt.setText(estFrancais ? "Port :" : "Port :");
        } else {
            lblMessage.setText(estFrancais ?
                    "Informations du serveur"
                    : "Server information");
            lblIpPrompt.setText(""); lblPortPrompt.setText("");
        }
        btnConnecter.setText(estFrancais ? "Se connecter" : "Connect");
        repaint();
    }



    private void afficherInfo() {
        if (!estClient) {
            try (DatagramSocket socket = new DatagramSocket()) {
                // Se connecte à une adresse externe (pas de données envoyées)
                socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
                String ipLocale = socket.getLocalAddress().getHostAddress();

                lblIpInfo.setText("IP   : " + ipLocale);
                lblHostInfo.setText("Port : " + 7000);
            } catch (IOException e) {
                lblIpInfo.setText(estFrancais ? "Erreur IP" : "IP error");
                lblHostInfo.setText("");
            }
        }
    }


    public void changerLangue(boolean estFrancais) {
        this.estFrancais = estFrancais;
        updateText(); afficherInfo();
    }


    public void appliquerTheme(Theme theme) {
        this.theme = theme;
        try { imageFond = ImageIO.read(UCC.ouvre(theme.getImgSelectionMode())); }
        catch (IOException e) { e.printStackTrace(); }

        btnConnecter.setCouleurs(theme.getCouleurBoutonNormal(),
                theme.getCouleurBoutonSurvol(),
                theme.getCouleurBoutonPression(),
                theme.getCouleurBoutonConteur());
        for (JLabel l : new JLabel[]{lblMessage,lblIpPrompt,lblPortPrompt})
            l.setForeground(theme.getTextColor());
        repaint();
    }


    public void setEstClient(boolean estClient) {
        this.estClient = estClient;
        updateText(); afficherInfo(); positionner();
    }


    public String getIpSaisie()   { return txtIp.getText();  }

    public String getPortSaisi()  { return txtPort.getText();}

    public void afficherMessage(String msg) {
        lblMessage.setText(msg);
        repaint();
    }
}
