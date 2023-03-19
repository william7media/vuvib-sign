package vuvibsign.signature.infrastructure.delivery.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vuvibsign.signature.application.delivery.LoadFirmaUseCase;
import vuvibsign.signature.domain.model.FirmaInfo;
import vuvibsign.shared.exception.ResourceNotFoundException;

import java.util.List;

@RestController
@RequestMapping(value = "/signer/api/v1/detallefirma")
@CrossOrigin(origins = "*", maxAge = 3600)
public class LoadFirmaController {

    private final LoadFirmaUseCase loadFirmaUseCase;

    public LoadFirmaController(LoadFirmaUseCase loadFirmaUseCase) {
        this.loadFirmaUseCase = loadFirmaUseCase;
    }

    @GetMapping(value = "/perfil/{idPerfilUsuario}")
    public ResponseEntity<List<FirmaInfo>> getByIdPerfilUsuario(@PathVariable Long idPerfilUsuario) {

        List<FirmaInfo> response = loadFirmaUseCase.getByPerfil(idPerfilUsuario);

        if (response == null)
            throw new ResourceNotFoundException("Firma no encontrado [idPerfilUsuario]: " + idPerfilUsuario);

        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/usuario/{idUsuario}")
    public ResponseEntity<List<FirmaInfo>> getByIdUsuario(@PathVariable Long idUsuario) {

        List<FirmaInfo> response = loadFirmaUseCase.getByUsuario(idUsuario);

        if (response == null)
            throw new ResourceNotFoundException("Firma no encontrado [idUsuario]: " + idUsuario);

        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/perfil/{idPerfilUsuario}/documento/{idDocumento}")
    public ResponseEntity<FirmaInfo> getByIdPerfilUsuarioByIdDocument(@PathVariable Long idPerfilUsuario,
                                                                      @PathVariable Long idDocumento) {
        FirmaInfo response =
                loadFirmaUseCase.getByPerfilAndDocument(idPerfilUsuario, idDocumento);

        if (response == null)
            throw new ResourceNotFoundException(
                    String.format("Detalle Firma no encontrado [idPerfilUsuario, idDocumento]: %s, %s",
                            idPerfilUsuario,
                            idDocumento));

        return ResponseEntity.ok(response);
    }

}
