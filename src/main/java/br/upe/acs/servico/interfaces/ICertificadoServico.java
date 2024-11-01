package br.upe.acs.servico.interfaces;

import org.springframework.web.multipart.MultipartFile;

import br.upe.acs.dominio.Certificado;
import br.upe.acs.dominio.dto.CertificadoDTO;

public interface ICertificadoServico {
	
	public Certificado buscarCertificadoPorId(Long id);
	
	public byte[] buscarPdfDoCertificadoPorId(Long certificadoId);
	
	public Long adicionarCertificado(MultipartFile file, Long requisicaoId, String email);
	
	public void alterarCertificado(Long certificadoId, CertificadoDTO certificadoDTO, String email);
	
	public void excluirCertificado(Long certificadoId, String email);

}
