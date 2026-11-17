package br.com.acalfortaleza.fikonmais.models.Metas;

public class ComissaoVariavelProduto {
    public String FilCod;
    public String Filial;
    public String Cpf;
    public Integer CodigoVen;
    public String Consultor;
    public String Funcao; // Novo
    public Double Vendas;
    public Double Devolucoes;
    public Double VendaLiquida;
    public Double ComissaoNova;
    public Double Margem;
    public String Matricula; // Novo
    public Integer DiasUteis; // Novo
    public Integer Repousos; // Novo
    public Integer Feriados; // Novo
    public Double SalarioBase; // Novo
    public String Situacao; // Novo
    public Double Dsr;
    public Double DsrFeriados;
    public Double DiaFeriado;
    public Integer CarCodigo; // Novo
    public String Cargo; // Novo
    public String Estado; // Novo
    public Integer Atendimentos; // Novo
    public Integer Skus; // Novo
    public Double AjudaCusto; // Novo
    public Double PremioIncentivo;
    
    // TotalReceber: Não mapeado na API atual, removido
    // Chave, Sku: Removidos
    public Double TotalReceber; // Mantendo caso seja calculado ou reapareça, mas default null
}
