package test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import main.Adolescent;
import main.CSVHandler;
import main.HistoricManager;
import main.Pair;
import main.PairingEngine;

public class AdolescentTest {

    public static void main(String[] args) {
        ArrayList<Adolescent> hosts = CSVHandler.importAdolescents("./res/data/adoHost.csv");
        ArrayList<Adolescent> guests = CSVHandler.importAdolescents("./res/data/adoGuest.csv");

        List<Pair> pairs = PairingEngine.generateOptimalPairs(hosts, guests);
        System.out.println(pairs+"\n\n----------------PART 2----------------");

        

        try {
            CSVHandler.exportAdolescent(hosts, "./res/data/testExportHosts.csv");
            HistoricManager.sauvegarderHistorique(pairs, "./res/data/savedHistoric.csv");


            hosts = CSVHandler.importAdolescents("./res/data/second/adoHost.csv");
            guests = CSVHandler.importAdolescents("./res/data/second/adoGuest.csv");

            
            Adolescent hostToForce = Adolescent.getAdoById(hosts, 17);
            Adolescent guestToForce = Adolescent.getAdoById(guests, 11);
            

            Adolescent hostToForce2 = Adolescent.getAdoById(hosts, 10);
            Adolescent guestToForce2 = Adolescent.getAdoById(guests, 18);

            PairingEngine.forcePair(hostToForce, guestToForce);
            PairingEngine.forcePair(hostToForce2, guestToForce2);


            System.out.println(hostToForce + " OSKOUR " + guestToForce);

            // Force la paire
            // Applique l'historique si besoin
            HistoricManager.appliquerHistorique(PairingEngine.generateOptimalPairs(hosts, guests), "./res/data/savedHistoric.csv");

            // Génère les paires
            pairs = PairingEngine.generateOptimalPairs(hosts, guests);

            System.out.println(pairs);

            System.out.println("\n\n\n-----\n");
            ArrayList<Adolescent> chost = new ArrayList<>();
            ArrayList<Adolescent> cguest = new ArrayList<>();
            Adolescent ado1 = new Adolescent(
                "21",
                "Delcourt",
                "Adrien",
                "male",
                "12/06/2006",
                "Belgium",
                "no",
                "no",
                "vegetarian",
                "nonuts",
                "cinema,football",
                "female",
                "other"
            );

            Adolescent ado2 = new Adolescent(
                "22",
                "Weber",
                "Camille",
                "female",
                "25/09/2005",
                "Belgium",
                "yes",
                "no",
                "",
                "vegetarian",
                "reading,music",
                "male",
                "other"
            );

            ado1.setLastCorrespondantId(22);
            ado2.setLastCorrespondantId(21);
            PairingEngine.forcePair(ado1, ado2);

            chost.add(ado1);
            cguest.add(ado2);
            System.out.println(ado1);
            System.out.println(ado2);

            pairs = PairingEngine.generateOptimalPairs(chost, cguest);
            System.out.println(pairs);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}