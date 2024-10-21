package br.upe.acs.dominio.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CertificadoDTO {

	private String titulo;
		
	private String dataInicial;

	private String dataFinal;
	
	private float quantidadeDeHoras;
	
	private Long atividadeId;
}
