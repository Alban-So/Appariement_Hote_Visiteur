package IHM;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.Adolescent;
import main.CSVHandler;
import main.Pair;
import main.PairingEngine;

import static main.PairingEngine.generateOptimalPairs;
import static main.PairingEngine.getAffiniteAge;
import static main.PairingEngine.scoreAffinite;

public class Inter extends Application {

    private BorderPane rootLayout;
    private StackPane rootContainer;

    // Stockage global des listes
    private List<Adolescent> adolescentsHote = CSVHandler.startup("adoHost.csv");
    private List<Adolescent> adolescentsVisiteur = CSVHandler.startup("adoGuest.csv");
    private List<Pair> currentPairs = new ArrayList<>();
    private List<String> hotesLibres = new ArrayList<>();
    private List<String> visiteursLibres = new ArrayList<>();

    private TextField hotePrefGenreField, visiteurPrefGenreField;

    // VBox pairsView accessible pour mise à jour
    private VBox pairsView;

    @Override
    public void start(Stage primaryStage) {
        rootLayout = new BorderPane();
        rootLayout.setPadding(new Insets(20));

        rootContainer = new StackPane(rootLayout);

        if (!adolescentsHote.isEmpty() && !adolescentsVisiteur.isEmpty()) {
            currentPairs = PairingEngine.generateOptimalPairs(adolescentsHote, adolescentsVisiteur);
        }

        // Boutons import CSV
        Button importCSV = new Button("Importer un fichier .csv pour les hôtes");
        Button importCSVs = new Button("Importer un fichier .csv pour les visiteurs");

        HBox topButtons = new HBox(10);
        topButtons.setAlignment(Pos.CENTER_LEFT);
        topButtons.getChildren().addAll(importCSV, importCSVs);
        topButtons.setPadding(new Insets(10));

        rootLayout.setTop(topButtons);
        pairsView = createPairsDisplayPanelContainer(currentPairs, adolescentsHote, adolescentsVisiteur);

        importCSV.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choisir un fichier CSV");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers CSV", "*.csv"));
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                List<Adolescent> imported = CSVHandler.importAdolescents(selectedFile.getAbsolutePath());
                if (imported != null && !imported.isEmpty()) {
                    adolescentsHote.clear();
                    adolescentsHote.addAll(imported);
                    System.out.println("Hôtes importés :");
                    adolescentsHote.forEach(System.out::println);

                    updatePairsDisplayPanel(currentPairs, adolescentsHote, adolescentsVisiteur);
                } else {
                    showAlertErreur();
                }
            }
        });

        importCSVs.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choisir un fichier CSV");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers CSV", "*.csv"));
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                List<Adolescent> imported = CSVHandler.importAdolescents(selectedFile.getAbsolutePath());
                if (imported != null && !imported.isEmpty()) {
                    adolescentsVisiteur.clear();
                    adolescentsVisiteur.addAll(imported);
                    System.out.println("Visiteurs importés :");
                    adolescentsVisiteur.forEach(System.out::println);

                    updatePairsDisplayPanel(currentPairs, adolescentsHote, adolescentsVisiteur);
                } else {
                    showAlertErreur();
                }
            }
        });

        VBox hostForm = createPersonForm("Hôte", "choisir un nouvel hôte", "hote");
        VBox visitorForm = createPersonForm("Visiteur", "choisir un nouveau visiteur", "visiteur");

        HBox centerLayout = new HBox(40, hostForm, visitorForm, pairsView);
        centerLayout.setPadding(new Insets(20));
        centerLayout.setAlignment(Pos.TOP_CENTER);
        rootLayout.setCenter(centerLayout);

        Button matchBtn = new Button("Forcer l'appareillage");
        matchBtn.setOnAction(e -> {
            String nomPrenomHote = hoteNomPrenomField.getText();
            String nomPrenomVisiteur = visiteurNomPrenomField.getText();

            Adolescent hote = adolescentsHote.stream()
                    .filter(a -> a.getNameAndFirstName().equals(nomPrenomHote))
                    .findFirst().orElse(null);

            Adolescent visiteur = adolescentsVisiteur.stream()
                    .filter(a -> a.getNameAndFirstName().equals(nomPrenomVisiteur))
                    .findFirst().orElse(null);

            if (hote != null && visiteur != null) {
                PairingEngine.forcePair(hote, visiteur);
                currentPairs = PairingEngine.generateOptimalPairs(adolescentsHote, adolescentsVisiteur);

                updatePairsDisplayPanel(currentPairs, adolescentsHote, adolescentsVisiteur);
                showAlert("Appariement forcé entre : " + nomPrenomHote + " et " + nomPrenomVisiteur);
            } else {
                showAlert("Erreur : Hôte ou visiteur introuvable.");
            }
        });

        Button unmatchBtn = new Button("Forcer le non-appareillage");
        unmatchBtn.setOnAction(e -> {
            String nomPrenomHote = hoteNomPrenomField.getText();
            String nomPrenomVisiteur = visiteurNomPrenomField.getText();

            Adolescent hote = adolescentsHote.stream()
                    .filter(a -> a.getNameAndFirstName().equals(nomPrenomHote))
                    .findFirst().orElse(null);

            Adolescent visiteur = adolescentsVisiteur.stream()
                    .filter(a -> a.getNameAndFirstName().equals(nomPrenomVisiteur))
                    .findFirst().orElse(null);

            if (hote != null && visiteur != null) {
                PairingEngine.forceUnpairing(hote, visiteur);
                currentPairs = PairingEngine.generateOptimalPairs(adolescentsHote, adolescentsVisiteur);

                updatePairsDisplayPanel(currentPairs, adolescentsHote, adolescentsVisiteur);
                showAlert("Non-appariement forcé entre : " + nomPrenomHote + " et " + nomPrenomVisiteur);
            } else {
                showAlert("Paire introuvable.");
            }
        });

        Button optimalPairsBtn = new Button("Appariement Optimal");
        optimalPairsBtn.setOnAction(e -> {
            // Appariement optimal à partir des célibataires
            List<Pair> optimal = generateOptimalPairs(adolescentsHote, adolescentsVisiteur);

            // Supprimer des célibataires ceux qui ont été appariés
            for (Pair pair : optimal) {
                hotesLibres.remove(pair.getHost().getNameAndFirstName());
                visiteursLibres.remove(pair.getGuest().getNameAndFirstName());
            }
            currentPairs.clear();
            // Ajouter à la liste des paires actuelles
            currentPairs.addAll(optimal);

            updatePairsDisplayPanel(currentPairs, adolescentsHote, adolescentsVisiteur);
        });

        // Crée une HBox pour le bas avec 2 zones : gauche et droite
        HBox bottomBar = new HBox();
        bottomBar.setPadding(new Insets(20));
        bottomBar.setPrefWidth(1300); // ou la largeur que tu souhaites

        // Zone gauche (pour matchBtn)
        HBox leftBox = new HBox(matchBtn, unmatchBtn);
        leftBox.setAlignment(Pos.BOTTOM_LEFT);
        leftBox.setPrefWidth(650); // moitié de la largeur
        leftBox.setPadding(new Insets(0, 0, 0, 20)); // un peu de marge à gauche

        // Zone droite (pour optimalPairsBtn)
        HBox rightBox = new HBox(optimalPairsBtn);
        rightBox.setAlignment(Pos.BOTTOM_RIGHT);
        rightBox.setPrefWidth(650);
        rightBox.setPadding(new Insets(0, 20, 0, 0)); // un peu de marge à droite

        // Ajoute les 2 zones dans la barre du bas
        bottomBar.getChildren().addAll(leftBox, rightBox);

        rootLayout.setBottom(bottomBar);

        Scene mainScene = new Scene(rootContainer, 1300, 700);
        primaryStage.setScene(mainScene);
        primaryStage.setTitle("Appariement Hôtes/Visiteurs");
        primaryStage.show();

        if(!adolescentsHote.isEmpty() && adolescentsVisiteur.isEmpty()) {
            showAlert("Importation automatique des hôtes.\nVeuillez importer les visiteurs.");
        }
        if(adolescentsHote.isEmpty() && !adolescentsVisiteur.isEmpty()) {
            showAlert("Importation automatique des visiteurs.\nVeuillez importer les hôtes.");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showAlertErreur() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText("Importation échouée");
        alert.setContentText("Veuillez vérifier le contenu du fichier CSV.");
        alert.showAndWait();
    }

    private VBox createPairsDisplayPanelContainer(List<Pair> pairs, List<Adolescent> hotes,
            List<Adolescent> visiteurs) {
        VBox container = new VBox(10);
        container.setAlignment(Pos.TOP_CENTER);
        container.setPrefWidth(450);

        VBox pairsList = new VBox(10);

        if (pairs != null && !pairs.isEmpty()) {
            for (Pair pair : pairs) {
                HBox pairBox = new HBox(30);
                pairBox.setAlignment(Pos.CENTER);
                pairBox.setPadding(new Insets(10));
                pairBox.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, null, null)));

                VBox hostBox = createProfileBox(pair.getHost().getNameAndFirstName(), "hote");
                VBox guestBox = createProfileBox(pair.getGuest().getNameAndFirstName(), "visiteur");

                VBox infoBox = new VBox();
                infoBox.setAlignment(Pos.CENTER);

                int scores = scoreAffinite(pair.getHost(), pair.getGuest());
                String scoreText;
                if (scores == -1) {
                    scoreText = "Incompatible";
                } else if (scores == 2000) {
                    scoreText = "Forcé";
                } else {
                    scoreText = String.valueOf(scores);
                }

                Label affLabel = new Label("Affinité : " + scoreText);
                affLabel.setTextFill(Color.BLACK);
                affLabel.setWrapText(true);
                affLabel.setPrefWidth(120);
                infoBox.getChildren().add(affLabel);

                pairBox.getChildren().addAll(hostBox, infoBox, guestBox);
                pairsList.getChildren().add(pairBox);
            }
        } else if (hotes != null && !hotes.isEmpty() && visiteurs != null && !visiteurs.isEmpty()) {
            HBox pair = new HBox(30);
            pair.setAlignment(Pos.CENTER);
            pair.setPadding(new Insets(10));
            pair.setBackground(new Background(new BackgroundFill(Color.HOTPINK, null, null)));

            VBox leftProfiles = new VBox(10);
            leftProfiles.setAlignment(Pos.CENTER);
            for (Adolescent hote : hotes) {
                leftProfiles.getChildren().add(createProfileBox(hote.getNameAndFirstName(), "hote"));
            }

            VBox rightProfiles = new VBox(10);
            rightProfiles.setAlignment(Pos.CENTER);
            for (Adolescent visiteur : visiteurs) {
                rightProfiles.getChildren().add(createProfileBox(visiteur.getNameAndFirstName(), "visiteur"));
            }

            pair.getChildren().addAll(leftProfiles, rightProfiles);
            pairsList.getChildren().add(pair);
        } else {
            pairsList.getChildren().add(new Label("Aucune donnée disponible pour l'affichage."));
        }

        ScrollPane scrollPane = new ScrollPane(pairsList);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(450);

        container.getChildren().addAll(scrollPane);
        return container;
    }

    private void updatePairsDisplayPanel(List<Pair> pairs, List<Adolescent> hotes, List<Adolescent> visiteurs) {
        VBox newPairsView = createPairsDisplayPanelContainer(pairs, hotes, visiteurs);

        HBox center = (HBox) rootLayout.getCenter();
        // On remplace le 3ème adolescent (index 2) par la nouvelle vue
        center.getChildren().set(2, newPairsView);

        pairsView = newPairsView; // mettre à jour la référence
    }

    private void showSearchOverlay(String type) {
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");

        VBox searchContent = new VBox(20);
        searchContent.setAlignment(Pos.CENTER);
        searchContent.setPadding(new Insets(40));
        searchContent.setStyle("-fx-background-color: white; -fx-border-radius: 10; -fx-background-radius: 10;");
        searchContent.setMaxWidth(400);

        Label titleLabel = new Label("Recherche de " + type);
        titleLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");

        TextField searchField = new TextField();
        searchField.setPromptText("Rechercher...");
        searchField.setPrefWidth(300);

        VBox resultBox = new VBox(10);
        resultBox.setAlignment(Pos.CENTER_LEFT);

        // Obtenir la bonne liste d'adolescents selon le type
        List<Adolescent> sourceList;
        if (type.equalsIgnoreCase("hote")) {
            sourceList = adolescentsHote;
        } else if (type.equalsIgnoreCase("visiteur")) {
            sourceList = adolescentsVisiteur;
        } else {
            sourceList = new ArrayList<>();
        }

        // Affichage des résultats
        for (Adolescent ado : sourceList) {
            final String fullName = ado.getNameAndFirstName();
            Label result = new Label(fullName);

            result.setOnMouseClicked(e -> {
                Adolescent adoo = sourceList.stream()
                        .filter(a -> a.getNameAndFirstName().equals(fullName))
                        .findFirst().orElse(null);

                if (adoo != null) {
                    remplirFormulaire(adoo, type);
                }

                rootContainer.getChildren().remove(overlay);
            });

            resultBox.getChildren().add(result);
        }
        searchField.textProperty().addListener((obs, oldText, newText) -> {
            resultBox.getChildren().clear();
            for (Adolescent ado : sourceList) {
                String fullName = ado.getNameAndFirstName();
                if (fullName.toLowerCase().contains(newText.toLowerCase())) {
                    Label result = new Label(fullName);
                    result.setOnMouseClicked(e -> {
                        remplirFormulaire(ado, type);
                        rootContainer.getChildren().remove(overlay);
                    });
                    resultBox.getChildren().add(result);
                }
            }
        });

        Button closeBtn = new Button("Retour");
        closeBtn.setOnAction(e -> rootContainer.getChildren().remove(overlay));

        searchContent.getChildren().addAll(titleLabel, searchField, resultBox, closeBtn);
        overlay.getChildren().add(searchContent);

        rootContainer.getChildren().add(overlay);
    }

    private TextField hoteNomPrenomField, hotePaysField, hoteGenreField, hoteNaissanceField, hoteAllergieField,
            hoteRegimeField, hoteHobbiesField;
    private TextField visiteurNomPrenomField, visiteurPaysField, visiteurGenreField, visiteurNaissanceField,
            visiteurAllergieField, visiteurRegimeField, visiteurHobbiesField;

    private VBox createPersonForm(String title, String buttonLabel, String type) {
        VBox box = new VBox(10);
        box.setPadding(new Insets(10));
        box.setPrefWidth(300);
        box.setStyle("-fx-border-color: black; -fx-border-radius: 10;");

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font(20));

        Label nomPrenomLabel = new Label("Nom Prénom");
        TextField nomPrenomField = new TextField();
        VBox nomPrenomBox = new VBox(5, nomPrenomLabel, nomPrenomField);

        Label paysLabel = new Label("Pays");
        TextField paysField = new TextField();
        VBox paysBox = new VBox(5, paysLabel, paysField);

        Label genreLabel = new Label("Genre");
        TextField genreField = new TextField();
        VBox genreBox = new VBox(10, genreLabel, genreField);

        Label actualPrefLabel = new Label("Genre préféré");
        TextField actualPrefField = new TextField();
        actualPrefField.setEditable(false); // lecture seule
        VBox actualPrefBox = new VBox(5, actualPrefLabel, actualPrefField);
        HBox genreHBox = new HBox(10, genreBox, actualPrefBox);
        VBox genreSection = new VBox(10, genreHBox);

        // Le label est ajusté ici en fonction du type
        Label allergieLabel = new Label(
                type.equalsIgnoreCase("hote") ? "Possède un animal" : "A une allergie aux animaux");
        TextField allergieField = new TextField();
        VBox allergieBox = new VBox(5, allergieLabel, allergieField);

        Label naissanceLabel = new Label("Date de naissance");
        TextField naissanceField = new TextField();
        naissanceField.setMinHeight(30); // Agrandir à 30px
        VBox naissanceBox = new VBox(5, naissanceLabel, naissanceField);

        // Allergie puis Naissance empilés verticalement
        VBox allergiesBox = new VBox(10, allergieBox, naissanceBox);

        Label regimeLabel = new Label("Régimes alimentaires");
        TextField regimeField = new TextField();
        VBox regimeBox = new VBox(5, regimeLabel, regimeField);

        Label hobbiesLabel = new Label("Hobbies");
        TextField hobbiesField = new TextField();
        VBox hobbiesBox = new VBox(5, hobbiesLabel, hobbiesField);

        Button changeBtn = new Button(buttonLabel);
        changeBtn.setOnAction(e -> showSearchOverlay(type));

        // Stockage des champs selon le type (hôte ou visiteur)
        if (type.equalsIgnoreCase("hote")) {
            hoteNomPrenomField = nomPrenomField;
            hotePaysField = paysField;
            hoteGenreField = genreField;
            hoteNaissanceField = naissanceField;
            hoteAllergieField = allergieField;
            hoteRegimeField = regimeField;
            hoteHobbiesField = hobbiesField;
            hotePrefGenreField = actualPrefField;
        } else {
            visiteurNomPrenomField = nomPrenomField;
            visiteurPaysField = paysField;
            visiteurGenreField = genreField;
            visiteurNaissanceField = naissanceField;
            visiteurAllergieField = allergieField;
            visiteurRegimeField = regimeField;
            visiteurHobbiesField = hobbiesField;
            visiteurPrefGenreField = actualPrefField;
        }

        box.getChildren().addAll(titleLabel, nomPrenomBox, paysBox, genreSection, allergiesBox, regimeBox, hobbiesBox,
                changeBtn);
        return box;
    }

    private VBox createProfileBox(String nomPrenom, String type) {
        VBox box = new VBox(5);
        box.setAlignment(Pos.CENTER);
        box.setPrefSize(150, 50);
        box.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-padding: 5;");

        Label name = new Label(nomPrenom);
        box.getChildren().addAll(name);

        // Au clic, remplir le formulaire et déclencher la compatibilité
        box.setOnMouseClicked(e -> {
            Adolescent ado;
            if (type.equals("hote")) {
                ado = adolescentsHote.stream()
                        .filter(a -> a.getNameAndFirstName().equals(nomPrenom))
                        .findFirst().orElse(null);

                if (ado != null) {
                    remplirFormulaire(ado, "hote");
                    hoteNomPrenomField.setText(nomPrenom);
                }
            } else {
                ado = adolescentsVisiteur.stream()
                        .filter(a -> a.getNameAndFirstName().equals(nomPrenom))
                        .findFirst().orElse(null);

                if (ado != null) {
                    remplirFormulaire(ado, "visiteur");
                    visiteurNomPrenomField.setText(nomPrenom);
                }
            }

            // Vérification compatibilité si les deux ados sont sélectionnés
            String hoteNom = hoteNomPrenomField.getText();
            String visiteurNom = visiteurNomPrenomField.getText();

            if (hoteNom != null && !hoteNom.isEmpty() && visiteurNom != null && !visiteurNom.isEmpty()) {
                Adolescent hote = adolescentsHote.stream()
                        .filter(a -> a.getNameAndFirstName().equals(hoteNom))
                        .findFirst().orElse(null);

                Adolescent visiteur = adolescentsVisiteur.stream()
                        .filter(a -> a.getNameAndFirstName().equals(visiteurNom))
                        .findFirst().orElse(null);

                if (hote != null && visiteur != null) {
                    verifierCompatibilite(hote, visiteur);
                }
            }
        });

        return box;
    }

    private void setFieldGreen(TextField field) {
        field.setStyle("-fx-border-color: green; -fx-border-width: 2;");
    }

    private void setFieldRed(TextField field) {
        field.setStyle("-fx-border-color: red; -fx-border-width: 2;");
    }

    private void setFieldGrey(TextField field) {
        field.setStyle("-fx-border-color: grey; -fx-border-width: 2;");
    }

    private void verifierCompatibilite(Adolescent hote, Adolescent visiteur) {
        // 1. Allergie ↔ Animal
        String hoteAnimal = hote.getCriteria().get("HOST_HAS_ANIMAL").toLowerCase().trim();
        String visiteurAllergie = visiteur.getCriteria().get("GUEST_ANIMAL_ALLERGY").toLowerCase().trim();
        if ((hoteAnimal.equals("yes") && visiteurAllergie.equals("no")) ||
                (hoteAnimal.equals("no") && visiteurAllergie.equals("no")) ||
                (hoteAnimal.equals("no") && visiteurAllergie.equals("yes"))) {
            setFieldGreen(hoteAllergieField);
            setFieldGreen(visiteurAllergieField);
        } else {
            setFieldRed(hoteAllergieField);
            setFieldRed(visiteurAllergieField);
        }

        // 2. Genre préféré ↔ Genre réel
        String prefGenreVisiteur = visiteur.getCriteria().get("PAIR_GENDER").trim();
        String genreHote = hote.getGenre().trim();
        if (prefGenreVisiteur.equalsIgnoreCase(genreHote) || prefGenreVisiteur.equalsIgnoreCase("Non spécifié")) {
            setFieldGreen(visiteurPrefGenreField);
            setFieldGreen(hoteGenreField);
        } else {
            setFieldRed(visiteurPrefGenreField);
            setFieldRed(hoteGenreField);
        }

        String prefGenreHote = hote.getCriteria().get("PAIR_GENDER").trim();
        String genreVisiteur = visiteur.getGenre().trim();
        if (prefGenreHote.equalsIgnoreCase(genreVisiteur) || prefGenreHote.equalsIgnoreCase("Non spécifié")) {
            setFieldGreen(hotePrefGenreField);
            setFieldGreen(visiteurGenreField);
        } else {
            setFieldRed(hotePrefGenreField);
            setFieldRed(visiteurGenreField);
        }

        // 3. Régimes alimentaires
        String hoteFood = hote.getCriteria().get("HOST_FOOD").trim();
        String visiteurFood = visiteur.getCriteria().get("GUEST_FOOD").trim();
        if (hoteFood.equalsIgnoreCase(visiteurFood) ||
                hoteFood.equalsIgnoreCase("Pas de préférence") ||
                visiteurFood.equalsIgnoreCase("Pas de préférence")) {
            setFieldGreen(hoteRegimeField);
            setFieldGreen(visiteurRegimeField);
        } else {
            setFieldRed(hoteRegimeField);
            setFieldRed(visiteurRegimeField);
        }

        // 4. Hobbies
        String hoteHobbies = hote.getCriteria().get("HOBBIES").trim();
        String visiteurHobbies = visiteur.getCriteria().get("HOBBIES").trim();
        if (hoteHobbies.equalsIgnoreCase(visiteurHobbies) ||
                hoteHobbies.equalsIgnoreCase("Pas de préférence") ||
                visiteurHobbies.equalsIgnoreCase("Pas de préférence")) {
            setFieldGreen(hoteHobbiesField);
            setFieldGreen(visiteurHobbiesField);
        } else {
            setFieldRed(hoteHobbiesField);
            setFieldRed(visiteurHobbiesField);
        }

        // 5. Affinité d'âge
        int affinite = getAffiniteAge(hote, visiteur);
        if (affinite == 30) {
            setFieldGreen(hoteNaissanceField);
            setFieldGreen(visiteurNaissanceField);
        } else {
            setFieldGrey(hoteNaissanceField);
            setFieldGrey(visiteurNaissanceField);
        }
    }

    private void remplirFormulaire(Adolescent ado, String type) {
        String pairGender = ado.getCriteria().get("PAIR_GENDER");
        if (type.equals("hote")) {
            hoteNomPrenomField.setText(ado.getNameAndFirstName());
            hotePaysField.setText(ado.getCountryOrigin());
            hoteGenreField.setText(ado.getGenre());
            hoteNaissanceField.setText(ado.getBirthDate());
            hoteAllergieField.setText(ado.getCriteria().get("HOST_HAS_ANIMAL").equals("yes") ? "Oui" : "Non");
            String hostFood = ado.getCriteria().get("HOST_FOOD");
            hoteRegimeField.setText((hostFood != null && !hostFood.trim().isEmpty()) ? hostFood : "Pas de préférence");

            String hostHobbies = ado.getCriteria().get("HOBBIES");
            hoteHobbiesField.setText(
                    (hostHobbies != null && !hostHobbies.trim().isEmpty()) ? hostHobbies : "Pas de préférence");
            hotePrefGenreField.setText(pairGender != null ? pairGender : "Non spécifié");

        } else {
            visiteurNomPrenomField.setText(ado.getNameAndFirstName());
            visiteurPaysField.setText(ado.getCountryOrigin());
            visiteurGenreField.setText(ado.getGenre());
            visiteurNaissanceField.setText(ado.getBirthDate());
            visiteurAllergieField.setText(ado.getCriteria().get("GUEST_ANIMAL_ALLERGY").equals("yes") ? "Oui" : "Non");
            String guestFood = ado.getCriteria().get("GUEST_FOOD");
            visiteurRegimeField
                    .setText((guestFood != null && !guestFood.trim().isEmpty()) ? guestFood : "Pas de préférence");

            String guestHobbies = ado.getCriteria().get("HOBBIES");
            visiteurHobbiesField.setText(
                    (guestHobbies != null && !guestHobbies.trim().isEmpty()) ? guestHobbies : "Pas de préférence");
            visiteurPrefGenreField.setText(pairGender != null ? pairGender : "Non spécifié");

        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}