package app.view.network;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NetworkRankSortTest {
    @Test
    void parseRecognizesSortIdsAndAliases() {
        assertEquals(NetworkRankSort.MIOPOINT, NetworkRankSort.parse(null));
        assertEquals(NetworkRankSort.MIOPOINT, NetworkRankSort.parse("miopoint"));
        assertEquals(NetworkRankSort.DIAMOND, NetworkRankSort.parse("diamond"));
        assertEquals(NetworkRankSort.DIAMOND, NetworkRankSort.parse("gems"));
        assertEquals(NetworkRankSort.COIN, NetworkRankSort.parse("coin"));
        assertEquals(NetworkRankSort.GAMES_PLAYED, NetworkRankSort.parse("games played"));
        assertEquals(NetworkRankSort.GAMES_PLAYED, NetworkRankSort.parse("games_played"));
    }
}
