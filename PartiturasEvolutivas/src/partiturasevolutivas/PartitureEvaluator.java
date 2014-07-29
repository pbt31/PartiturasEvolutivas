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

import com.musicg.fingerprint.FingerprintSimilarityComputer;
import com.musicg.wave.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jfugue.Pattern;
import org.jfugue.Player;
import org.uncommons.watchmaker.framework.FitnessEvaluator;
/**
 * Fitness evalator that measures the total distance of a route in the travelling salesman
 * problem.  The fitness score of a route is the total distance (in km).  A route
 * is represented as a list of cities in the order that they will be visited.
 * The last leg of the journey is from the last city in the list back to the
 * first.
 * @author Daniel Dyer
 */
public class PartitureEvaluator implements FitnessEvaluator<List<Nota>>
{
    public static Wave w1= new Wave(Nota.archivo+".wav");
    static{
        w1.rightTrim(Nota.trim);
    }
    private static byte[] fingerprint=w1.getFingerprint();
    private static float sim= getSimilarity(fingerprint,w1);
    public static int largo_sonido_inicial;


    /**
     * @param distances Provides distances between a set of cities.
     */
    public PartitureEvaluator(int largocandidato)
    {
        largo_sonido_inicial=largocandidato;
    }


    /**
     * Calculates the length of an evolved route. 
     * @param candidate The route to evaluate.
     * @param population {@inheritDoc}
     * @return The total distance (in kilometres) of a journey that visits
     * each city in order and returns to the starting point.
     */
    public double getFitness(List<Nota> candidate,
                             List<? extends List<Nota>> population)
    {
        String cand="";
        
        for(Nota n:candidate){
            cand+=n.toString()+" ";
        }
//        final String name=System.getProperty("user.dir")+System.getProperty("file.separator")+"TEMP"+System.getProperty("file.separator")+new Date().getTime()+"_"+(new Random()).nextInt(1000000)+".wav";
        
        String name= new Date().getTime()+"_"+new Random().nextInt(10000000);
        try {
            generateSong(cand,name);
        } catch (IOException ex) {
            Logger.getLogger(PartitureEvaluator.class.getName()).log(Level.SEVERE, null, ex);
        }

        Wave w2= new Wave("/tmp/"+name+".wav");        
        w2.rightTrim(Nota.trim);//corto dos segundos
        double difLargo=Nota.largoPartitura(candidate)-largo_sonido_inicial;
        if(difLargo<0)
            difLargo= -difLargo;//en vez de ABS
        
        if(difLargo>2)
            difLargo=Math.pow(difLargo, 2);
        
        if(difLargo>4)
            difLargo=Math.pow(difLargo, 3);
//        double difLargo=w2.length()-largo;
//        if(difLargo<0)
//            difLargo= -difLargo;//en vez de ABS
//        
//        if(difLargo<Nota.dif_segundos)
//            difLargo=0;
//        else if (difLargo<=1)
//            difLargo*=100;
//        else
//            difLargo*=200;// Math.pow(difLargo, 3);
        
        double res=Math.pow(sim-getSimilarity(fingerprint,w2),5.0f)+(difLargo);       
        
        new File("/tmp/"+name+".wav").delete();
        new File("/tmp/"+name+".mid").delete();

        return res;
    }
    
    public static float getLargoCandidato(List<Nota> candidate){
        String cand="";
        for(Nota n:candidate){
            cand+=n.toString()+" ";
        }
        //System.out.println("LARGO CANDIDATO: "+cand);
        String name= new Date().getTime()+"_"+new Random().nextInt(10000000);

        try {
           generateSong(cand,name);
        } catch (IOException ex) {
            Logger.getLogger(PartitureEvaluator.class.getName()).log(Level.SEVERE, null, ex);
        }
        Wave w2= new Wave("/tmp/"+name+".wav");
        w2.rightTrim(Nota.trim);
        return w2.length();
    }

    
    /**
     * {@inheritDoc}
     * Returns false since shorter distances represent fitter candidates.
     * @return false
     */
    public boolean isNatural()
    {
        return false;
    }
    
    static Semaphore s= new Semaphore(1);
    static void generateSong(String pat,String file) throws IOException{
        try {
            File f = new File("/tmp/"+file+".mid");
            f.createNewFile();
            
            s.acquire();
            Player player = new Player();
            Pattern pattern1 = new Pattern(pat);
            Pattern song = new Pattern();
            song.add(pattern1);
            
            player.saveMidi(song,f);
            player.close();
            s.release();
            
            Process p = Runtime.getRuntime().exec ("/ens/home01/ae2014_03/Desktop/PartiturasEvolutivas/lib/bin/timidity -Ow -s 10240 -o /tmp/"+file+".wav /tmp/"+file+".mid"); 
            p.waitFor();
            p.destroy();

        } catch (InterruptedException ex) {
            Logger.getLogger(PartitureEvaluator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    static float getSimilarity(byte[] fw1,Wave w2){//ACA pasar el fingerprint del w1 en vez del w1
//        try{
            FingerprintSimilarityComputer fingerprintSimilarityComputer=new FingerprintSimilarityComputer(fw1,w2.getFingerprint());
            return fingerprintSimilarityComputer.getFingerprintsSimilarity().getScore();
//        }
//        catch (Exception e){
//            System.out.println("Exploto");
//            return 0.0f;
//        }
    }
}
