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
import org.uncommons.maths.number.ConstantGenerator;
import org.uncommons.maths.number.NumberGenerator;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;

/**
 * Randomly mutates the polygons that make up an image by adding a polygon
 * according to some probability.
 * @author Daniel Dyer
 */
public class AddNotaMutation implements EvolutionaryOperator<List<Nota>>
{
    private final NumberGenerator<Probability> addNotaProbability;
    private final PartiturasFactory factory;



    /**
     * @param addNotaProbability A {@link NumberGenerator} that controls the probability
     * that a polygon will be added.
     * @param factory Used to create new polygons.
     * @param maxNotas The maximum number of polygons permitted in an image (must be at least 2).
     */
    public AddNotaMutation(NumberGenerator<Probability> addNotaProbability,
                              PartiturasFactory factory)
    {
        if (Nota.max_tam < 2)
        {
            throw new IllegalArgumentException("Max polygons must be > 1.");
        }
        this.addNotaProbability = addNotaProbability;
        this.factory = factory;
    }


    /**
     * @param addNotaProbability The probability that a polygon will be removed.
     * @param factory Used to create new polygons.
     * @param maxNotas The maximum number of polygons permitted in an image (must be at least 2).
     */
    public AddNotaMutation(Probability addNotaProbability,
                              PartiturasFactory factory,
                              int maxNotas)
    {
        this(new ConstantGenerator<Probability>(addNotaProbability),
             factory
             );
    }


    public List<List<Nota>> apply(List<List<Nota>> selectedCandidates, Random rng)
    {
        List<List<Nota>> mutatedCandidates = new ArrayList<List<Nota>>(selectedCandidates.size());
        for (List<Nota> candidate : selectedCandidates)
        {
            // A single polygon is added with the configured probability, unless
            // we already have the maximum permitted number of polygons.
            if (candidate.size() < Nota.max_tam && addNotaProbability.nextValue().nextEvent(rng))
            {
                List<Nota> newPartiture = new ArrayList<Nota>(candidate);
                newPartiture.add(rng.nextInt(newPartiture.size() + 1),
                                factory.crearNotaRandom(rng));
                mutatedCandidates.add(newPartiture);
            }
            else // Nothing changed.
            {
                mutatedCandidates.add(candidate);
            }
        }
        return mutatedCandidates;
    }


}
