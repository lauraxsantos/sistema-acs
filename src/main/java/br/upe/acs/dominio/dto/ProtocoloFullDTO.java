package br.upe.acs.dominio.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProtocoloFullDTO {

	private String data;
	
	private int semestre;
	
	private int qtdCertificados;
	
	private MultipartFile protocolo;
	
	private MultipartFile[] certificados;
	
	private MultipartFile protocoloJson;
}
