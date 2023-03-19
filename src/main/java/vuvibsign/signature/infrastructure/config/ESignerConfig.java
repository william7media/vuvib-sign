package vuvibsign.signature.infrastructure.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
public class ESignerConfig {

    public static String UNSIGNED_DIRECTORY;
    public static String SIGNED_DIRECTORY;
    public static String KEYSTORE_DIRECTORY;
    public static String KEYSTORE_TYPE;
    public static String HASH_ALGORITHM;
    public static Boolean SIGNATURE_APPEND;
    public static Boolean SIGNATURE_VISIBLE;
    public static Integer PAGE;
    public static Double FONT_SIZE;
    public static String PREFIX = "vuvib_";
    public static String DOCUMENT_SUFFIX = ".pdf";
    public static String KEYSTORE_SUFFIX = ".pfx";
    @Value("${file.directory.unsigned}")
    private String unsignedDirectory;
    @Value("${file.directory.signed}")
    private String signedDirectory;
    @Value("${keystore.directory}")
    private String keystoreDirectory;
    @Value("${keystore.type}")
    private String keystoreType;
    @Value("${hash.algorithm}")
    private String hashAlgorithm;
    @Value("${signature.append}")
    private Boolean signatureAppend;
    @Value("${signature.visible}")
    private Boolean signatureVisible;
    @Value("${signature.page}")
    private Integer page;
    @Value("${signature.fontsize}")
    private Double fontSize;

    ////
    @PostConstruct
    public void init() {
        UNSIGNED_DIRECTORY = unsignedDirectory;
        SIGNED_DIRECTORY = signedDirectory;
        KEYSTORE_DIRECTORY = keystoreDirectory;
        KEYSTORE_TYPE = keystoreType;
        HASH_ALGORITHM = hashAlgorithm;
        SIGNATURE_APPEND = signatureAppend;
        SIGNATURE_VISIBLE = signatureVisible;
        PAGE = page;
        FONT_SIZE = fontSize;
    }

}
