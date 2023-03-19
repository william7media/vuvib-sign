package vuvibsign.signature.domain.service;

import org.springframework.stereotype.Service;
import vuvibsign.signature.application.delivery.LoadFirmaUseCase;
import vuvibsign.signature.application.gateway.FirmaApiPort;
import vuvibsign.signature.domain.model.FirmaInfo;

import java.util.List;

@Service
public class LoadFirmaService implements LoadFirmaUseCase {

    private final FirmaApiPort firmaApiPort;

    public LoadFirmaService(FirmaApiPort firmaApiPort) {
        this.firmaApiPort = firmaApiPort;
    }

    @Override
    public List<FirmaInfo> getByPerfil(Long idPerfilUsuario) {
        return firmaApiPort.findByIdPerfil(idPerfilUsuario);
    }

    @Override
    public List<FirmaInfo> getByUsuario(Long idUsuario) {
        return firmaApiPort.findByIdUsuario(idUsuario);
    }

    @Override
    public FirmaInfo getByPerfilAndDocument(Long idPerfilUsuario, Long idDocumento) {
        return firmaApiPort
                .findByIdPerfil(idPerfilUsuario)
                .stream()
                .filter(detalleFirma -> detalleFirma.getIdDocumento().equals(idDocumento))
                .findAny().orElse(null);
    }

}
