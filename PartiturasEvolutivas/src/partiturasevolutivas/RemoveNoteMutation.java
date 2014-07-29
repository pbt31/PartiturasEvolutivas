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
 * Randomly mutates the polygons that make up an image by removing a polygon
 * according to some probability.
 * @author Daniel Dyer
 */
public class RemoveNoteMutation implements EvolutionaryOperator<List<Nota>>
{
    private final NumberGenerator<Probability> removeNoteProbability;

    /**
     * @param removeNoteProbability A {@link NumberGenerator} that controls the probability
     * that a polygon will be removed.
     */
    public RemoveNoteMutation(NumberGenerator<Probability> removeNoteProbability)
    {
        this.removeNoteProbability = removeNoteProbability;
    }


    /**
     * @param removeNoteProbability The probability that a polygon will be removed.
     */
    public RemoveNoteMutation(Probability removeNoteProbability)
    {
        this(new ConstantGenerator<Probability>(removeNoteProbability));
    }


    public List<List<Nota>> apply(List<List<Nota>> selectedCandidates, Random rng)
    {
        List<List<Nota>> mutatedCandidates = new ArrayList<List<Nota>>(selectedCandidates.size());
        for (List<Nota> candidate : selectedCandidates)
        {
            // A single polygon is removed with the configured probability, unless
            // we already have the minimum permitted number of polygons.
            if (candidate.size() > PartiturasFactory.min_tam
                && removeNoteProbability.nextValue().nextEvent(rng))
            {
                List<Nota> newNotes = new ArrayList<Nota>(candidate);
                newNotes.remove(rng.nextInt(newNotes.size()));
                mutatedCandidates.add(newNotes);
            }
            else // Nothing changed.
            {
                mutatedCandidates.add(candidate);
            }
        }
        return mutatedCandidates;
    }
}
