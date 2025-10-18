package main;

public class Pair {
    private Adolescent host; // l'adolescent hôte
    private Adolescent guest; // l'adolescent invité
    private int scoreAffinite; // le score d'affinité


    
    public Pair(Adolescent host, Adolescent guest) {
        this.host=host;
        this.guest=guest;
        this.scoreAffinite=PairingEngine.scoreAffinite(host, guest);
    }

    /**
     * hashCode généré par VsCode.
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((host == null) ? 0 : host.hashCode());
        result = prime * result + ((guest == null) ? 0 : guest.hashCode());
        return result;
    }

    /**
     * Sert à savoir si deux objets sont égaux en fonction du guest et de l'host. Généré par VsCode
     * @return Si obj est égal à l'objet courant alors true, sinon false.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Pair other = (Pair) obj;
        if (host == null) {
            if (other.host != null)
                return false;
        } else if (!host.equals(other.host))
            return false;
        if (guest == null) {
            if (other.guest != null)
                return false;
        } else if (!guest.equals(other.guest))
            return false;
        return true;
    }

    // Getters
    public Adolescent getHost() {
        return host;
    }

    public Adolescent getGuest() {
        return guest;
    }

    public int getScoreAffinite() {
        return scoreAffinite;
    }

    @Override
    public String toString() {
        return this.getHost()+"<->"+this.getGuest()+" : "+this.getScoreAffinite()+"\n";
    }
}
