package vuvibsign.signature.domain.service;

import lombok.extern.slf4j.Slf4j;
import net.sf.jsignpdf.SignerLogic;
import net.sf.jsignpdf.SignerOptionsFromCmdLine;
import net.sf.jsignpdf.ssl.SSLInitializer;
import net.sf.jsignpdf.utils.ConfigProvider;
import net.sf.jsignpdf.utils.PKCS11Utils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import vuvibsign.security.application.delivery.StorageKeystoreUseCase;
import vuvibsign.security.domain.model.Keystore;
import vuvibsign.shared.util.Messages;
import vuvibsign.signature.application.delivery.LoadFirmaUseCase;
import vuvibsign.signature.application.delivery.ManageDocumentUseCase;
import vuvibsign.signature.application.delivery.SignDocumentUseCase;
import vuvibsign.signature.domain.model.FirmaInfo;
import vuvibsign.signature.domain.model.Position;
import vuvibsign.signature.infrastructure.config.ESignerConfig;

import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

@Service
@Slf4j
public class SignDocumentService implements SignDocumentUseCase {

    public static final String TEMP_DIRECTORY = "java.io.tmpdir";
    public static final String FILE_SUFFIX = ".pdf";
    public static final String NO_SUCH_ALGORITHM_ERROR = "Algoritmo de cifrado no encontrado.";
    public static final String KEY_MANAGEMENT_ERROR = "Error de clave de firma.";
    public static final String KEY_STORE_ERROR = "Error de almacén de claves.";
    public static final String CERTIFICATE_ERROR = "Error de certificado digital.";
    public static final String INPUT_OUTPUT_ERROR = "Error de lectura/escritura de un archivo.";
    public static final String KEYSTORE_NOT_FOUND = "Keystore no encontrado.";
    public static final String DOCUMENT_NOT_FOUND = "Documento no encontrado.";
    private final StorageKeystoreUseCase storageKeystoreUseCase;
    private final LoadFirmaUseCase loadFirmaUseCase;
    private final ManageDocumentUseCase manageDocumentUseCase;

    public SignDocumentService(StorageKeystoreUseCase storageKeystoreUseCase,
                               LoadFirmaUseCase loadFirmaUseCase,
                               ManageDocumentUseCase manageDocumentUseCase) {
        this.storageKeystoreUseCase = storageKeystoreUseCase;
        this.loadFirmaUseCase = loadFirmaUseCase;
        this.manageDocumentUseCase = manageDocumentUseCase;
    }

    private Path handleUploadFile(String directory,
                                  byte[] bytes,
                                  String prefix,
                                  String suffix)
            throws IOException {
        String tmpDir = System.getProperty(TEMP_DIRECTORY);
        Path tmpDirPath = Path.of(tmpDir, directory);
        Files.createDirectories(tmpDirPath);
        Path tempFilePath = Files.createTempFile(tmpDirPath, prefix, suffix);
        BufferedOutputStream stream =
                new BufferedOutputStream(
                        new FileOutputStream(tempFilePath.toFile()));
        FileCopyUtils.copy(new ByteArrayInputStream(bytes), stream);
        stream.close();
        return tempFilePath;
    }

    private Path handleSignedFile(String directory) throws IOException {
        String tmpDir = System.getProperty(TEMP_DIRECTORY);
        Path tmpDirPath = Path.of(tmpDir, directory);
        return Files.createDirectories(tmpDirPath);
    }

    private SignerOptionsFromCmdLine initOptions(
            byte[] keystoreFile,
            String keystorePass,
            byte[] documentFile,
            Position position)
            throws IOException {

        SignerOptionsFromCmdLine options = new SignerOptionsFromCmdLine();
        // Directorio de keystore
        Path keystoreFilePath =
                this.handleUploadFile(
                        ESignerConfig.KEYSTORE_DIRECTORY,
                        keystoreFile,
                        ESignerConfig.PREFIX,
                        ESignerConfig.KEYSTORE_SUFFIX);
        options.setKsFile(keystoreFilePath.toAbsolutePath().toString());
        options.setKsPasswd(keystorePass);
        // Directorio de archivos no firmados
        Path unsignedFilePath =
                this.handleUploadFile(
                        ESignerConfig.UNSIGNED_DIRECTORY,
                        documentFile,
                        ESignerConfig.PREFIX,
                        ESignerConfig.DOCUMENT_SUFFIX);
        String[] unsignedFiles =
                new String[]{unsignedFilePath.toAbsolutePath().toString()};
        options.setFiles(unsignedFiles);
        // Directorio de archivos firmados
        Path signedFilePath =
                this.handleSignedFile(ESignerConfig.SIGNED_DIRECTORY);
        options.setOutPath(signedFilePath.toAbsolutePath().toString());
        // Mas opciones
        options.setKsType(ESignerConfig.KEYSTORE_TYPE);
        options.setAppend(ESignerConfig.SIGNATURE_APPEND);
        options.setHashAlgorithm(ESignerConfig.HASH_ALGORITHM);
        options.setVisible(ESignerConfig.SIGNATURE_VISIBLE);
        options.setPage(position.getPage());
        options.setL2TextFontSize(ESignerConfig.FONT_SIZE.floatValue());
        // Posición de la firma en el archivo PDF
        options.setPositionLLX(position.getLowLeftX());
        options.setPositionLLY(position.getLowLeftY());
        options.setPositionURX(position.getUpperRightX());
        options.setPositionURY(position.getUpperRightY());

        return options;
    }

    @Override
    public String signDocument(
            String username,
            String idPerfil,
            String idDocumento,
            Position position)
            throws IOException {

        Keystore keystore =
                storageKeystoreUseCase.findByUsername(username)
                        .orElseThrow(() -> new RuntimeException(KEYSTORE_NOT_FOUND));

        FirmaInfo firmaInfo = loadFirmaUseCase.getByPerfilAndDocument(Long.valueOf(idPerfil), Long.valueOf(idDocumento));
        URI uri = URI.create(firmaInfo.getRutaDocumento());
        log.info(uri.getPath());

        Resource documentResource =
                manageDocumentUseCase
                        .downloadResource(firmaInfo.getRutaDocumento())
                        .orElseThrow(() -> new RuntimeException(DOCUMENT_NOT_FOUND));

        byte[] documentBytes = documentResource.getInputStream().readAllBytes();
        Resource keystoreResource =
                storageKeystoreUseCase.load(keystore.getKeystoreUrl())
                        .orElseThrow(() -> new RuntimeException(KEYSTORE_NOT_FOUND));
        byte[] keystoreBytes = keystoreResource.getInputStream().readAllBytes();

        return this.signDocument(keystoreBytes, keystore.getKeystorePass(), documentBytes, position);
    }

    @Override
    public String signDocument(
            byte[] keystoreFile,
            String keystorePass,
            byte[] documentFile,
            Position position)
            throws IOException {

        SignerOptionsFromCmdLine options =
                initOptions(keystoreFile, keystorePass, documentFile, position);

        return signDocumentByOptions(options);
    }

    private String signDocumentByOptions(@NonNull SignerOptionsFromCmdLine options) {
        try {
            SSLInitializer.init();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(NO_SUCH_ALGORITHM_ERROR, e);
        } catch (KeyManagementException e) {
            throw new RuntimeException(KEY_MANAGEMENT_ERROR, e);
        } catch (KeyStoreException e) {
            throw new RuntimeException(KEY_STORE_ERROR, e);
        } catch (CertificateException e) {
            throw new RuntimeException(CERTIFICATE_ERROR, e);
        } catch (IOException e) {
            throw new RuntimeException(INPUT_OUTPUT_ERROR, e);
        }

        PKCS11Utils.
                registerProviders(
                        ConfigProvider
                                .getInstance()
                                .getProperty("pkcs11config.path"));

        String signedFilePath = null;
        if (ArrayUtils.isNotEmpty(options.getFiles())
                || (!StringUtils.isEmpty(options.getInFile())
                && !StringUtils.isEmpty(options.getOutFile()))) {
            this.signFiles(options);
            //Fin ejecución exitosa, retorna ruta del archivo firmado.
            //return tmpOpts.getOutPath();
            signedFilePath = options.getOutFile();
        } else if (options.isPrintVersion()
                || options.isPrintHelp()
                || options.isListKeyStores()
                || options.isListKeys()) {
            exit(Messages.NO_VALID_COMMAND_PROVIDED);
        }
        //log.info(traceInfo());
        return signedFilePath;
    }

    private void signFiles(SignerOptionsFromCmdLine options) {

        SignerLogic signerLogic = new SignerLogic(options);
        String outPath = options.getOutPath();
        String outPrefix = options.getOutPrefix();
        String outSuffix = options.getOutSuffix() + FILE_SUFFIX;

        int successCount = 0;
        int failedCount = 0;
        int totalCount = 0;

        for (String wildcardPath : options.getFiles()) {

            File wildcardFile = new File(wildcardPath);
            File[] inputFiles;

            if (StringUtils.containsAny(wildcardFile.getName(), '*', '?')) {
                File inputFolder = wildcardFile.getAbsoluteFile().getParentFile();
                FileFilter fileFilter =
                        new AndFileFilter(
                                FileFileFilter.INSTANCE,
                                new WildcardFileFilter(wildcardFile.getName())
                        );
                inputFiles = inputFolder.listFiles(fileFilter);

                if (inputFiles == null) {
                    continue;
                }
            } else {
                inputFiles = new File[]{wildcardFile};
            }

            for (File inputFile : inputFiles) {
                String tmpInFile = inputFile.getPath();

                if (!inputFile.canRead()) {
                    failedCount++;
                    log.error("Archivo no legible! " + tmpInFile);
                    continue;
                }

                options.setInFile(tmpInFile);
                String tmpNameBase = FilenameUtils.getBaseName(inputFile.getName());
                String tmpExtension = FilenameUtils.getExtension(inputFile.getName());
                String tmpSuffix = !tmpExtension.isEmpty() ? outSuffix : "." + tmpExtension + outSuffix;
                String tmpName = outPath + outPrefix + tmpNameBase + tmpSuffix;
                options.setOutFile(tmpName);

                if (signerLogic.signFile())
                    successCount++;
                else
                    failedCount++;

                totalCount++;
            }
        }

        if (totalCount == 0) {
            if (!signerLogic.signFile())
                exit(Messages.ALL_SIGN_FAILED);
        } else if (failedCount == 0) {
            log.info(Messages.ALL_SIGN_SUCCESSFUL);
        } else if (successCount == 0) {
            log.error(Messages.ALL_SIGN_FAILED);
            exit(Messages.ALL_SIGN_FAILED);
        } else {
            log.error(Messages.SOME_SIGN_FAILED);
            exit(Messages.SOME_SIGN_FAILED);
        }
    }

    private void exit(String message) {
        PKCS11Utils.unregisterProviders();
        throw new RuntimeException(message);
    }

}
