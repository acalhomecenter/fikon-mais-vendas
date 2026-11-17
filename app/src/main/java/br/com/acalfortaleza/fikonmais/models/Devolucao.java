package br.com.acalfortaleza.fikonmais.models;

import java.util.Comparator;

public class Devolucao {
    public String Tipo;
    public String Numnota;
    public String Cgccpf;
    public String Cliente;
    public double Valor;
    public String Data;
    public String Filial;

    public static final Comparator<Devolucao> ORDER_AZ = new Comparator<Devolucao>() {
        @Override
        public int compare(Devolucao dev, Devolucao t1) {

            return dev.Cliente.compareTo(t1.Cliente);
        }
    };


    public static final Comparator<Devolucao> ORDER_ZA= new Comparator<Devolucao>() {
        @Override
        public int compare(Devolucao dev, Devolucao t1) {

            return t1.Cliente.compareTo(dev.Cliente);
        }
    };


    public static final Comparator<Devolucao> ORDER_AZ_NOTA = new Comparator<Devolucao>() {
        @Override
        public int compare(Devolucao dev, Devolucao t1) {

            return Double.compare(Double.parseDouble(dev.Numnota),Double.parseDouble(t1.Numnota));
        }
    };


    public static final Comparator<Devolucao> ORDER_ZA_NOTA = new Comparator<Devolucao>() {
        @Override
        public int compare(Devolucao dev, Devolucao t1) {

            return Double.compare(Double.parseDouble(t1.Numnota),Double.parseDouble(dev.Numnota));
        }
    };


    public static final Comparator<Devolucao> ORDER_VALOR = new Comparator<Devolucao>() {
        @Override
        public int compare(Devolucao dev, Devolucao t1) {

            return Double.compare(dev.Valor,t1.Valor);
        }
    };


    public static final Comparator<Devolucao> ORDER_VALORDESC = new Comparator<Devolucao>() {
        @Override
        public int compare(Devolucao dev, Devolucao t1) {

            return Double.compare(t1.Valor,dev.Valor);
        }
    };

}
