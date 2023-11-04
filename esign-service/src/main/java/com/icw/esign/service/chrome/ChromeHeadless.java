package com.icw.esign.service.chrome;
import com.github.kklisura.cdt.launch.ChromeArguments;
import com.github.kklisura.cdt.launch.ChromeLauncher;
import com.github.kklisura.cdt.protocol.commands.Page;
import com.github.kklisura.cdt.protocol.types.page.Navigate;
import com.github.kklisura.cdt.protocol.types.page.PrintToPDF;
import com.github.kklisura.cdt.protocol.types.page.PrintToPDFTransferMode;
import com.github.kklisura.cdt.services.ChromeDevToolsService;
import com.github.kklisura.cdt.services.ChromeService;
import com.github.kklisura.cdt.services.types.ChromeTab;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Component
public class ChromeHeadless {

    private static final Logger logger = LoggerFactory.getLogger(ChromeHeadless.class);

    private boolean initialized = false;
    private ChromeLauncher launcher = null;
    private ChromeService chromeService = null;
    Map<String, byte[]> pdfMap = new HashMap<>();

    @PostConstruct
    private void initialize() {
        logger.info("Initializing Chrome Headless");
        this.launcher = new ChromeLauncher();

        ChromeArguments args = ChromeArguments
                .builder()
                .headless(true)
                .additionalArguments("no-sandbox", true)
                .additionalArguments("remote-allow-origins", "*")
                .build();
        this.chromeService = launcher.launch(args);

        logger.info("Chrome Headless initialized successfully");
    }

    //@Retryable(value = {Exception.class},
    //        backoff = @Backoff(delay = 1000))
    public byte[] getPDF(String html, boolean isLetter) throws Exception {

        logger.info("Enter getPDF()");

        try {

            // Create empty tab ie about:blank.
            final ChromeTab tab = this.chromeService.createTab();

            // Get DevTools service to this tab
            final ChromeDevToolsService devToolsService = this.chromeService.createDevToolsService(tab);

            // Get individual commands
            final Page page = devToolsService.getPage();
            final Navigate navigate = page.navigate("about:blank");
            final String frameId = navigate.getFrameId();
            this.pdfMap.put(frameId, null);


            page.onLoadEventFired(
                    loadEventFired -> {
                        logger.info("Page loaded successfully. Printing to PDF");
                        Boolean landscape = false;
                        Boolean displayHeaderFooter = false;
                        Boolean printBackground = true;

                        Double paperWidth = isLetter ? 7.25d : 8.27d; // A4 paper format
                        Double paperHeight = isLetter ? 10.5d : 11.7d; // A4 paper format
                        Double scale = 1d;
                        Double marginTop = isLetter ? 0d : 0.5d;
                        Double marginBottom = isLetter ? 0d : 0.5d;
                        Double marginLeft = 0d;
                        Double marginRight = 0d;
                        String pageRanges = "";
                        Boolean ignoreInvalidPageRanges = false;
                        String headerTemplate = "";
                        String footerTemplate = "";
                        Boolean preferCSSPageSize = false;
                        PrintToPDFTransferMode mode = PrintToPDFTransferMode.RETURN_AS_BASE_64;


                        PrintToPDF printToPDF = page.printToPDF(
                                landscape,
                                displayHeaderFooter,
                                printBackground,
                                scale,
                                paperWidth,
                                paperHeight,
                                marginTop,
                                marginBottom,
                                marginLeft,
                                marginRight,
                                pageRanges,
                                ignoreInvalidPageRanges,
                                headerTemplate,
                                footerTemplate,
                                preferCSSPageSize,
                                mode);

                        logger.info("Getting PDF bytes");
                        final byte[] pdfBytes = Base64.getDecoder().decode(printToPDF.getData());
                        this.pdfMap.replace(frameId, pdfBytes);

                        logger.info("Closing DevToolsService");
                        devToolsService.close();
                    });

            page.enable();

            logger.info("Loading HTML document");
            page.setDocumentContent(frameId, html);


            logger.info("Waiting for HTML to finish loading and close");
            devToolsService.waitUntilClosed();

            logger.info("Closing Tab");
            this.chromeService.closeTab(tab);

            byte[] bytes = this.pdfMap.get(frameId);

            logger.info("Removing entry from map", bytes);
            this.pdfMap.remove(frameId);

            logger.info("Returing {} bytes", bytes != null ? bytes.length : -1);
            return bytes;
        } catch (Exception ex) {
            throw ex;
        }
    }
}