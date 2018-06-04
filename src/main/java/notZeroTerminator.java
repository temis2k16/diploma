import org.uncommons.watchmaker.framework.EvolutionObserver;
import org.uncommons.watchmaker.framework.PopulationData;
import org.uncommons.watchmaker.framework.TerminationCondition;
import org.uncommons.watchmaker.framework.termination.Stagnation;

import java.util.ArrayList;
import java.util.List;

public class notZeroTerminator implements TerminationCondition {
    private Stagnation st;

    notZeroTerminator(int generationLimit){
        st = new Stagnation(generationLimit,true);
    }

    public boolean shouldTerminate(PopulationData<?> populationData) {
        return !(populationData.getBestCandidateFitness() == 0.0) && st.shouldTerminate(populationData);
    }
}
