//=============================================================================
// Copyright 2006-2010 Daniel W. Dyer
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//=============================================================================
package partiturasevolutivas;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.uncommons.maths.number.AdjustableNumberGenerator;
import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.CachingFitnessEvaluator;
import org.uncommons.watchmaker.framework.EvolutionEngine;
import org.uncommons.watchmaker.framework.EvolutionObserver;
import org.uncommons.watchmaker.framework.EvolutionStrategyEngine;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.GenerationalEvolutionEngine;
import org.uncommons.watchmaker.framework.PopulationData;
import org.uncommons.watchmaker.framework.TerminationCondition;
import org.uncommons.watchmaker.framework.islands.IslandEvolution;
import org.uncommons.watchmaker.framework.islands.IslandEvolutionObserver;
import org.uncommons.watchmaker.framework.islands.RingMigration;
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline;
import org.uncommons.watchmaker.framework.operators.ListCrossover;
import org.uncommons.watchmaker.framework.operators.ListOperator;
import org.uncommons.watchmaker.framework.selection.StochasticUniversalSampling;
import org.uncommons.watchmaker.framework.termination.Stagnation;
import org.uncommons.watchmaker.framework.termination.TargetFitness;
import org.uncommons.watchmaker.swing.ProbabilityParameterControl;

/**
 * Simple evolutionary algorithm that evolves a population of randomly-generated
 * strings until at least one matches a specified target string.
 * @author Daniel Dyer
 */
public final class PartiturasEvolutivas
{
    private static final Probability TWO_TENTH = new Probability(0.2d);



    /**
     * Entry point for the sample application.  Any data specified on the
     * command line is considered to be the target String.  If no target is
     * specified, a default of "HELLOW WORLD" is used instead.
     * @param args The target String (as an array of words).
     */
    public static void main(String[] args)
    {
        Properties p= new Properties();
        try {
            p.load(new FileInputStream("parametros.prop"));
        } catch (IOException ex) {
            Logger.getLogger(PartiturasEvolutivas.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Nota.archivo=p.getProperty("archivo");

        
        Nota.tam_generacion=Integer.parseInt(p.getProperty("tam_generacion"));
        Nota.elitismo=Integer.parseInt(p.getProperty("elitismo"));
        Nota.target_fitness=Float.parseFloat(p.getProperty("target_fitness"));
        Nota.epocas=Integer.parseInt(p.getProperty("epocas"));
        Nota.migracion=Integer.parseInt(p.getProperty("migracion"));
        Nota.islas=Integer.parseInt(p.getProperty("islas"));



        //probabilidades 
        Nota.mutacion_nota=Float.parseFloat(p.getProperty("mutacion_nota"));
        Nota.mutacion_offset=Float.parseFloat(p.getProperty("mutacion_offset"));
        Nota.mutacion_add_nota=Float.parseFloat(p.getProperty("mutacion_add_nota"));
        Nota.mutacion_rem_nota=Float.parseFloat(p.getProperty("mutacion_rem_nota"));
        Nota.mutacion_join_nota=Float.parseFloat(p.getProperty("mutacion_join_nota"));
        Nota.mutacion_split_nota=Float.parseFloat(p.getProperty("mutacion_split_nota"));
        
        System.out.println( "tam_generacion="+Nota.tam_generacion+"\n" +
                            "elitismo="+Nota.elitismo+"\n" +
                            "target_fitness="+Nota.target_fitness+"\n" +
                            "epocas="+Nota.epocas+"\n" +
                            "migracion="+Nota.migracion+"\n" +
                            "islas="+Nota.islas+"\n" +
                            "\n" +
                            "mutacion_nota="+Nota.mutacion_nota+"\n" +
                            "mutacion_offset="+Nota.mutacion_offset+"\n" +
                            "mutacion_add_nota="+Nota.mutacion_add_nota+"\n" +
                            "mutacion_rem_nota="+Nota.mutacion_rem_nota+"\n" +
                            "mutacion_join_nota="+Nota.mutacion_join_nota+"\n" +
                            "mutacion_split_nota="+Nota.mutacion_split_nota+" ");
        
        List<Nota> l = evolvePartiture();
        System.out.println("Evolution result: " + l.toString());
    }


    public static List<Nota> evolvePartiture( )
    {        
        //System.out.println(Nota.getPosTiempo(Nota.individualFromString("G5q C5i D5i E5i G5q C5i D5i E5i"),20));
        long ms= new Date().getTime();

        ProbabilityParameterControl mutNota = new ProbabilityParameterControl(Probability.ZERO,
                TWO_TENTH,
                3,
                new Probability(Nota.mutacion_nota));
        ProbabilityParameterControl mutOffset = new ProbabilityParameterControl(Probability.ZERO,
                TWO_TENTH,
                3,
                new Probability(Nota.mutacion_offset));
        ProbabilityParameterControl addNota = new ProbabilityParameterControl(Probability.ZERO,
                TWO_TENTH,
                3,
                new Probability(Nota.mutacion_add_nota));
        ProbabilityParameterControl remNota = new ProbabilityParameterControl(Probability.ZERO,
                TWO_TENTH,
                3,
                new Probability(Nota.mutacion_rem_nota));
        ProbabilityParameterControl mutJoin = new ProbabilityParameterControl(Probability.ZERO,
                TWO_TENTH,
                3,
                new Probability(Nota.mutacion_join_nota));
        ProbabilityParameterControl mutSplit = new ProbabilityParameterControl(Probability.ZERO,
                TWO_TENTH,
                3,
                new Probability(Nota.mutacion_split_nota));
        
        Random random=new MersenneTwisterRNG();
        int l=PartiturasFactory.guessCandidateLength(random,Nota.max_tam);
        
        //System.out.println(l);
        PartitureEvaluator pe=new PartitureEvaluator(l);      
        
        CachingFitnessEvaluator cfe=new CachingFitnessEvaluator<List<Nota>>(pe);
        PartiturasFactory factory = new PartiturasFactory(Nota.min_tam,Nota.max_tam,l);
        List<EvolutionaryOperator<List<Nota>>> operators = new ArrayList<EvolutionaryOperator<List<Nota>>>(3);
        operators.add(new ListOperator<Nota>(new NotaMutation(mutNota.getNumberGenerator() )));//new Probability(0.02d)));
        operators.add(new ListOperator<Nota>(new JoinNotesMutation(mutJoin.getNumberGenerator() )));//new Probability(0.02d)));
        operators.add(new ListOperator<Nota>(new SplitNotesMutation(mutSplit.getNumberGenerator() )));//new Probability(0.02d)))
        operators.add(new ListOperator<Nota>(new offsetMutation(mutOffset.getNumberGenerator() )));
        operators.add(new CustomListCrossover(new AdjustableNumberGenerator<Integer>(1)));
        operators.add(new AddNotaMutation(addNota.getNumberGenerator(),factory));
        operators.add(new RemoveNoteMutation(remNota.getNumberGenerator()));
        EvolutionaryOperator<List<Nota>> pipeline = new EvolutionPipeline<List<Nota>>(operators);
//        EvolutionEngine<List<Nota>> engine = new GenerationalEvolutionEngine<List<Nota>>(factory,
//                                                                                 pipeline,
//                                                                                 cfe,
//                                                                                 new StochasticUniversalSampling(),
//                                                                                 random);
        
        IslandEvolution<List<Nota>> engine = new IslandEvolution<List<Nota>>(Nota.islas, // Number of islands.
                                                          new RingMigration(),
                                                          factory,
                                                          pipeline,
                                                          cfe,
                                                          new StochasticUniversalSampling(),
                                                          random);
        
//        EvolutionEngine<List<Nota>> engine = new EvolutionStrategyEngine<List<Nota>>(factory,
//                                                                                 pipeline,
//                                                                                 new PartitureEvaluator(),
//                                                                                 true,
//                                                                                 new StochasticUniversalSampling(),
//                                                                                 new MersenneTwisterRNG());
        //se agregan semillas a la evolucion.
        List<List<Nota>> semillas= new ArrayList();
        
//        semillas.add(Nota.individualFromString("G5q C5i D5i E5i F5i"));
//        semillas.add(Nota.individualFromString("C5q D5q E5q"));
//        semillas.add(Nota.individualFromString("C5q D5q E5q F5q"));
//        semillas.add(Nota.individualFromString("C5q D5q E5q F5q G5q"));
//        semillas.add(Nota.individualFromString("C5q D5q E5q F5q G5q A5q"));
//        semillas.add(Nota.individualFromString("C5q D5q E5q F5q G5q A5q B5q"));
//        semillas.add(Nota.individualFromString("C5q D5q E5q F5q G5q A5q B5q C5q"));
//        semillas.add(Nota.individualFromString("C5q D5q E5q F5q G5q A5q B5q C5q D5q"));
//        semillas.add(Nota.individualFromString("C5q D5q E5q F5q G5q A5q B5q C5q D5q E5q"));
//        semillas.add(Nota.individualFromString("C5q D5q E5q F5q G5q A5q B5q C5q D5q E5q F5q"));
//        semillas.add(Nota.individualFromString("C5q D5q E5q F5q G5q A5q B5q C5q D5q E5q F5q G5q"));
//        semillas.add(Nota.individualFromString("C5q D5q E5q F5q G5q A5q B5q C5q D5q E5q F5q G5q A5q"));
//        semillas.add(Nota.individualFromString("C5q D5q E5q F5q G5q A5q B5q C5q D5q E5q F5q G5q A5q B5q"));      
        
//        engine.addEvolutionObserver(new EvolutionLogger());
//        return engine.evolve(Nota.tam_generacion, 
//                             Nota.elitismo, 
//                             semillas,
//                             new TargetFitness(Nota.target_fitness, false));
        
        engine.addEvolutionObserver(new EvolutionLoggerI());
        List<Nota> ret= engine.evolve(Nota.tam_generacion, // Population size per island.
              Nota.elitismo, // Elitism for each island.
              Nota.epocas, // Epoch length (no. generations).
              Nota.migracion, // Migrations from each island at each epoch.
              new TargetFitness(Nota.target_fitness, false),new Stagnation(20, false));
        
        ms= new Date().getTime()-ms;
        System.out.println("Duracion total en s: "+ms/1000);
        return ret;
    }


    /**
     * Converts an arguments array into a single String of words
     * separated by spaces.
     * @param args The command-line arguments.
     * @return A single String made from the command line data.
     */


    /**
     * Trivial evolution observer for displaying information at the end
     * of each generation.
     */
    private static class EvolutionLogger implements EvolutionObserver<List<Nota>>
    {
        public void populationUpdate(PopulationData<? extends List<Nota>> data)
        {
            System.out.printf("Generation %d: %s\n ",
                              data.getGenerationNumber(),
                              data.getBestCandidate().toString()
                              );
            System.out.println("FITNESS: "+data.getBestCandidateFitness()+" - DESVIACION: "+data.getFitnessStandardDeviation());
            //System.out.println("LARGO MEJOR CANDIDATO: "+PartitureEvaluator.getLargoCandidato(data.getBestCandidate()));
        }
    }
        private static class EvolutionLoggerI implements IslandEvolutionObserver<List<Nota>>
    {
        public void populationUpdate(PopulationData<? extends List<Nota>> data)
        {
            System.out.printf("Epoca %d: %s\n ",
                              data.getGenerationNumber(),
                              data.getBestCandidate().toString()
                              );
            System.out.println("FITNESS: "+data.getBestCandidateFitness()+" - LARGO: "+Nota.largoPartitura(data.getBestCandidate()));
            //System.out.println("LARGO MEJOR CANDIDATO: "+PartitureEvaluator.getLargoCandidato(data.getBestCandidate()));
        }
        public void islandPopulationUpdate(int i,PopulationData<? extends List<Nota>> data)
        {
            if(data.getGenerationNumber()==Nota.epocas-1){
                System.out.printf("Isla %d: %s ",
                  i,
                  data.getBestCandidate().toString()
                  );
                System.out.println(" ---  FITNESS: "+data.getBestCandidateFitness()+" - Largo: "+Nota.largoPartitura(data.getBestCandidate()));

            }
//            System.out.printf("Generation %d %d: %s\n ",
//                                i,
//                              data.getGenerationNumber(),
//                              data.getBestCandidate().toString()
//                              );
//            System.out.println("FITNESS: "+data.getBestCandidateFitness()+" - DESVIACION: "+data.getFitnessStandardDeviation());
//            //System.out.println("LARGO MEJOR CANDIDATO: "+PartitureEvaluator.getLargoCandidato(data.getBestCandidate()));
        }
    }
    
    
}
