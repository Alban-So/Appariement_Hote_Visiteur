package main;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.alg.matching.MaximumWeightBipartiteMatching;

public class AlgorithmEngine {

    /**
     * Renvoi les pair selon l'algorithme Hongrois du couplage optimal et maximum.
     * Ce qui donne donc les apparaiments parfaits pour chaque host/guest.
     * Le but ici est d'avoir la somme des scores d'affinité de chaque pair et que cette somme soit maximale.
     * @param hosts
     * @param guests
     * @return Optimal Pairings
     */
    protected static List<Pair> executeMatchingAlgorithm(List<Adolescent> hosts, List<Adolescent> guests) {
        Graph<Adolescent, DefaultWeightedEdge> graph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);

        // ajoute les hosts et guest au graphe
        for (Adolescent h : hosts) {
            graph.addVertex(h);
        }
        for (Adolescent g : guests) {
            graph.addVertex(g);
        }

        /**
         * Crée des arêtes et définit les poids des arêtes
         * Le poids est défini par la fonction scoreAffinite qui calcule l'affinite
         * entre host et guest.
         * Si le score est de -1 ce qui souligne qu'une
         * clause rédhibitoire a été levé alors on n'ajoute pas
         * d'arête enre ces deux personnes
         */
        for (Adolescent h : hosts) {
            for (Adolescent g : guests) {
                int poids = PairingEngine.scoreAffinite(h, g);
                if(poids < 0) {
                    continue;
                }
                DefaultWeightedEdge e = graph.addEdge(h, g);
                graph.setEdgeWeight(e, poids);
            }
        }

        /**
         * Deux Set de host et guest car le constructeur MaximumWeightBipartiteMatching
         * prends en paramètre des Set et non des List
         */
        Set<Adolescent> hostSet = new HashSet<>(hosts);
        Set<Adolescent> guestSet = new HashSet<>(guests);

        MaximumWeightBipartiteMatching<Adolescent, DefaultWeightedEdge> matcher = new MaximumWeightBipartiteMatching<>(
                graph, hostSet, guestSet);

        /**
         * Récupérer les matchings optimales.
         */
        Set<DefaultWeightedEdge> matchingEdges = matcher.getMatching().getEdges();
        List<Pair> pairings = new ArrayList<>();        
        for (DefaultWeightedEdge e : matchingEdges) {
            Adolescent host = graph.getEdgeSource(e);
            Adolescent guest = graph.getEdgeTarget(e);
            pairings.add(new Pair(host, guest));
        }
        return pairings;
    }


    /**
     * Renvoi les pair selon l'algorithme Hongrois du couplage optimal et maximum.
     * Ce qui donne donc les apparaiments parfaits pour chaque host/guest.
     * Le but ici est d'avoir la somme des scores d'affinité de chaque pair et que cette somme soit maximale.
     * @param pairs
     * @return Optimal Pairings
     */
    protected static List<Pair> executeMatchingAlgorithm(List<Pair> pairs) {
        List<Adolescent> hosts = new ArrayList<>();
        List<Adolescent> guests = new ArrayList<>();
        for(Pair p: pairs) {
            hosts.add(p.getHost());
            guests.add(p.getGuest());
        }
        return executeMatchingAlgorithm(hosts, guests);
    }
}