package GeneticAlgorithm;

import main.Flight;

//The Watchmaker Framework
import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.maths.random.PoissonGenerator;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.*;
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline;
import org.uncommons.watchmaker.framework.operators.ListCrossover;
import org.uncommons.watchmaker.framework.operators.ListOrderMutation;
import org.uncommons.watchmaker.framework.selection.TournamentSelection;
import org.uncommons.watchmaker.framework.termination.GenerationCount;

import java.util.*;

import static java.lang.Math.ceil;


public class GeneticAlgorithm {

    public static String runGeneticAlgorithm(genDbConnection ga, Flight flight){

        ga.createTempTable();
        ga.createResultTable();
        double mean = ga.getMeanGrp();
        //random
        Random rng = new MersenneTwisterRNG();
        // Create a factory.
        List<Flight.block> values = ga.getAllBlocks();

        double size = flight.TotalAmount/ mean;
        //pack weeks into list
        List<Flight.week> statWeeks = new ArrayList<>();
        for (Map.Entry<Integer, List<Flight.week>> pair: flight.weeks.entrySet()) {
            for (Flight.week week: pair.getValue()){
                if (statWeeks.contains(week)){
                    statWeeks.get(statWeeks.indexOf(week)).ratio += week.ratio;
                }
                else {
                    statWeeks.add(new Flight.week(week.begin, week.end, week.ratio));
                }
            }
        }

        CandidateFactory<List<Flight.block>> factory = new BlocksFactory(values, (int) ceil(size*(rng.nextDouble()+1)),
                                                                            flight, statWeeks);
//      Create a pipeline that applies cross-over then mutation.
        List<EvolutionaryOperator<List<Flight.block>>> operators
                = new LinkedList<>();

        operators.add(new ListOrderMutation<>(new PoissonGenerator(2, rng), new PoissonGenerator(2, rng)));
        operators.add(new ListCrossover<>(2));
        EvolutionaryOperator<List<Flight.block>> pipeline
                = new EvolutionPipeline<>(operators);

        FitnessEvaluator<List<Flight.block>> fitnessEvaluator = new BlocksEvaluator(flight, 0.25);
        SelectionStrategy<Object> selection = new
                TournamentSelection(new Probability(0.8));


        EvolutionEngine<List<Flight.block>> engine
                = new GenerationalEvolutionEngine<>(factory,
                pipeline,
                fitnessEvaluator,
                selection,
                rng);

        final long[] t = {0, 0};
        engine.addEvolutionObserver(data -> {
        /*    System.out.printf("Generation %d: %s\tFITNESS = %f\n",
                    data.getGenerationNumber(),
                    data.getBestCandidate(),
                    data.getBestCandidateFitness());*/
            t[0] = data.getElapsedTime();
            t[1] = data.getGenerationNumber();
        });

        Set<Flight.block> res = new HashSet<>(engine.evolve(80, 3
                , new notZeroTerminator(100)
                , new GenerationCount(1000)
        ));


        double plus;
//        flight.status.Aff = f[0];
        for (Flight.block b: res){
            plus = b.grp * (double) b.CurrentAd.duration/30.0;
            ga.addResultLine(b.id, b.CurrentAd.id, b.issueDate, plus);
            ga.deleteBlock(b.id);
            flight.status.GRP += plus;
            flight.status.statusMonths.get(b.getMonth()).addMonthGrp(plus);
            flight.status.Aff += b.aff * (double) b.CurrentAd.duration/30.0;
            if (b.prime) {
                flight.status.statusMonths.get(b.getMonth()).addPrime(b.grp);
            }
            else {
                flight.status.statusMonths.get(b.getMonth()).addNonPrime(b.grp);
            }
        }
        for (Flight.block b: res) {
            plus = b.grp * (double) b.CurrentAd.duration/30.0;
            for (Flight.week temp : flight.status.statusWeeks.get(b.CurrentAd.id)) {
                if ((b.issueDate.after(temp.begin)) && (b.issueDate.before(temp.end))
                        || (b.issueDate.equals(temp.begin)) || (b.issueDate.equals(temp.end))) {
                    temp.addGrp(plus);
                    temp.setRatio(temp.grp / flight.status.statusMonths.get(b.getMonth()).grp);
                }
            }
        }

        ga.dropTempTable();
        return "\nВремя работы алгоритма = "+ t[0]+" миллисекунд\nЧисло поколений = " + t[1];

    }
}

