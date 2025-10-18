package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;



public class AdolescentManager {
    ArrayList<Adolescent> affectedAdolescents = new ArrayList<Adolescent>();
    ArrayList<Adolescent> unaffectedAdolescents = new ArrayList<Adolescent>();
    List<Pair> pairs = new ArrayList<>();


     /**
     * Supprime les adolescents présentant des incohérences dans leurs critères :
     *  - Valeur non-yes/no pour les critères booléens
     *  - Déclaration d'allergie aux animaux (GUEST_ANIMAL_ALLERGY = yes) ET possession d'un animal (HOST_HAS_ANIMAL = yes)
     *
     * @param ados Liste initiale d'adolescents
     * @return Nouvelle liste sans incohérences
     */
    public static ArrayList<Adolescent> supprimerIncoherents(ArrayList<Adolescent> ados) {
        ArrayList<Adolescent> valides = new ArrayList<Adolescent>();
        for (Adolescent ado : ados) {
            Map<String, String> crit = ado.getCriteria();
            String hasAnimal=crit.get("HOST_HAS_ANIMAL");
            String animalAlergy=crit.get("GUEST_ANIMAL_ALLERGY");
            if(!hasAnimal.equals("yes") && !animalAlergy.equals("yes")) {
                valides.add(ado);
            }
        }
        return valides;
    }
}
