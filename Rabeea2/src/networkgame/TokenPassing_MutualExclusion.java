package networkgame;

import java.util.ArrayList;

public class TokenPassing_MutualExclusion {

    int clock;
    static int siteCounter;
    int id;
    boolean gotTheToken;
    Player player;
    static ArrayList<TokenPassing_MutualExclusion> playersConnected = new ArrayList<>();
    static ArrayList<String> tokenArraylist = new ArrayList<>();

    public TokenPassing_MutualExclusion(Player player, int clock) {
        this.player = player;
        this.clock = clock;
        this.id = siteCounter;

        // den første spiller starter med at have token
        if (id == 0) {
            this.gotTheToken = true;
        } else {
            this.gotTheToken = false;
        }

        siteCounter++; // tæller antal spillere - bruges til at give hver spiller et unikt id

        playersConnected.add(this); // tilføjer spilleren til listen
    }

    // BLIVER IKKE BRUGT LIGE PT.
    public void requestToken() {
        clock++;
        tokenArraylist.add(player.getName() + " " + clock + " " + id);
    }

    // denne er kun beregnet til 2 spillere
    public void sendToken() {

        for (int i = 0; i < playersConnected.size(); i++) {

            // hvis spiller1 har token er det spiller2 der får token og den fjernes fra
            // spiller 1 ellers er det omvendt
            if (playersConnected.get(i).gotTheToken) {
                gotTheToken = false;

                if (playersConnected.get(i).id == 0) {
                    playersConnected.get(1).gotTheToken = true;

                } else if (playersConnected.get(i).id == 1) {
                    playersConnected.get(0).gotTheToken = true;
                }
            }
        }
    }
}
