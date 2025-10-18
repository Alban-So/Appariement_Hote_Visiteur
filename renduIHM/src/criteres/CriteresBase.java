package criteres;

import java.util.LinkedHashMap;
import java.util.Map;

public class CriteresBase {
    public static final Map<String, CritereType> criteres = new LinkedHashMap<>();

    static {
        // Contraintes rédhibitoires (booléens)
        criteres.put("GUEST_ANIMAL_ALLERGY", CritereType.B);
        criteres.put("HOST_HAS_ANIMAL"     , CritereType.B);
        
        // Régimes alimentaires rédhibitoires (food)
        criteres.put("GUEST_FOOD"           , CritereType.F);
        criteres.put("HOST_FOOD"            , CritereType.F);
        
        // Préférences et autres (texte)
        criteres.put("HOBBIES"              , CritereType.T);
        criteres.put("PAIR_GENDER"          , CritereType.G);
        criteres.put("HISTORY"              , CritereType.T);
    }


}
