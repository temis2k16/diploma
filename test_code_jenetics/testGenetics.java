// The main class. (knapsack with different elements GA)
//The Watchmaker Framework
import org.uncommons.maths.random.PoissonGenerator;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.*;
import org.uncommons.watchmaker.framework.operators.*;
import org.uncommons.watchmaker.framework.selection.*;
import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.watchmaker.framework.termination.GenerationCount;
import org.uncommons.watchmaker.framework.termination.Stagnation;
import org.uncommons.watchmaker.framework.termination.TargetFitness;

import java.util.*;
import static java.lang.Math.ceil;


public class testGenetics {

    public static class block{
        int id;
        double aff;
        double grp;
        block(int id, double aff, double grp){
            this.id = id;
            this.aff = aff;
            this.grp = grp;
        }
        @Override
        public String toString() {
            return "\nid = "+ id +" aff: " + aff + " grp: " + grp;
        }
    }

    private static double limit = 8.77;
    private static int blocksCount;

    public static void test (final String [] args) {

        blocksCount = 3000;
        //random
        Random rng = new MersenneTwisterRNG();
        // Create a factory to generate random 11-character Strings.
//        Map<Integer,block> blocks = new HashMap<>();
        List<block> values = new ArrayList<>();
        double mean = 0;

        for (int i = 0; i<blocksCount; i++){
            values.add(new block(i,rng.nextDouble(),rng.nextDouble()));
            mean += values.get(i).grp;
        }
        mean /= blocksCount;
        double size = limit/ mean;

        System.out.println("MEAN = " + mean + "\n SIZE = " + size + "\n\n");
//        System.out.println(values);
//        values.addAll(blocks.values());

        CandidateFactory<List<block>> factory = new IntegerFactory(values, (int) ceil(size*(rng.nextDouble()+1)));
// Create a pipeline that applies cross-over then mutation.
        List<EvolutionaryOperator<List<block>>> operators
                = new LinkedList<>();

        operators.add(new ListOrderMutation<>(new PoissonGenerator(1, rng), new PoissonGenerator(1, rng)));
        operators.add(new ListCrossover<>((rng.nextInt(5)+1)));
        EvolutionaryOperator<List<block>> pipeline
                = new EvolutionPipeline<>(operators);

        FitnessEvaluator<List<block>> fitnessEvaluator = new IntegerEvaluator(limit);
        SelectionStrategy<Object> selection = new TournamentSelection(new Probability(0.6));


        EvolutionEngine<List<block>> engine
                = new GenerationalEvolutionEngine<>(factory,
                pipeline,
                fitnessEvaluator,
                selection,
                rng);

        final long[] t = {0};
        final double[] f={0.0};
        engine.addEvolutionObserver(data -> {
            System.out.printf("Generation %d: %s\tFITNESS = %f\n",
                    data.getGenerationNumber(),
                    data.getBestCandidate(),
                    data.getBestCandidateFitness());
            f[0] = data.getBestCandidateFitness();
            t[0] = data.getElapsedTime();
        });


//        String result = (engine.evolve(50, 5, new Stagnation(20,true))).toString();

        Set<block> res = new HashSet<>((engine.evolve(30, 2
                                        , new notZeroTerminator(70)
                                        , new GenerationCount(200)
//                                        , new Stagnation(20,true)
        )));

        System.out.println("\nRESULT:\n");

        System.out.println(res);
        double sumGRP = 0.0;
        for (block b: res){
            sumGRP += b.grp;
        }


        System.out.println("\nAFF = " + f[0]+"\tGRP = "+ sumGRP);

        System.out.println("time = "+ t[0]+" milliseconds");

    }
}
