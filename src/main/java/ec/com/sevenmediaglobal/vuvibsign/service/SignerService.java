package ec.com.sevenmediaglobal.vuvibsign.service;

import ec.com.sevenmediaglobal.vuvibsign.exception.SignerPdfServiceException;
import ec.com.sevenmediaglobal.vuvibsign.util.Messages;
import lombok.Getter;
import lombok.Setter;
import net.sf.jsignpdf.SignerLogic;
import net.sf.jsignpdf.SignerOptionsFromCmdLine;
import net.sf.jsignpdf.ssl.SSLInitializer;
import net.sf.jsignpdf.utils.ConfigProvider;
import net.sf.jsignpdf.utils.PKCS11Utils;
import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Provider;
import java.security.Security;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static net.sf.jsignpdf.Constants.LOGGER;
import static net.sf.jsignpdf.Constants.RES;

@Service
@Getter
@Setter
public class SignerService {

    @Autowired
    public SignerService() {
    }

    public Path handleUpload(String directory, byte[] bytes, String prefix, String suffix) throws IOException {
        String tmpDir = System.getProperty("java.io.tmpdir");
        Path tmpDirPath = Path.of(tmpDir, directory);
        Files.createDirectories(tmpDirPath);
        Path tempFilePath = Files.createTempFile(tmpDirPath, prefix, suffix);
        BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(tempFilePath.toFile()));
        FileCopyUtils.copy(new ByteArrayInputStream(bytes), stream);
        stream.close();
        return tempFilePath;
    }

    public Path handleSignedPath(String directory) throws IOException {
        String tmpDir = System.getProperty("java.io.tmpdir");
        Path tmpDirPath = Path.of(tmpDir, directory);
        return Files.createDirectories(tmpDirPath);
    }

    public void signDocument(@NonNull SignerOptionsFromCmdLine tmpOpts) throws SignerPdfServiceException {

        try {
            SSLInitializer.init();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, Messages.UNABLE_RECONFIGURE_SSL_LAYER, e);
            throw new SignerPdfServiceException(Messages.UNABLE_RECONFIGURE_SSL_LAYER, e);
        }

        PKCS11Utils.registerProviders(ConfigProvider.getInstance().getProperty("pkcs11config.path"));
        traceInfo();

        if (ArrayUtils.isNotEmpty(tmpOpts.getFiles())
                || (!StringUtils.isEmpty(tmpOpts.getInFile()) && !StringUtils.isEmpty(tmpOpts.getOutFile()))) {
            signFiles(tmpOpts);
            //Fin ejecución exitosa
        } else {
            final boolean tmpCommand = tmpOpts.isPrintVersion() || tmpOpts.isPrintHelp() || tmpOpts.isListKeyStores()
                    || tmpOpts.isListKeys();
            if (!tmpCommand) {
                exit(Messages.NO_VALID_COMMAND_PROVIDED);
            }
            //Fin ejecución exitosa
        }

    }

    /**
     * Writes info about security providers to the {@link Logger} instance. The log-level for messages is FINER.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void traceInfo() throws SignerPdfServiceException {
        if (LOGGER.isLoggable(Level.FINER)) {
            try {
                Provider[] aProvider = Security.getProviders();
                for (int i = 0; i < aProvider.length; i++) {
                    Provider provider = aProvider[i];
                    LOGGER.finer("Provider " + (i + 1) + " : " + provider.getName() + " " + provider.getInfo() + " :");
                    List keyList = new ArrayList(provider.keySet());
                    try {
                        Collections.sort(keyList);
                    } catch (Exception e) {
                        LOGGER.log(Level.FINER, Messages.PROPERTIES_KEYS_CANT_BE_SORTED, e);
                        throw new SignerPdfServiceException(Messages.PROPERTIES_KEYS_CANT_BE_SORTED, e);
                    }
                    for (Object o : keyList) {
                        String key = (String) o;
                        LOGGER.finer(key + ": " + provider.getProperty(key));
                    }
                    LOGGER.finer("------------------------------------------------");
                }
            } catch (Exception e) {
                LOGGER.log(Level.FINER, Messages.LISTING_SECURITY_PROVIDERS_FAILED, e);
                throw new SignerPdfServiceException(Messages.LISTING_SECURITY_PROVIDERS_FAILED, e);
            }
        }
    }

    /**
     * Sign the files
     *
     * @param anOpts
     */
    private void signFiles(SignerOptionsFromCmdLine anOpts) throws SignerPdfServiceException {
        final SignerLogic tmpLogic = new SignerLogic(anOpts);
        if (ArrayUtils.isEmpty(anOpts.getFiles())) {
            // we've used -lp (loadproperties) parameter
            if (!tmpLogic.signFile()) {
                exit(Messages.ALL_SIGN_FAILED);
            }
            return;
        }
        int successCount = 0;
        int failedCount = 0;

        for (final String wildcardPath : anOpts.getFiles()) {
            final File wildcardFile = new File(wildcardPath);

            File[] inputFiles;
            if (StringUtils.containsAny(wildcardFile.getName(), '*', '?')) {
                final File inputFolder = wildcardFile.getAbsoluteFile().getParentFile();
                final FileFilter fileFilter = new AndFileFilter(FileFileFilter.INSTANCE,
                        new WildcardFileFilter(wildcardFile.getName()));
                inputFiles = inputFolder.listFiles(fileFilter);
                if (inputFiles == null) {
                    continue;
                }
            } else {
                inputFiles = new File[]{wildcardFile};
            }
            for (File inputFile : inputFiles) {
                final String tmpInFile = inputFile.getPath();
                if (!inputFile.canRead()) {
                    failedCount++;
                    System.err.println(RES.get("file.notReadable", tmpInFile));
                    continue;
                }
                anOpts.setInFile(tmpInFile);
                String tmpNameBase = inputFile.getName();
                String tmpSuffix = ".pdf";
                if (StringUtils.endsWithIgnoreCase(tmpNameBase, tmpSuffix)) {
                    tmpSuffix = StringUtils.right(tmpNameBase, 4);
                    tmpNameBase = StringUtils.left(tmpNameBase, tmpNameBase.length() - 4);
                }
                String tmpName = anOpts.getOutPath() + anOpts.getOutPrefix() +
                        tmpNameBase + anOpts.getOutSuffix() + tmpSuffix;
                anOpts.setOutFile(tmpName);
                if (tmpLogic.signFile()) {
                    successCount++;
                } else {
                    failedCount++;
                }

            }
        }
        if (failedCount > 0) {
            if (successCount > 0)
                exit(Messages.SOME_SIGN_FAILED);
            else
                exit(Messages.ALL_SIGN_FAILED);
        }
    }

    private void exit(String message) throws SignerPdfServiceException {
        PKCS11Utils.unregisterProviders();
        throw new SignerPdfServiceException(message);
    }

}
