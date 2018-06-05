package GeneticAlgorithm;

import org.uncommons.watchmaker.framework.PopulationData;
import org.uncommons.watchmaker.framework.TerminationCondition;
import org.uncommons.watchmaker.framework.termination.Stagnation;

public class notZeroTerminator implements TerminationCondition {
    private Stagnation st;

    notZeroTerminator(int generationLimit){
        st = new Stagnation(generationLimit,true);
    }

    public boolean shouldTerminate(PopulationData<?> populationData) {
        return !(populationData.getBestCandidateFitness() == 0.0) && st.shouldTerminate(populationData);
    }
}
