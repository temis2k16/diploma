//The Watchmaker Framework
import org.uncommons.maths.random.PoissonGenerator;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.*;
import org.uncommons.watchmaker.framework.operators.*;
import org.uncommons.watchmaker.framework.selection.*;
import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.watchmaker.framework.termination.GenerationCount;


import java.util.*;
import static java.lang.Math.ceil;
public class GeneticAlgorithm {

//    public static void main(final String[] args) {
    public static void runGeneticAlgorithm(genDbConnection ga, Flight flight){
//        genDbConnection ga = new genDbConnection();
//        Flight flight = new Flight(ga);
//        Flight.ad CurrentAd = flight.Ads.get(0);
        ga.createTempTable();
        ga.createResultTable();
        double mean = ga.getMeanGrp();
        //random
        Random rng = new MersenneTwisterRNG();
        // Create a factory.
        List<Flight.block> values = ga.getAllBlocks();

        double size = flight.TotalAmount/ mean;
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
        operators.add(new ListCrossover<>((rng.nextInt(2)+1)));
//        operators.add(new ListCrossover<>());
        EvolutionaryOperator<List<Flight.block>> pipeline
                = new EvolutionPipeline<>(operators);

        FitnessEvaluator<List<Flight.block>> fitnessEvaluator = new BlocksEvaluator(flight, 0.35);
        SelectionStrategy<Object> selection = new
                TournamentSelection(new Probability(0.6));


        EvolutionEngine<List<Flight.block>> engine
                = new GenerationalEvolutionEngine<>(factory,
                pipeline,
                fitnessEvaluator,
                selection,
                rng);

        final long[] t = {0, 0};
//        final double[] f={0.0};
        engine.addEvolutionObserver(data -> {
//            System.out.printf("Generation %d: %s\tFITNESS = %f\n",
//                    data.getGenerationNumber(),
//                    data.getBestCandidate(),
//                    data.getBestCandidateFitness());
//            f[0] = data.getBestCandidateFitness();
            t[0] = data.getElapsedTime();
            t[1] = data.getGenerationNumber();
        });


//        String result = (engine.evolve(50, 5, new Stagnation(20,true))).toString();

        Set<Flight.block> res = new HashSet<>(engine.evolve(60, 2
                , new notZeroTerminator(100)
                , new GenerationCount(1000)
        ));

//        System.out.println("\nRESULT:\n");

//        System.out.println(res);

        double plus;
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

            for (Flight.week temp : flight.status.statusWeeks.get(b.CurrentAd.id)) {
                if ((b.issueDate.after(temp.begin)) && (b.issueDate.before(temp.end))
                        || (b.issueDate.equals(temp.begin)) || (b.issueDate.equals(temp.end)) ) {
                    temp.addGrp(plus);
                    temp.setRatio(temp.grp / flight.status.statusMonths.get(b.getMonth()).grp);
                }
            }

        }

//        System.out.println(flight);
//        System.out.println(flight.status);
        System.out.println("\ntime = "+ t[0]+" milliseconds\nmax generation = " + t[1]);

//        CheckResult.UniformTest(ga, "\"geneticAlgorithm\"");
//        CheckResult.UniformTest(ga,null);

    }
}

