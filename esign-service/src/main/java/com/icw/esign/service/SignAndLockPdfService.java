package com.icw.esign.service;

import java.io.IOException;
import java.security.GeneralSecurityException;

public interface SignAndLockPdfService {
    byte [] encryptAndSignPdf(byte[] pdfBytes) throws IOException, GeneralSecurityException;
}
