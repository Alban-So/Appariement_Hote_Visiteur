package main;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Crées des objets de Pairing pour établire les paires selon les critères.
 */
public class PairingEngine {
    static int POINTS_GENDER = 20; // VALEURS A MODIFIER SELON LES PREFS SUR L'APP
    static int POINTS_HOBBIES = 10;
    static int POINTS_AGE = 30;
    static int POINTS_HISTORY = 30;

    private static List<Pair> currentPairs = new ArrayList<>();

    public static List<Pair> getCurrentPairs() {
        return currentPairs;
    }

    public static void setCurrentPairs(List<Pair> pairs) {
        currentPairs = pairs;
    }

    /**
     * Calcule le score total en fonction de tous critères.
     * 
     * @param host
     * @param guest
     * @return Retourne -1 si incompatible ou le score si compatible
     */
    public static int scoreAffinite(Adolescent host, Adolescent guest) {
        int score = 0;
        if(isForced(host, guest)) {
            return 2000;
        }
        if(isForcedWithSomeone(host) || isForcedWithSomeone(guest)) {
            return -1;
        }
        score += getAffiniteGender(host, guest);
        score += getAffiniteHobbies(host, guest);
        score += getAffiniteAge(host, guest);
        score += getAffiniteHistory(host, guest);

        if(isIncompatible(host, guest)) {
            return -1;
        }

        return score; // Retourner le score final
    }

    /**
     * Calcule l'affinité en rapport au critère de genre.
     * 
     * @param host
     * @param guest
     * @return score d'affinité
     */
    public static int getAffiniteGender(Adolescent host, Adolescent guest) {
        int score = 0;
        if(guest.getCriteria().get("PAIR_GENDER")==null) return 0;
        
        if (guest.getCriteria().get("PAIR_GENDER").equalsIgnoreCase(host.getGenre())) {
            score += POINTS_GENDER;
        }
        if (host.getCriteria().get("PAIR_GENDER").equalsIgnoreCase(guest.getGenre())) {
            score += POINTS_GENDER;
        }
        return score;
    }

    /**
     * Calcule le score d'affinité entre deux adolescents en fonction de l'historique
     * @param host
     * @param guest
     * @return
     */
    public static int getAffiniteHistory(Adolescent host, Adolescent guest) {
        if(host.getLastCorrespondantId()!=guest.getId()) {
            return 0;
        }
        if(host.getCriteria().get("HISTORY").equals("same") && guest.getCriteria().get("HISTORY").equals("same")) {
            return 1000;
        }
        return POINTS_HISTORY;
    }

    /**
     * Calcule le score d'affinité entre deux adolescents en fonction de leurs
     * loisirs.
     * Le score est déterminé en comparant les loisirs de l'hôte et de l'invité.
     * Chaque loisir correspondant ajoute 10 points au score.
     *
     * @param host  L'adolescent hôte dont les loisirs seront comparés.
     * @param guest L'adolescent invité dont les loisirs seront comparés.
     * @return n*10 Soit n le nombre de hobbies en commun.
     */
    public static int getAffiniteHobbies(Adolescent host, Adolescent guest) {
        String hostHobbies = host.getCriteria().get("HOBBIES");
        String guestHobbies = guest.getCriteria().get("HOBBIES");
        if (hostHobbies == null || guestHobbies == null) {
            return 0;
        }
        Set<String> hostSet = new HashSet<>(Arrays.asList(hostHobbies.split(",")));
        Set<String> guestSet = new HashSet<>(Arrays.asList(guestHobbies.split(",")));
        hostSet.retainAll(guestSet);
        int matches = hostSet.size();
        return matches * POINTS_HOBBIES;
    }

    /**
     * Calcule l'affinité en fonction de l'âge.
     * Vérifie si la différence d'âge entre l'hôte et l'invité est de 2 ans ou
     * moins.
     *
     * @param host  L'adolescent hôte.
     * @param guest L'adolescent invité.
     * @return 30 si la différence d'âge est de 2 ans ou moins, sinon 0.
     */
    public static int getAffiniteAge(Adolescent host, Adolescent guest) {
        if (host == null || guest == null)
            return 0;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy"); // Patèrne pour lire la date
        LocalDate hostBirthDate = LocalDate.parse(host.getBirthDate(), formatter);
        LocalDate guestBirthDate = LocalDate.parse(guest.getBirthDate(), formatter);
        int ageDifference = hostBirthDate.until(guestBirthDate).getYears();
        if (ageDifference > -2 && ageDifference < 2) {
            return POINTS_AGE;
        }
        return 0;
    }

    /**
     * Execute toutes les fonctions de vérification de rédhibitions
     * 
     * @return si un lien est possible entre les deux adolescent
     */
    public static boolean isIncompatible(Adolescent host, Adolescent guest) {
        boolean isProblem = false;
        if (isAnimalProblem(host, guest)) {
            isProblem = true;
        } else if (!isProblem && isCountryProblem(host, guest)) {
            isProblem = true;
        } else if(!isProblem && isFoodProblem(host, guest)) {
            isProblem = true;
        } else if(!isProblem && isHistoryProblem(host, guest)){
            isProblem = true;
        }else if(!isProblem && isForceUnpairedProblem(host, guest)){
            isProblem = true;
        }
        return isProblem;
    }

    /**
     * Regarde si les deux étudiants ne sont pas forcé à ne pas être ensemble.
     * @param host
     * @param guest
     * @return
     */
    public static boolean isForceUnpairedProblem(Adolescent host, Adolescent guest) {
        if(host.isForceUnpairedWith(guest) || guest.isForceUnpairedWith(host)) {
            return true;
        }
        return false;
    }

    public static boolean isHistoryProblem(Adolescent host, Adolescent guest) {
        if(host.getLastCorrespondantId()!=guest.getId()) {
            return false;
        }
        if(host.getCriteria().get("HISTORY").equals("other") || guest.getCriteria().get("HISTORY").equals("other")) {
            return true;
        }
        return false;
    }

    public static boolean isAnimalProblem(Adolescent host, Adolescent guest) {
        if(host.getCriteria().get("HOST_HAS_ANIMAL").equals("yes") && guest.getCriteria().get("GUEST_ANIMAL_ALLERGY").equals("yes")) {
            return true;
        }
        return false;
    }

    public static boolean isCountryProblem(Adolescent host, Adolescent guest) {
        String hostCountry = host.getCountryOrigin();
        String guestCountry = guest.getCountryOrigin();
        // si deux français pas de problèmes
        if(hostCountry.equals("France") && guestCountry.equals("France")) {
            return false;
        }
        // si un des deux est français alors au moins un hobbie en commun
        if(hostCountry.equals("France") || guestCountry.equals("France")) {
            if(getAffiniteHobbies(host, guest)<POINTS_HOBBIES) {
                return true;
            }
        }
        // si aucun français alors pas de problème
        return false;
    }

    public static boolean isFoodProblem(Adolescent host, Adolescent guest) {
        // créer les listes avec les critères
        List<String> hostList = new ArrayList<>();
        for(String s: host.getCriteria().get("HOST_FOOD").split(",")) {
            hostList.add(s);
        }

        List<String> guestList = new ArrayList<>();
        for(String s: guest.getCriteria().get("GUEST_FOOD").split(",")) {
            guestList.add(s);
        }
        // si il n'y a aucun éléments dans la liste alors aucune allergie
        if(guestList.size() == 1 && guestList.get(0) == "") {
            return false;
        }
        // si tous les attribus sont dans la liste de l'host tout va bien
        if(guestList.containsAll(hostList)) {
            return false;
        }
        // sinon problème
        return true;
    }

    /**
     * Renvoi les pair selon l'algorithme Hongrois du couplage optimal et maximum.
     * Ce qui donne donc les apparaiments parfaits pour chaque host/guest.
     * Le but ici est d'avoir la somme des scores d'affinité de chaque pair et que
     * cette somme soit maximale.
     * 
     * @param hosts
     * @param guests
     * @return Optimal Pairings
     */
    public static List<Pair> generateOptimalPairs(List<Adolescent> hosts, List<Adolescent> guests) {
        return AlgorithmEngine.executeMatchingAlgorithm(hosts, guests);
    }

    /**
     * Renvoi les pair selon l'algorithme Hongrois du couplage optimal et maximum.
     * Ce qui donne donc les apparaiments parfaits pour chaque host/guest.
     * Le but ici est d'avoir la somme des scores d'affinité de chaque pair et que
     * cette somme soit maximale.
     * 
     * @param pairs
     * @return Optimal Pairings
     */
    public static List<Pair> generateOptimalPairs(List<Pair> pairs) {
        return AlgorithmEngine.executeMatchingAlgorithm(pairs);
    }

    /**
     * Forcer deux étudiants ensemble.
     * @param host
     * @param guest
     */
    public static void forcePair(Adolescent host, Adolescent guest) {
    host.setForceWith(guest);
    guest.setForceWith(host);

    Pair newPair = new Pair(host, guest);

    // Supprime une ancienne paire si elle existait déjà
    currentPairs.removeIf(p -> p.getHost().equals(host) || p.getGuest().equals(guest));

    currentPairs.add(newPair);
}


    /**
     * Enlever un binome forcé.
     * @param host
     * @param guest
     */
    public static void forceUnpair(Adolescent host, Adolescent guest) {
        host.setUnpair();
        guest.setUnpair();

        currentPairs.removeIf(p -> 
            (p.getHost().equals(host) && p.getGuest().equals(guest)) ||
            (p.getHost().equals(guest) && p.getGuest().equals(host))
        );
    }


    /**
     * Retourne so oui ou non l'utilisateur est forcé avec un autre.
     * @param host
     * @param guest
     * @return
     */
    public static boolean isForced(Adolescent host, Adolescent guest) {
        if(host.getForcedWith()==guest.getId() && guest.getForcedWith()==host.getId()) {
            return true;
        }
        return false;
    }

    /**
     * Vérifie si la personne est forcé avec un autre.
     * @param a
     * @return
     */
    public static boolean isForcedWithSomeone(Adolescent a) {
        if(a.getForcedWith()!=0) {
            return true;
        }
        return false;
    }

    /**
     * Force les deux personnes à ne pas être ensemble.
     * @param host
     * @param guest
     */
    public static void forceUnpairing(Adolescent host, Adolescent guest) {
        if(!host.getForceUnpairList().contains(guest.getId())) {
            host.getForceUnpairList().add(guest.getId());
        }
        if(!guest.getForceUnpairList().contains(host.getId())) {
            guest.getForceUnpairList().add(host.getId());
        }
    }
}
