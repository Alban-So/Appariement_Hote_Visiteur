package main;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        List<Adolescent> guests = new ArrayList<>();
        List<Adolescent> hosts = new ArrayList<>();

        String[] prenoms = { "Ethan", "Léa", "Lucas", "Emma", "Noah", "Chloé" };
        String[] noms = { "Martin", "Durand", "Petit", "Moreau", "Lemoine", "Fournier" };
        String[] pays = { "France", "Belgique", "Suisse" };
        String[] hobbies = { "football", "jeux vidéo", "musique", "peinture", "natation", "basket", "lecture" };

        Random rand = new Random();

        // Générer les guests
        for (int i = 0; i < 10; i++) {
            String prenom = prenoms[rand.nextInt(prenoms.length)];
            String nom = noms[rand.nextInt(noms.length)];
            String sexe = rand.nextBoolean() ? "male" : "female";
            String dateNaissance = randomDate(rand, 2005, 2010); // entre 14 et 19 ans
            String paysOrigine = pays[rand.nextInt(pays.length)];

            
            Map<String, String> criteria = new HashMap<>();
            criteria.put("HOBBIES", generateRandomHobbies(hobbies, rand));
            criteria.put("PAIR_GENDER", rand.nextBoolean() ? "male" : "female");
            Adolescent guest = new Adolescent(prenom, nom, sexe, dateNaissance, paysOrigine, criteria);
            
            guests.add(guest);
        }

        // Générer les hosts (a1, a2, a3)
        for (int i = 0; i < 10; i++) {
            String prenom = "a" + (i + 1); // a1, a2, a3
            String nom = noms[rand.nextInt(noms.length)];
            String sexe = rand.nextBoolean() ? "male" : "female";
            String dateNaissance = randomDate(rand, 2004, 2009); // entre 15 et 20 ans
            String paysOrigine = pays[rand.nextInt(pays.length)];


            Map<String, String> criteria = new HashMap<>();
            criteria.put("HOBBIES", generateRandomHobbies(hobbies, rand));
            criteria.put("PAIR_GENDER", rand.nextBoolean() ? "male" : "female");
            Adolescent host = new Adolescent(prenom, nom, sexe, dateNaissance, paysOrigine, criteria);
            hosts.add(host);

        }

        List<Pair> pairs = PairingEngine.generateOptimalPairs(hosts, guests);
        for(Pair p: pairs) {
            System.out.println(p);
        }
        

        // Affichage
        // System.out.println("Guests:");
        // for (Adolescent a : guests)
        //     System.out.println(a);
        // System.out.println("\nHosts:");
        // for (Adolescent a : hosts)
        //     System.out.println(a);

    }

    // Génère une date au format "dd/MM/yyyy"
    public static String randomDate(Random rand, int yearMin, int yearMax) {
        int day = rand.nextInt(28) + 1;
        int month = rand.nextInt(12) + 1;
        int year = rand.nextInt(yearMax - yearMin + 1) + yearMin;
        return String.format("%02d/%02d/%d", day, month, year);
    }

    // Tire 2 ou 3 hobbies aléatoires
    public static String generateRandomHobbies(String[] allHobbies, Random rand) {
        List<String> source = new ArrayList<>(Arrays.asList(allHobbies));
        Collections.shuffle(source, rand);
        int count = 2 + rand.nextInt(2); // 2 ou 3 hobbies
        return String.join(",", source.subList(0, count));
    }
}
