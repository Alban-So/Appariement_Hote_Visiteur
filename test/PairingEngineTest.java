package test;

import org.junit.jupiter.api.Test;

import main.Adolescent;
import main.Pair;
import main.PairingEngine;

import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

class PairingEngineTest {
    private Adolescent createAdolescent(String genre, String birthDate, String pairGender, String hobbies) {
        Map<String, String> criteria = new HashMap<>();
        criteria.put("PAIR_GENDER", pairGender);
        criteria.put("HOBBIES", hobbies);
        return new Adolescent("A", "B", genre, birthDate, "France", criteria);
    }

    private Adolescent createAdolescent(String country, String GUEST_FOOD, String HOST_FOOD,
            String GUEST_ANIMAL_ALLERGY, String HOST_HAS_ANIMAL) {
        Map<String, String> criteria = new HashMap<>();
        criteria.put("GUEST_FOOD", GUEST_FOOD);
        criteria.put("HOST_FOOD", HOST_FOOD);
        criteria.put("GUEST_ANIMAL_ALLERGY", GUEST_ANIMAL_ALLERGY);
        criteria.put("HOST_HAS_ANIMAL", HOST_HAS_ANIMAL);

        return new Adolescent("A", "B", "male", "01/01/2008", country, criteria);
    }

    @Test
    void testGetAffiniteGender_Match() {
        Adolescent host = createAdolescent("female", "01/01/2008", "male", "music,sport");
        Adolescent guest = createAdolescent("male", "01/01/2008", "female", "music,reading");
        int score = PairingEngine.getAffiniteGender(host, guest);
        assertEquals(40, score);
    }

    @Test
    void testGetAffiniteGender_NoMatch() {
        Adolescent host = createAdolescent("female", "01/01/2008", "female", "music,sport");
        Adolescent guest = createAdolescent("male", "01/01/2008", "male", "music,reading");
        int score = PairingEngine.getAffiniteGender(host, guest);
        assertEquals(0, score);
    }

    @Test
    void testGetAffiniteHobbies_SomeMatch() {
        Adolescent host = createAdolescent("female", "01/01/2008", "male", "music,sport");
        Adolescent guest = createAdolescent("male", "01/01/2008", "female", "music,reading");
        int score = PairingEngine.getAffiniteHobbies(host, guest);
        assertEquals(10, score);
    }

    @Test
    void testGetAffiniteHobbies_AllMatch() {
        Adolescent host = createAdolescent("female", "01/01/2008", "male", "music,sport");
        Adolescent guest = createAdolescent("male", "01/01/2008", "female", "music,sport");
        int score = PairingEngine.getAffiniteHobbies(host, guest);
        assertEquals(20, score);
    }

    @Test
    void testGetAffiniteHobbies_NoMatch() {
        Adolescent host = createAdolescent("female", "01/01/2008", "male", "music,sport");
        Adolescent guest = createAdolescent("male", "01/01/2008", "female", "reading,painting");
        int score = PairingEngine.getAffiniteHobbies(host, guest);
        assertEquals(0, score);
    }

    @Test
    void testGetAffiniteAge_Within2Years() {
        Adolescent host = createAdolescent("female", "01/01/2008", "male", "music,sport");
        Adolescent guest = createAdolescent("male", "01/01/2009", "female", "music,reading");
        int score = PairingEngine.getAffiniteAge(host, guest);
        assertEquals(30, score);
    }

    @Test
    void testGetAffiniteAge_Exactly2YearsApart() {
        Adolescent host = createAdolescent("female", "01/01/2008", "male", "music,sport");
        Adolescent guest = createAdolescent("male", "01/01/2010", "female", "music,reading");
        int score = PairingEngine.getAffiniteAge(host, guest);
        assertEquals(0, score);
    }

    @Test
    void testGetAffiniteAge_MoreThan2YearsApart() {
        Adolescent host = createAdolescent("female", "01/01/2008", "male", "music,sport");
        Adolescent guest = createAdolescent("male", "01/01/2012", "female", "music,reading");
        int score = PairingEngine.getAffiniteAge(host, guest);
        assertEquals(0, score);
    }

    @Test
    void testScoreAffinite_AllCriteria() {
        Adolescent host = createAdolescent("female", "01/01/2008", "male", "music,sport");
        Adolescent guest = createAdolescent("male", "01/01/2009", "female", "music,sport");
        int score = PairingEngine.scoreAffinite(host, guest);
        // 40 (gender) + 20 (hobbies) + 30 (age) = 90
        assertEquals(90, score);
    }

    @Test
    void testGenerateOptimalPairs_FromAdolescents() {
        Adolescent host1 = createAdolescent("female", "01/01/2008", "male", "music,sport");
        Adolescent host2 = createAdolescent("male", "01/01/2007", "female", "reading,art");
        Adolescent guest1 = createAdolescent("male", "01/01/2009", "female", "music,sport");
        Adolescent guest2 = createAdolescent("female", "01/01/2006", "male", "reading,art");
        List<Adolescent> hosts = Arrays.asList(host1, host2);
        List<Adolescent> guests = Arrays.asList(guest1, guest2);
        List<Pair> pairs = PairingEngine.generateOptimalPairs(hosts, guests);
        assertEquals(2, pairs.size());
        assertEquals(host1, pairs.get(0).getHost());
        assertEquals(guest1, pairs.get(0).getGuest());
        assertEquals(host2, pairs.get(1).getHost());
        assertEquals(guest2, pairs.get(1).getGuest());
    }

    @Test
    void testFood_compatible_noPreferences() {
        Adolescent host = createAdolescent("France", "", "nonuts", "", "");
        Adolescent guest = createAdolescent("France", "", "", "", "");
        assertFalse(PairingEngine.isFoodProblem(host, guest));
    }

    @Test
    void testFood_compatible_withPreferences() {
        Adolescent host = createAdolescent("France", "", "nonuts", "", "");
        Adolescent guest = createAdolescent("France", "nonuts", "", "", "");
        assertFalse(PairingEngine.isFoodProblem(host, guest));
    }

    @Test
    void testFood_incompatible() {
        Adolescent host = createAdolescent("France", "", "nonuts", "", "");
        Adolescent guest = createAdolescent("France", "vegetarian", "", "", "");
        assertTrue(PairingEngine.isFoodProblem(host, guest));
    }

    @Test
    void testFood_compatible_multiplePreferences() {
        Adolescent host = createAdolescent("France", "", "vegetarian,nonuts", "", "");
        Adolescent guest = createAdolescent("France", "nonuts,vegetarian", "", "", "");
        assertFalse(PairingEngine.isFoodProblem(host, guest));
    }

    @Test
    void testCountry_compatible() {
        Adolescent host = createAdolescent("France", "", "vegetarian,nonuts", "", "");
        Adolescent guest = createAdolescent("France", "nonuts,vegetarian", "", "", "");
        assertFalse(PairingEngine.isCountryProblem(host, guest));
    }

    @Test
    void testCountry_incompatible() {
        Adolescent host = createAdolescent("France", "", "vegetarian,nonuts", "", "");
        Adolescent guest = createAdolescent("Germany", "nonuts,vegetarian", "", "", "");
        assertTrue(PairingEngine.isCountryProblem(host, guest));
    }

    @Test
    void testCountry_compatible_noFrance() {
        Adolescent host = createAdolescent("Italy", "", "vegetarian,nonuts", "", "");
        Adolescent guest = createAdolescent("Germany", "nonuts,vegetarian", "", "", "");
        assertFalse(PairingEngine.isCountryProblem(host, guest));
    }

    @Test
    void testGenerateOptimalPairs_FromPairs() {
        Adolescent host = createAdolescent("female", "01/01/2008", "male", "music,sport");
        Adolescent guest = createAdolescent("male", "01/01/2009", "female", "music,sport");
        Pair pair = new Pair(host, guest);
        List<Pair> pairs = Arrays.asList(pair);
        List<Pair> result = PairingEngine.generateOptimalPairs(pairs);
        assertEquals(1, result.size());
        assertEquals(pair, result.get(0));

        // Adolescent [name=A, criteria={HOBBIES=music,sport,
        // PAIR_GENDER=male}]<->Adolescent [name=A, criteria={HOBBIES=music,sport,
        // PAIR_GENDER=female}] : 90
        // Adolescent [name=A, criteria={HOBBIES=music,sport,
        // PAIR_GENDER=male}]<->Adolescent [name=A, criteria={HOBBIES=music,sport,
        // PAIR_GENDER=female}] : 90
    }
}