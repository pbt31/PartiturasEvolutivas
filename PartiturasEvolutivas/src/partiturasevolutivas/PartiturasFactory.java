/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package partiturasevolutivas;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory;


/**
 *
 * @author ASUS_USER
 */
/**
 * Creates random polygon-based images.
 * @author Daniel Dyer
 */
public class PartiturasFactory extends AbstractCandidateFactory<List<Nota>>
{
    public static int min_tam;
    public static int max_tam;
    public int largoCandidato;

    public PartiturasFactory(int min_tam,int max_tam, int largoCandidato) 
    {
        this.min_tam = min_tam;
        this.max_tam = max_tam;
        this.largoCandidato=largoCandidato;
    }

    
    public List<Nota> generateRandomCandidate(Random rng)
    {
        List<Nota> partitura = new ArrayList<Nota>();
        int largo=0;
        int tmp=0;
        Nota n;
        int pos;
        do{
            n=crearNotaRandom(rng);
            if(n.getLength()=='w')
                tmp=16;
            else if(n.getLength()=='h')
                tmp=8;
            else if(n.getLength()=='q')    
                tmp=4;
            else if(n.getLength()=='i')
                tmp=2;
            else if(n.getLength()=='s') 
                tmp=1; 
            if(largo+tmp<=this.largoCandidato){
                if(partitura.size()==0)
                    pos=0;
                else
                    pos=rng.nextInt(partitura.size());
                partitura.add(pos,n);
                largo+=tmp;
            }
        }
        while(largo!=this.largoCandidato);
        if(partitura.size()<2){
            partitura=generateRandomCandidate(rng);
        }    
       //System.out.println(partitura);
        return partitura;
    }


    static Nota crearNotaRandom(Random rng)
    {
        char note;
        boolean sharp;
        char scale;
        char length;
        boolean half;
        
        length=Nota.alf_largo[rng.nextInt(Nota.alf_largo.length)];
        //probabilidad de punto es 0.1
        half=(rng.nextInt(10)<Nota.p_punto);
        //es silencio? Probabilidad de silencio es 0.1
        if(rng.nextInt(10)<Nota.p_rest)
            return new Nota('R',false,'1',length,half,true);
        
        note=Nota.alf_notas[rng.nextInt(Nota.alf_notas.length)];
        scale=Nota.alf_escala[rng.nextInt(Nota.alf_escala.length)];
        

        //probabilidad de sharp es 0.4
        sharp= (rng.nextInt(10)<Nota.p_sharp);
        
        
        
        Nota n=new Nota(note,sharp,scale,length,half,false);
        return n;       
       
    }
    
    public static int guessCandidateLength(Random rng,int max)
    {
        float largo= PartitureEvaluator.w1.length()/2;//no sabemos pq cuando se levanta del archivo da el doble
        
        List<Nota> partitura = new ArrayList<Nota>();
        float dif=1000;
        boolean salir=false;
        while(!salir && (dif>Nota.dif_segundos || dif<-Nota.dif_segundos)){
            partitura.add(new Nota('A',false,'3','w',false,false));
            dif=PartitureEvaluator.getLargoCandidato(partitura)-largo;
            if(dif>Nota.dif_segundos){
                partitura.remove(partitura.size()-1);
                salir=true;
            }
        }
        //System.out.println(dif);

        if(!(dif>Nota.dif_segundos || dif<-Nota.dif_segundos))
            return Nota.largoPartitura(partitura);
        salir=false;
        while(!salir && (dif>Nota.dif_segundos || dif<-Nota.dif_segundos)){
            partitura.add(new Nota('A',false,'3','h',false,false));
            dif=PartitureEvaluator.getLargoCandidato(partitura)-largo;
            if(dif>Nota.dif_segundos){
                partitura.remove(partitura.size()-1);
                salir=true;
            }
        }
        //System.out.println(dif);

        if(!(dif>Nota.dif_segundos || dif<-Nota.dif_segundos))
            return Nota.largoPartitura(partitura);
        salir=false;
        while(!salir && (dif>Nota.dif_segundos || dif<-Nota.dif_segundos)){
            partitura.add(new Nota('A',false,'3','q',false,false));
            dif=PartitureEvaluator.getLargoCandidato(partitura)-largo;
            if(dif>Nota.dif_segundos){
                partitura.remove(partitura.size()-1);
                salir=true;
            }
        }
        //System.out.println(dif);
        
        if(!(dif>Nota.dif_segundos || dif<-Nota.dif_segundos))
            return Nota.largoPartitura(partitura);
        salir=false;
        while(!salir && (dif>Nota.dif_segundos || dif<-Nota.dif_segundos)){
            partitura.add(new Nota('A',false,'3','i',false,false));
            dif=PartitureEvaluator.getLargoCandidato(partitura)-largo;
            if(dif>Nota.dif_segundos){
                partitura.remove(partitura.size()-1);
                salir=true;
            }
        }
        //System.out.println(dif);
        
        if(!(dif>Nota.dif_segundos || dif<-Nota.dif_segundos))
            return Nota.largoPartitura(partitura);
        salir=false;
        while(!salir && (dif>Nota.dif_segundos || dif<-Nota.dif_segundos)){
            partitura.add(new Nota('A',false,'3','s',false,false));
            dif=PartitureEvaluator.getLargoCandidato(partitura)-largo;
            if(dif>Nota.dif_segundos){
                partitura.remove(partitura.size()-1);
                salir=true;
            }
        }
        //System.out.println(dif);
        
        if(!(dif>Nota.dif_segundos || dif<-Nota.dif_segundos))
            return Nota.largoPartitura(partitura);
        return -1;
    }
}