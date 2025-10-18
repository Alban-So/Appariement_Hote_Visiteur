package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HistoricManager {
    public static void sauvegarderHistorique(List<Pair> pairs, String cheminFichier) throws IOException {
        try {
            File file = new File(cheminFichier);
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write("idHost;idGuest");
            List<Integer> addedAdo = new ArrayList<>();
            for (Pair p : pairs) {
                bw.newLine();
                int idHost = p.getHost().getId();
                int idGuest = p.getGuest().getId();
                addedAdo.add(idHost);
                addedAdo.add(idGuest);
                bw.write(idHost + ";" + idGuest);
            }
            bw.close();
        } catch (IOException e) {
            throw e;
        }
    }

    public static void appliquerHistorique(List<Pair> pairs, String cheminFichier)
            throws FileNotFoundException, IOException {
        try {
            // Récupère tous les adolescents uniques présents dans les pairs
            List<Adolescent> allAdos = new ArrayList<>();
            for (Pair p : pairs) {
                if (!allAdos.contains(p.getHost()))
                    allAdos.add(p.getHost());
                if (!allAdos.contains(p.getGuest()))
                    allAdos.add(p.getGuest());
            }

            File file = new File(cheminFichier);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String data;
            br.readLine();
            while ((data = br.readLine()) != null) {
                String[] ids = data.split(";");
                int idHost = Integer.parseInt(ids[0]);
                int idGuest = Integer.parseInt(ids[1]);
                for (Adolescent ado : allAdos) {
                    if (ado.getId() == idHost) {
                        ado.setLastCorrespondantId(idGuest);
                    } else if (ado.getId() == idGuest) {
                        ado.setLastCorrespondantId(idHost);
                    }
                }
            }
            br.close();
        } catch (IOException e) {
            throw e;
        }
    }
}
