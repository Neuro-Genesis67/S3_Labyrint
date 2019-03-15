package networkgame;

import java.util.ArrayList;

public class TokenPassing_MutualExclusion {
    //
    // int clock;
    // boolean token_present;
    // boolean token_held;
    //
    // static ArrayList<String> tokenArray = new ArrayList<>();
    //
    // public void requestToken() {
    // if (!token_present) {
    // clock++; /* Prelude */
    // broadcast(Request, clock, i);
    // wait(access, token);
    // token_present = true;
    // }
    // token_held = true;
    // // <critical section>;
    //
    // if (received(Request, k, j)) {
    // request(j) = max(request(j), k);
    // if (token_present && !token_held) {
    // token[i] = clock; /* Postlude */
    // }
    // token_held = false;
    // for (int j = i + 1; j < n; j++) {
    // if (request(j) > token[j] && token_present) {
    // token_present = false;
    // send(access, token[j]);
    // }
    // }
    // }
    //
    // }
    //
    // public void request(int j) {
    //
    // }
}
