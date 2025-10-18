package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import criteres.CriteresBase;

public class Adolescent {
    private String name; // prénom
    private String firstName; // nom
    private String genre; // son genre
    private String birthDate; // date de naissance
    private String countryOrigin; // son pays d'origine
    private int id; // id de l'étudiant
    private static int count=1;
    private int lastCorrespondantId;
    private int forcedWith;
    Map<String, String> criteria; // les critères
    private ArrayList<Integer> forceUnpairList;

    /**
     * Constructor for csv importation
     * @param id
     * @param name
     * @param firstName
     * @param genre
     * @param birthDate
     * @param countryOrigin
     * @param GUEST_ANIMAL_ALLERGY
     * @param HOST_HAS_ANIMAL
     * @param GUEST_FOOD
     * @param HOST_FOOD
     * @param HOBBIES
     * @param PAIR_GENDER
     */
    public Adolescent(String id, String name, String firstName, String genre, String birthDate, String countryOrigin, String GUEST_ANIMAL_ALLERGY, String HOST_HAS_ANIMAL, String GUEST_FOOD, String HOST_FOOD, String HOBBIES, String PAIR_GENDER, String HISTORY) {
        this.id = Integer.parseInt(id);
        count=this.id+1;
        this.name=name;
        this.firstName=firstName;
        this.genre = genre;
        this.birthDate = birthDate;
        this.countryOrigin = countryOrigin;
        this.criteria=setupCriteria();
        this.getCriteria().put("GUEST_ANIMAL_ALLERGY", GUEST_ANIMAL_ALLERGY); // ALLERGIE GUEST
        this.getCriteria().put("HOST_HAS_ANIMAL", HOST_HAS_ANIMAL); // ANIMAL HOST
        this.getCriteria().put("GUEST_FOOD", GUEST_FOOD); // ANIMAL HOST
        this.getCriteria().put("HOST_FOOD", HOST_FOOD); // ANIMAL HOST
        this.getCriteria().put("HOBBIES", HOBBIES); // ANIMAL HOST
        this.getCriteria().put("PAIR_GENDER", PAIR_GENDER); // ANIMAL HOST
        this.getCriteria().put("HISTORY", HISTORY); // HISTORY PREFERENCES
        this.forcedWith=0;
        forceUnpairList=new ArrayList<>();
    }

    /**
     * Constructeur d'un adolescent
     * 
     * @param name
     * @param firstName
     * @param genre
     * @param birthDate
     * @param countryOrigin
     */
    public Adolescent(String name, String firstName, String genre, String birthDate, String countryOrigin) {
        this(name, firstName, genre, birthDate, countryOrigin, new HashMap<>());
    }

    /**
     * Constructeur d'un adolescent
     * 
     * @param name
     * @param firstName
     * @param genre
     * @param birthDate
     * @param countryOrigin
     * @param criteria
     */
    public Adolescent(String name, String firstName, String genre, String birthDate, String countryOrigin,
            Map<String, String> criteria) {
        this.name = name;
        this.firstName = firstName;
        this.genre = genre;
        this.birthDate = birthDate;
        this.countryOrigin = countryOrigin;
        this.id = count++;
        this.criteria = setupCriteria();
        this.criteria.putAll(criteria);
        forceUnpairList=new ArrayList<>();
    }

    /**
     * Modifie un critère
     * 
     * @param newCriteria le/les critère/s à modifier
     * @return boolean si oui ou non le critère a été modifé.
     */
    public boolean setCriteria(Map<String, String> newCriteria) {
        if (newCriteria.isEmpty()) {
            return false;
        }
        boolean modified = false;
        for (Map.Entry<String, String> entry : newCriteria.entrySet()) {
            String key = entry.getKey();
            // si ce n'est pas dans les critères de base alors on ajoute pas 
            if(!CriteresBase.criteres.containsKey(key)) {
                continue;
            }
            String value = entry.getValue();
            if (!value.equals(this.criteria.get(key))) {
                this.criteria.put(key, value);
                modified = true;
            }
        }
        return modified;
    }

    public static Map<String, String> setupCriteria() {
        Map<String, String> res = new LinkedHashMap<>();
        Set<String> set = CriteresBase.criteres.keySet();
        for(String s: set) {
            res.put(s, "");
        }
        return res;
    }

    @Override
    public String toString() {
        return "[id=" + this.id +", last="+ this.lastCorrespondantId+"Forced with:"+this.getForcedWith()+"]";
    }

    public String toCsv() { // id;name;firstName;genre;birthDate;countryOrigin;GUEST_ANIMAL_ALLERGY;HOST_HAS_ANIMAL;GUEST_FOOD;HOST_FOOD;HOBBIES;PAIR_GENDER
        String[] fields = {
            String.valueOf(id),
            name,
            firstName,
            genre,
            birthDate,
            countryOrigin,
            criteria.getOrDefault("GUEST_ANIMAL_ALLERGY", ""),
            criteria.getOrDefault("HOST_HAS_ANIMAL", ""),
            criteria.getOrDefault("GUEST_FOOD", ""),
            criteria.getOrDefault("HOST_FOOD", ""),
            criteria.getOrDefault("HOBBIES", ""),
            criteria.getOrDefault("PAIR_GENDER", ""),
            criteria.getOrDefault("HISTORY", ""),
        };
        return String.join(";", fields);
    }

    public void setLastCorrespondantId(int lastCorrespondantId) {
        this.lastCorrespondantId = lastCorrespondantId;
    }

    public void setForceWith(Adolescent ado) {
        this.forcedWith=ado.getId();
    }

    public void setUnpair() {
        this.forcedWith=0;
    }

    /**
     * Si la personne est forcé à ne pas être en pair avec un autre.
     * @return
     */
    public boolean isForceUnpaired() {
        if(this.forceUnpairList.isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     * Retrourne si l'étudiant est forcé de ne pas être ensemble avec un autre étudiant donné.
     * @param other
     * @return
     */
    public boolean isForceUnpairedWith(Adolescent other) {
        if(this.forceUnpairList.contains(other.getId())) {
            return true;
        }
        return false;
    }

    /**
     * Enlève tous les adolescents de la liste de forcage de désaparaiement.
     */
    public void clearForcedUnpairList() {
        this.forceUnpairList.clear();
    }

    // GETTERS
    public String getCountryOrigin() {
        return countryOrigin;
    }

    public Map<String, String> getCriteria() {
        return criteria;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getGenre() {
        return genre;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String getBirthDate() {
        return birthDate;
    }
    public int getLastCorrespondantId() {
        return this.lastCorrespondantId;
    }
    public String getNameAndFirstName() {
        return name + " " + firstName;
    }
    public int getForcedWith() {
        return forcedWith;
    }

    public static Adolescent getAdoById(List<Adolescent> list, int id) {
        for (Adolescent ado : list) {
            if (ado.getId() == id) {
                return ado;
            }
        }
        return null;
    }

    public ArrayList<Integer> getForceUnpairList() {
        return forceUnpairList;
    }
}