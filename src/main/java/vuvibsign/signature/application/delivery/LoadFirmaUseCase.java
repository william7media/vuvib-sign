package vuvibsign.signature.application.delivery;

import vuvibsign.signature.domain.model.FirmaInfo;

import java.util.List;

public interface LoadFirmaUseCase {

    List<FirmaInfo> getByPerfil(Long idPerfilUsuario);

    List<FirmaInfo> getByUsuario(Long idUsuario);

    FirmaInfo getByPerfilAndDocument(Long idPerfilUsuario, Long idDocumento);

}
