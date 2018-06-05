package GeneticAlgorithm;

import main.Flight;
import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class BlocksFactory extends AbstractCandidateFactory<List<Flight.block>> {

    private final List<Flight.block> alphabet;
    private final int size;
    private final Flight flight;
    private final List<Flight.week> statWeek;

    BlocksFactory(List<Flight.block> alphabet, int size, Flight flight, List<Flight.week> statWeeks) {
        this.alphabet = alphabet;
        this.size = size;
        this.flight = flight;
        this.statWeek = statWeeks;

    }

    public List<Flight.block> generateRandomCandidate(Random rng) {
        List<Flight.block> cand = new ArrayList<>();
        List<Flight.block> tempWish = new ArrayList<>(flight.Wishlist);
        Flight.block b;

        for (int i = 0; i < size; i++) {

            if (tempWish.size() > 0){
                cand.add(alphabet.get(alphabet.indexOf(tempWish.get(i))));
                tempWish.remove(i);
            }
            else {
                cand.add(alphabet.get(rng.nextInt(alphabet.size())));
            }

            double prop;
            b = cand.get(i);
            boolean picked = false;
            picking:
            for (Map.Entry<Integer, List<Flight.week>> pair : flight.weeks.entrySet()) {
                for (Flight.week week : pair.getValue()) {
                    if ((b.issueDate.after(week.begin)) && (b.issueDate.before(week.end))
                            || (b.issueDate.equals(week.begin)) || (b.issueDate.equals(week.end))) {

                        prop = week.ratio / statWeek.get(statWeek.indexOf(week)).ratio;
                        if (rng.nextDouble() < prop) {
                            //ad is picked
                            picked = true;
                            for (Flight.ad a : flight.Ads) {
                                if (a.equals(pair.getKey())) {
                                    b.setCurrentAd(a);
                                }
                            }
                            break picking;
                        }
                    }
                }
            }
            if (!picked) {
                b.setCurrentAd(flight.Ads.get(rng.nextInt(flight.Ads.size())));
            }
        }

        return cand;
    }
}
