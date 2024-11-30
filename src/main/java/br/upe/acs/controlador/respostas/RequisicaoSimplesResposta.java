package br.upe.acs.controlador.respostas;

import br.upe.acs.dominio.Certificado;

import br.upe.acs.dominio.Requisicao;
import br.upe.acs.dominio.enums.RequisicaoStatusEnum;
import lombok.Getter;

import java.util.Date;

@Getter
public class RequisicaoSimplesResposta {

    private final Long id;

    private final String idRequisicao;

    private final RequisicaoStatusEnum status;

    private final Date data;

    private final float quantidadeDeHoras;
    
    private final UsuarioResposta comissao;

    public RequisicaoSimplesResposta(Requisicao requisicao) {
        this.id = requisicao.getId();
        this.idRequisicao = requisicao.getToken();
        if(requisicao.getComissao() != null) {
        	this.comissao = new UsuarioResposta(requisicao.getComissao());        	
        } else {
        	this.comissao = null;
        }
        this.status = requisicao.getStatusRequisicao();
        this.data = requisicao.getDataDeSubmissao();
        this.quantidadeDeHoras = (float) requisicao.getCertificados().stream()
                .mapToDouble(Certificado::getCargaHoraria).sum();
    }
}