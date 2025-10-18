package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;

import criteres.CriteresBase;
import exceptions.NotRecognizedFoodAllergy;
import exceptions.NotRecognizedGender;

/**
 * CSVHandler permets de sauvegarder et lire des fichiers. L'importation passera
 * par cette classe.
 * 
 * @short Fonctions utilitaires aux CSV.
 */
public class CSVHandler {

    /**
     * Vérifie les données, d'un fichier mis en paramètre.
     * 
     * @NOTE Ne pas oublier d'ajouter un nouveau critère créé en dur ici ou il se
     *       supprimera.
     * @param criteria
     * @throws IOException
     * @throws NotRecognizedGender
     * @throws NotRecognizedFoodAllergy
     */
    public static boolean verifyCSV(File file) throws IOException, NotRecognizedGender, NotRecognizedFoodAllergy {
        // Compteur pour message personnalisés avec la ligne de l'erreur
        int lineCounter = 1;
        // Récupérer les data
        String data = null;

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String headerLine = br.readLine();
            String[] headers = headerLine.split(";");

            while ((data = br.readLine()) != null) {
                String[] values = data.split(";");
                for (int i = 6; i < headers.length-1; i++) {
                    String current = values[i];
                    String header = headers[i];
                    switch (CriteresBase.criteres.get(header)) {
                        case B:
                            if (!current.equals("yes") && !current.equals("no")) {
                                throw new InputMismatchException(
                                        "No a valid boolean like 'yes' or 'no' at line " + lineCounter + " '" + current
                                                + "'" + header);
                            }
                            break;
                        case T:
                            // rien à faire
                            break;
                        case G:
                            if (!current.equals("male") && !current.equals("female")) {
                                throw new NotRecognizedGender(
                                        "Gender invalid at line " + lineCounter + " '" + current + "'" + header);
                            }
                            break;
                        case F:
                            List<String> regimes = Arrays.asList("nonuts", "vegetarian");
                            if (current.equals(""))
                                break;
                            for (String s : current.split(",")) {
                                if (!regimes.contains(s)) {
                                    throw new NotRecognizedFoodAllergy("Food criteria invalid at line " + lineCounter
                                            + " '" + current + "'" + header);
                                }
                            }
                            break;
                        default:
                            break;
                    }
                }
                lineCounter++;
            }
            br.close();
        } catch (InputMismatchException e) {
            throw e;
        } catch (NotRecognizedGender e) {
            throw e;
        } catch (NotRecognizedFoodAllergy e) {
            throw e;
        } catch (IOException e) {
            throw e;
        }
        return true;
    }

    /**
     * Importer des adolescents depuis un CSV
     * @param cheminFichier
     * @return
     */
    public static ArrayList<Adolescent> importAdolescents(String cheminFichier) {
        try {
            if(!verifyCSV(new File(cheminFichier))) {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("L'imporation a échoué, veuillez corriger les critères");
        }

        ArrayList<Adolescent> adolescents = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(cheminFichier)));
            String data;
            br.readLine(); // passer le header
            while((data=br.readLine())!=null) {
                String[] line = data.split(";", -1);
                Adolescent ado = new Adolescent(line[0], line[1], line[2], line[3], line[4], line[5], line[6], line[7], line[8], line[9], line[10], line[11], line[12]);
                // id;name;firstName;genre;birthDate;countryOrigin;GUEST_ANIMAL_ALLERGY;HOST_HAS_ANIMAL;GUEST_FOOD;HOST_FOOD;HOBBIES;PAIR_GENDER;HISTORY
                adolescents.add(ado);
            }
            br.close();
        } catch (FileNotFoundException e) {
            System.err.println("File "+cheminFichier+" not found.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return adolescents;
    }

    /**
     * Exporter des adolescents dans un CSV.
     * @param adolescents
     * @param cheminFichier
     * @throws IOException
     */
    public static void exportAdolescent(ArrayList<Adolescent> adolescents, String cheminFichier) throws IOException {
        try {
            File file = new File(cheminFichier);
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write("id;name;firstName;genre;birthDate;countryOrigin");
            for(String s: CriteresBase.criteres.keySet()) {
                bw.write(";"+s);
            }
            for(Adolescent a: adolescents) {
                bw.newLine();
                bw.write(a.toCsv());
            }
            bw.close();
        } catch (IOException e) {
            throw e;
        }
    }

    /**
     * Importe les adolescents au lancement.
     * À utiliser au début.
     * @param hostFileName
     * @param guestFileName
     * @return
     */
    public static ArrayList<Adolescent> startup(String fileName) {
        File file = new File(System.getProperty("user.dir")+File.separator+"res"+File.separator+"data"+File.separator+fileName);
        if(file.exists()) {
            return importAdolescents(file.getPath());
        }
        return null;
    }
}
