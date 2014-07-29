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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.uncommons.maths.number.NumberGenerator;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;

/**
 * Base class for mutation operators that modify the points of polygons in an
 * image.
 * @author Daniel Dyer
 */
public class SplitNotesMutation implements EvolutionaryOperator<Nota>
{
    private final NumberGenerator<Probability> mutationProbability;


    /**
     * @param mutationProbability A {@link NumberGenerator} that controls the probability
     * that a polygon's points will be mutated.
     * @param canvasSize The size of the canvas.  Used to constrain the positions of the points.
     */
    public SplitNotesMutation(NumberGenerator<Probability> mutationProbability)
    {
        this.mutationProbability = mutationProbability;
    }


    /**
     * @return The dimensions of the target image.
     */



    /**
     * @return The {@link NumberGenerator} that provides the mutation probability.
     */
    protected NumberGenerator<Probability> getMutationProbability()
    {
        return mutationProbability;
    }


    /**
     * Applies the mutation to each polygon in the list provided according to the
     * pre-configured mutation probability.  If the probability is 0.1, approximately
     * 10% of the individuals will be mutated.  The actual mutation operation is
     * defined in the sub-class implementation of the
     * {@link #mutateVertices(java.util.List, java.util.Random)} method.
     * @param partitura The list of polygons to be mutated.
     * @param rng A source of randomness.
     * @return The polygons after mutation.  None, some or all will have been
     * modified. 
     */
    public List<Nota> apply(List<Nota> partitura, Random rng)
    {
        List<Nota> nuevaPartitura= new ArrayList<>();

        if (getMutationProbability().nextValue().nextEvent(rng)){      
            int posAJuntar= rng.nextInt(partitura.size()-1);
            for (int i=0;i<partitura.size();i++) {
                    Nota n;
                    if(i==posAJuntar){
                        n=partitura.get(i).getCopia();
                        if(n.getLength()=='w')
                            n.setLength('h');
                        else if(n.getLength()=='h')
                            n.setLength('q');
                        else if(n.getLength()=='q')    
                            n.setLength('i');
                        else if(n.getLength()=='i')
                            n.setLength('s');
                        else if(n.getLength()=='s') 
                            n.setLength('i');  
                        nuevaPartitura.add(n);
                        nuevaPartitura.add(n.getCopia());
                    }
                    else
                        nuevaPartitura.add(partitura.get(i).getCopia());
            }
            //System.out.println("Partitura mutada= "+nuevaPartitura);
            return nuevaPartitura;
        }
        else
            return new ArrayList<>(partitura);
    }
}
