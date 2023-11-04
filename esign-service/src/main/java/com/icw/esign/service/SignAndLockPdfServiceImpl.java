package com.icw.esign.service;

import com.itextpdf.forms.PdfSigFieldLock;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.signatures.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.util.Arrays;

@Service
public class SignAndLockPdfServiceImpl implements SignAndLockPdfService{

    PrivateKey pk;
    java.security.cert.Certificate [] chain;

    String providerName;

    @Value("${esign.entrust.keystore.passphrase}")
    private String keystorePassphrase;

    @Value("${esign.entrust.pdf.owner.password}")
    private String pdfOwnerPassword;

    @Value("${esign.entrust.cert.path}")
    private String certPath;

    @Autowired
    private Environment environment;

    private static final Logger logger = LoggerFactory.getLogger(SignAndLockPdfServiceImpl.class);

    @PostConstruct
    void buildCertificateChain() throws Exception {
        if (!Arrays.asList(environment.getActiveProfiles()).contains("INT")) {
            logger.info("Cert path : {}", certPath);
            BouncyCastleProvider provider = new BouncyCastleProvider();
            Security.addProvider(provider);
            providerName = provider.getName();
            KeyStore ks = KeyStore.getInstance("pkcs12", provider.getName());
            ks.load(new FileInputStream(certPath), keystorePassphrase.toCharArray());
            String alias = ks.aliases().nextElement();
            pk = (PrivateKey) ks.getKey(alias, keystorePassphrase.toCharArray());
            chain = ks.getCertificateChain(alias);
        } else {
            logger.info("Skip Cert initialization in INT");
        }
    }

    void encrypt(InputStream source, OutputStream target) throws IOException {
        PdfReader reader = new PdfReader(source);
        PdfWriter writer = new PdfWriter(target, new WriterProperties().setStandardEncryption(null, pdfOwnerPassword.getBytes(),
                EncryptionConstants.ALLOW_PRINTING, EncryptionConstants.ENCRYPTION_AES_128 | EncryptionConstants.DO_NOT_ENCRYPT_METADATA));
        new PdfDocument(reader, writer).close();
    }

    public void sign(InputStream src, OutputStream result, String reason, String location)
            throws GeneralSecurityException, IOException {
        PdfReader reader = new PdfReader(src, new ReaderProperties().setPassword(pdfOwnerPassword.getBytes()));
        PdfSigner signer = new PdfSigner(reader, result, new StampingProperties().useAppendMode());
        PdfSigFieldLock fieldLock = new PdfSigFieldLock();
        fieldLock.setDocumentPermissions(PdfSigFieldLock.LockPermissions.NO_CHANGES_ALLOWED);
        fieldLock.setFieldLock(PdfSigFieldLock.LockAction.EXCLUDE, new String[]{});
        signer.setFieldLockDict(fieldLock);
        Rectangle rect = new Rectangle(0, 0, 0, 0);
        PdfSignatureAppearance appearance = signer.getSignatureAppearance();
        appearance.setPageRect(rect);
        appearance.setReason(reason);
        appearance.setLocation(location);
        IExternalSignature pks = new PrivateKeySignature(pk, DigestAlgorithms.SHA256, providerName);
        IExternalDigest digest = new BouncyCastleDigest();
        signer.signDetached(digest, pks, chain, null, null, null, 0, PdfSigner.CryptoStandard.CMS);
    }

    @Override
    public byte [] encryptAndSignPdf(byte[] pdfBytes) throws IOException, GeneralSecurityException {
        ByteArrayOutputStream lockedByteArrayOutputStream = new ByteArrayOutputStream();
        logger.info("PDF locking started");
        encrypt(new ByteArrayInputStream(pdfBytes), lockedByteArrayOutputStream);
        logger.info("PDF locking completed");
        //We have a byte Output Stream at this point with owner properties locked
        ByteArrayInputStream inLockedStream = new ByteArrayInputStream( lockedByteArrayOutputStream.toByteArray());
        ByteArrayOutputStream signedByteArrayOutputStream = new ByteArrayOutputStream();
        logger.info("PDF signing started");
        sign(inLockedStream, signedByteArrayOutputStream, "", "");
        logger.info("PDF signing completed");
        return signedByteArrayOutputStream.toByteArray();
    }
}

