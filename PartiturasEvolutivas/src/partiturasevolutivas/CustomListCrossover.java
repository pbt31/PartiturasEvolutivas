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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.uncommons.maths.number.NumberGenerator;
import org.uncommons.watchmaker.framework.operators.AbstractCrossover;

/**
 * Performs cross-over between Sudoku grids by re-combining rows from parents
 * to form new offspring.  Rows are copied intact, only columns are disrupted
 * by this cross-over.
 * @author Daniel Dyer
 */
public class CustomListCrossover extends AbstractCrossover<List<Nota>>
{
    /**
     * Single-point cross-over.
     */
    public CustomListCrossover()
    {
        this(1);
    }


    /**
     * Multiple-point cross-over (fixed number of points).
     * @param crossoverPoints The fixed number of cross-overs applied to each
     * pair of parents.
     */
    public CustomListCrossover(int crossoverPoints)
    {
        super(crossoverPoints);
    }


    /**
     * Multiple-point cross-over (variable number of points).
     * @param crossoverPointsVariable Provides the (possibly variable) number of
     * cross-overs applied to each pair of parents.
     */
    public CustomListCrossover(NumberGenerator<Integer> crossoverPointsVariable)
    {
        super(crossoverPointsVariable);
    }


    /**
     * Applies cross-over to a pair of parents.  Cross-over is performed vertically
     * (each offspring consists of some rows from {@literal parent1} and some rows
     * from {@literal parent2}).
     * @param parent1 The first parent.
     * @param parent2 The second parent.
     * @param numberOfCrossoverPoints The number of cross-overs to perform.
     * @param rng The RNG used to select the cross-over points.
     * @return A list containing a pair of offspring.
     */
    @Override
    protected List<List<Nota>> mate(List<Nota> parent1,
                                List<Nota> parent2,
                                int numberOfCrossoverPoints,
                                Random rng)
    {
        List<Nota> offspring1 = new ArrayList<Nota>(parent1);
        List<Nota> offspring2 = new ArrayList<Nota>(parent2);
        
        for (int i = 0; i < numberOfCrossoverPoints; i++)
        {
            int l1=Nota.largoPartitura(offspring1);
            int l2=Nota.largoPartitura(offspring2);
            int min = Math.min(l1,l2);
            
            if (min > 1) // Don't perform cross-over if there aren't at least 2 elements in each list.
            {
                int rnd=rng.nextInt(min);
                int crossoverIndex1 = (Nota.getPosTiempo(offspring1, rnd));
                int crossoverIndex2 = (Nota.getPosTiempo(offspring2, rnd));
                
                List<Nota> temp1=offspring1.subList(crossoverIndex1, offspring1.size());
                temp1= new ArrayList<Nota>(temp1);
                for (int j=crossoverIndex1;j<offspring1.size();j++)
                    offspring1.remove(j);
                
                List<Nota> temp2=offspring2.subList(crossoverIndex2, offspring2.size());
                temp2= new ArrayList<Nota>(temp2);
                for (int j=crossoverIndex2;j<offspring2.size();j++)
                    offspring2.remove(j);
                
                offspring1.addAll(temp2);
                offspring2.addAll(temp1);
            }
        }
        List<List<Nota>> result = new ArrayList<List<Nota>>(2);
        result.add(offspring1);
        result.add(offspring2);
        return result;
    }
}
