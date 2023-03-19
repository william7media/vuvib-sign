package vuvibsign.signature.application.gateway;

import vuvibsign.signature.domain.model.FirmaInfo;

import java.util.List;
import java.util.Optional;

public interface FirmaApiPort {

    List<FirmaInfo> findByIdPerfil(Long idPerfilUsuario);

    List<FirmaInfo> findByIdUsuario(Long idUsuario);

    Optional<FirmaInfo> changeToSignedStatus(String idDetalleFirma);
}
