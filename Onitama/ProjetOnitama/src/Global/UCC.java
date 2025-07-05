package Global;

import java.io.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.System.exit;

public class UCC {
    static UCC ucc = null;
    Properties prop;
    Logger logger;
    boolean mode_test;
    public static UCC instance(){
        if(ucc == null) ucc = new UCC();
        return ucc;
    }

    static void chargerProprietes(Properties p, InputStream in, String nom) {

        try {
            p.load(in);
        } catch (IOException e) {
            // Le logger n'est pas encore en place à ce moment là
            System.err.println("Impossible de charger " + nom);
            System.err.println(e);
            System.exit(1);
        }
    }


    public UCC(){
        InputStream in=ouvre("default.cfg");
        Properties defaut=new Properties();
        chargerProprietes(defaut, in, "defaut.cfg");
        String message = "Fichier de propriétés defaut.cfg chargé";
        String nom = System.getProperty("user.home") +"/Onitama/.Onitama"; // chnagé avec le nom du répertoire
        try{
            in=new FileInputStream(nom);
            prop=new Properties(defaut);
            chargerProprietes(prop, in, nom);
            String level=prop.getProperty("LogLevel");
            logger().info(message);
            logger().info("FIchier de configuration "+nom+" chargé");

        }catch(FileNotFoundException e){
            prop=defaut;
            String level=prop.getProperty("LogLevel");
            logger().info(message);
        }

    }

    public String lis(String nom){
        String value = prop.getProperty(nom);
        if(value==null) throw new RuntimeException("Propriété " + nom + " manquante");
        return value;
    }

    public static String lisString(String nom){
        return instance().lis(nom);
    }

    public static int lisInt(String nom){
        return Integer.parseInt(instance().lis(nom));
    }

    public static float lisFloat(String nom){
        return Float.parseFloat(instance().lis(nom));
    }

    public static double lisDouble(String nom){
        return Double.parseDouble(instance().lis(nom));
    }

    public static boolean lisBoolean(String nom){
        return Boolean.parseBoolean(instance().lis(nom));
    }

    public static InputStream ouvre(String file_name){
        InputStream in=ClassLoader.getSystemClassLoader().getResourceAsStream(file_name);
        if(in==null){
            System.err.println("impossible to open file: " + file_name);
            exit(1);
        }
        return in;
    }

    public Logger logger(){
        if(logger==null){
            System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s : %5$s%n");
            logger = Logger.getLogger("UCC");
            logger.setLevel(Level.parse(lis("LogLevel")));
        }
        return logger;
    }

    public static void info(String s) {
        if(instance().mode_test) return;
        instance().logger().info(s);
    }

    public static void alerte(String s) {
        if(instance().mode_test) return;
        instance().logger().warning(s);
    }

    public static void erreur(String s) {
        if(instance().mode_test) return;
        instance().logger().severe(s);

        System.exit(1);
    }

    public static void debug(String s) {
        if(instance().mode_test) return;
        instance().logger().info("DEBUG: " + s);
    }

    public static void setMode_test(boolean mode_test) {
        instance().mode_test = mode_test;
    }
}

