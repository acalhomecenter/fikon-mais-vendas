package br.com.acalfortaleza.fikonmais.helper;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * A Fikon devolve a UF pelo nome ("GOIÁS") quando o CEP está na tabela do ERP
 * e pela sigla ("GO") quando vem do ViaCEP. O app trabalha só com a sigla.
 */
public final class UfHelper {
    private static final Map<String, String> SIGLAS = new HashMap<>();

    static {
        SIGLAS.put("ACRE", "AC");
        SIGLAS.put("ALAGOAS", "AL");
        SIGLAS.put("AMAPA", "AP");
        SIGLAS.put("AMAZONAS", "AM");
        SIGLAS.put("BAHIA", "BA");
        SIGLAS.put("CEARA", "CE");
        SIGLAS.put("DISTRITO FEDERAL", "DF");
        SIGLAS.put("ESPIRITO SANTO", "ES");
        SIGLAS.put("GOIAS", "GO");
        SIGLAS.put("MARANHAO", "MA");
        SIGLAS.put("MATO GROSSO", "MT");
        SIGLAS.put("MATO GROSSO DO SUL", "MS");
        SIGLAS.put("MINAS GERAIS", "MG");
        SIGLAS.put("PARA", "PA");
        SIGLAS.put("PARAIBA", "PB");
        SIGLAS.put("PARANA", "PR");
        SIGLAS.put("PERNAMBUCO", "PE");
        SIGLAS.put("PIAUI", "PI");
        SIGLAS.put("RIO DE JANEIRO", "RJ");
        SIGLAS.put("RIO GRANDE DO NORTE", "RN");
        SIGLAS.put("RIO GRANDE DO SUL", "RS");
        SIGLAS.put("RONDONIA", "RO");
        SIGLAS.put("RORAIMA", "RR");
        SIGLAS.put("SANTA CATARINA", "SC");
        SIGLAS.put("SAO PAULO", "SP");
        SIGLAS.put("SERGIPE", "SE");
        SIGLAS.put("TOCANTINS", "TO");
    }

    private UfHelper() {
    }

    /** Devolve a sigla da UF; valores já em sigla ou desconhecidos voltam como vieram. */
    public static String sigla(String uf) {
        if (uf == null) {
            return null;
        }
        String trimmed = uf.trim();
        if (trimmed.length() <= 2) {
            return trimmed.toUpperCase(Locale.ROOT);
        }
        String key = Normalizer.normalize(trimmed, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toUpperCase(Locale.ROOT);
        String sigla = SIGLAS.get(key);
        return sigla == null ? trimmed : sigla;
    }
}
