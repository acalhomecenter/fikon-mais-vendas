package br.com.acalfortaleza.fikonmais.models;

import java.util.Comparator;

public class Cupom {
    public String Numnota;
    public String Cgccpf;
    public String Cliente;
    public double Valor;
    public String Data;
    public String Pdv;

    public static final Comparator<Cupom> ORDER_AZ = new Comparator<Cupom>() {
        @Override
        public int compare(Cupom dev, Cupom t1) {

            return dev.Cliente.compareTo(t1.Cliente);
        }
    };


    public static final Comparator<Cupom> ORDER_ZA= new Comparator<Cupom>() {
        @Override
        public int compare(Cupom cup, Cupom t1) {

            return t1.Cliente.compareTo(cup.Cliente);
        }
    };


    public static final Comparator<Cupom> ORDER_AZ_NOTA = new Comparator<Cupom>() {
        @Override
        public int compare(Cupom cup, Cupom t1) {

            return Double.compare(Double.parseDouble(cup.Numnota),Double.parseDouble(t1.Numnota));
        }
    };


    public static final Comparator<Cupom> ORDER_ZA_NOTA = new Comparator<Cupom>() {
        @Override
        public int compare(Cupom cup, Cupom t1) {

            return Double.compare(Double.parseDouble(t1.Numnota),Double.parseDouble(cup.Numnota));
        }
    };


    public static final Comparator<Cupom> ORDER_VALOR = new Comparator<Cupom>() {
        @Override
        public int compare(Cupom cup, Cupom t1) {

            return Double.compare(cup.Valor,t1.Valor);
        }
    };


    public static final Comparator<Cupom> ORDER_VALORDESC = new Comparator<Cupom>() {
        @Override
        public int compare(Cupom cup, Cupom t1) {

            return Double.compare(t1.Valor,cup.Valor);
        }
    };
}
