/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package partiturasevolutivas;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ASUS_USER
 */
public class Nota {
    private char note;
    private boolean sharp;
    private char scale;
    private char length;
    private boolean half;
    private boolean rest;
    
    public static final char[] alf_notas= {'A','B','C','D','E','F','G'};
    public static final char[] alf_escala= {'5','6'};//{'3','4','5','6'};
    public static final char[] alf_largo= {'q','i','h','s'};//{'w','h','q','i','s'};
    
    public static String archivo="sonido1";

    
    //parametros evolutivos
    public static int tam_generacion=25;
    public static int elitismo=2;
    public static float target_fitness=0.01f;
    public static int epocas=20;
    public static int migracion=1;
    public static int islas=4;


    
    //probabilidades 
    public static float mutacion_nota=0.09f;
    public static float mutacion_offset=0.01f;
    public static float mutacion_add_nota=0.1f;//0.03f;
    public static float mutacion_rem_nota=0.1f;//0.03f;
    public static float mutacion_join_nota=0.1f;
    public static float mutacion_split_nota=0.01f;
    
    //probabilidades que se dividen entre 10
    public static final int p_rest=0;
    public static final int p_punto=0;//1;
    public static final int p_sharp=0;//4;
    
    //probabilidades de mutacion si cae entre el numero y el anterior
//    public static final int p_m_note=6;
//    public static final int p_m_scale=10;
//    public static final int p_m_length=13;
//    public static final int p_m_rest=15;
//    public static final int p_m_punto=16;
//    public static final int p_m_sharp=20;
    public static final int p_m_note=10;
    public static final int p_m_scale=15;
    public static final int p_m_length=20;
    public static final int p_m_rest=20;
    public static final int p_m_punto=20;
    public static final int p_m_sharp=20;
    
    //tamano de partitura
    public static final int min_tam=2;
    public static final int max_tam=20;
    public static final float dif_segundos=0.1f;
    public static final float trim=1.0f;

    
    public Nota(char note, boolean sharp, char scale, char length, boolean half, boolean rest) {
        this.note = note;
        this.sharp = sharp;
        this.scale = scale;
        this.length = length;
        this.half = half;
        this.rest=rest;
    }

    public char getNote() {
        return note;
    }

    public void setNote(char note) {
        this.note = note;
    }

    public boolean isSharp() {
        return sharp;
    }

    public void setSharp(boolean sharp) {
        this.sharp = sharp;
    }
    
    public boolean isRest() {
        return rest;
    }

    public void setRest(boolean rest) {
        this.rest = rest;
    }

    public char getScale() {
        return scale;
    }

    public void setScale(char scale) {
        this.scale = scale;
    }

    public char getLength() {
        return length;
    }

    public void setLength(char length) {
        this.length = length;
    }

    public boolean isHalf() {
        return half;
    }

    public void setHalf(boolean half) {
        this.half = half;
    }

    @Override
    public boolean equals(Object obj) {
        if((this==null || obj==null))
            return false;
        
        
        Nota n=(Nota)obj;
        return (this.note == n.note) && (this.sharp == n.sharp) && (this.scale == n.scale) &&
        (this.length == n.length) && (this.half == n.half) && (this.rest==n.rest) ;
    }

    @Override
    public String toString(){
        if(this.rest)
            return "R"+this.length+(this.half ? "." : "");
        else
            return this.note+(this.sharp ? "#" : "")+this.scale+this.length+(this.half ? "." : "");
    }
    
    public static Nota fromString(String n){
        Nota ret;
        if(n.charAt(1)=='#')
            ret= new Nota(n.charAt(0),true,n.charAt(2),n.charAt(3),false,false );
        else
            ret= new Nota(n.charAt(0),false,n.charAt(1),n.charAt(2),false,false );
        return ret;
    }
    
    public static List<Nota> individualFromString(String l){
        List<Nota> ret= new ArrayList();
        String[] lista=l.split(" ");
        for(String s: lista)
            ret.add(fromString(s));
        return ret;
    }
    
    public static int largoPartitura(List<Nota> candidato){
        int largo=0;
        for(Nota n:candidato){
            if(n.length=='w')
                largo+=16;
            else if(n.length=='h')
                largo+=8;
            else if(n.length=='q')    
                largo+=4;
            else if(n.length=='i')
                largo+=2;
            else if(n.length=='s') 
                largo+=1;  
        }
        return largo;
    }

    public static int getPosTiempo(List<Nota> candidato,int tiempo){
        int largo=0;
        int pos=0;
        int ant=0;
        for(Nota n:candidato){
            ant=largo;
            if(n.length=='w')
                largo+=16;
            else if(n.length=='h')
                largo+=8;
            else if(n.length=='q')    
                largo+=4;
            else if(n.length=='i')
                largo+=2;
            else if(n.length=='s') 
                largo+=1;
            pos++;
            if(largo>=tiempo)          
                if(Math.abs(ant-tiempo)< Math.abs(largo-tiempo))
                    if(pos-1==0)
                        return 1;
                    else
                        return pos-1;
                else 
                    if(pos==candidato.size())
                        return pos-1;
                    else            
                        return pos;
            
        }
        return largo;
    }
    
    public Nota getCopia(){
        return new Nota(this.note, this.sharp, this.scale, this.length, this.half, this.rest);
    }
    
}
