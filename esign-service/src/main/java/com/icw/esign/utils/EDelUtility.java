package com.icw.esign.utils;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class EDelUtility {
    public static String getDBFormattedDate(LocalDateTime localDateTime) {
        int month = localDateTime.getMonthValue();
        int day = localDateTime.getDayOfMonth();
        int year = localDateTime.getYear();
        String dbFormatterDate = "";
        dbFormatterDate = dbFormatterDate.concat(Integer.toString(year));
        dbFormatterDate = dbFormatterDate.concat("-");
        dbFormatterDate = dbFormatterDate.concat(StringUtils.leftPad(Integer.toString(month), 2, "0"));
        dbFormatterDate = dbFormatterDate.concat("-");
        dbFormatterDate = dbFormatterDate.concat(StringUtils.leftPad(Integer.toString(day), 2, "0"));
        return dbFormatterDate;
    }

    public static String getDocumentTimeWithZone(String timeZone) {
        DateFormat requiredDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss zzz");
        requiredDateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
        return requiredDateFormat.format(new Date());
    }

    public static LocalDateTime getLocalDateFromString(String signedDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss zzz");
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(signedDate, formatter);
        return zonedDateTime.toLocalDateTime();
    }

    public static String getDBFormattedDate(LocalDate localDate) {
        int month = localDate.getMonthValue();
        int day = localDate.getDayOfMonth();
        int year = localDate.getYear();
        String dbFormatterDate = "";
        dbFormatterDate = dbFormatterDate.concat(Integer.toString(year));
        dbFormatterDate = dbFormatterDate.concat("-");
        dbFormatterDate = dbFormatterDate.concat(StringUtils.leftPad(Integer.toString(month), 2, "0"));
        dbFormatterDate = dbFormatterDate.concat("-");
        dbFormatterDate = dbFormatterDate.concat(StringUtils.leftPad(Integer.toString(day), 2, "0"));
        return dbFormatterDate;
    }

    public static String replaceTokens(String messageTemplate, Map<String, String> normalizedFieldMap, String regex) {
        StringBuffer messageBuffer = new StringBuffer();
        if (messageTemplate != null) {
            Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(messageTemplate);
            while (matcher.find()) {
                String messageToken = matcher.group();
                String fieldName = messageToken.substring(2, messageToken.length() - 2);
                String fieldValue = normalizedFieldMap.get(fieldName);
                if (fieldValue != null) {
                    if (fieldValue.contains("$")) {
                        fieldValue = fieldValue.replace("$", "\\$");
                    }
                    matcher.appendReplacement(messageBuffer, fieldValue);
                } else {
                    matcher.appendReplacement(messageBuffer, "");
                }
            }
            matcher.appendTail(messageBuffer);
        }
        return messageBuffer.toString();
    }

    public static Pair getDocKeyAndFileName(String contextId, String docCode, String docType, String extension, String contentType) {
        String basePath;
        if (!org.springframework.util.StringUtils.hasLength(contextId)) {
            UUID uuid = UUID.randomUUID();
            basePath = String.valueOf(uuid);
        } else if (contextId.contains("-")) {
            basePath = String.format("%s/%s",
                    contextId.toUpperCase().substring(0, contextId.lastIndexOf("-")), contextId.toUpperCase());
        } else {
            basePath = contextId;
        }
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss-SSS");
        String currentDateTime = dateFormat.format(Date.from(Instant.now()));
        String fileName = String.format("%s.%s.%s", currentDateTime, docCode, extension);
        String fileKey = String.format("%s/%s/%s", basePath, docType, fileName);
        Pair pair = new Pair(fileKey, fileName);
        pair.setContentType(contentType);
        return pair;
    }

    public static String getFormattedDobMMDDYYYY(Date dob) {
        LocalDate localDate = dob.toInstant().atZone(ZoneOffset.UTC).toLocalDate();
        int month = localDate.getMonthValue();
        int day = localDate.getDayOfMonth();
        int year = localDate.getYear();
        String dobStr = "";
        dobStr = dobStr.concat(org.apache.commons.lang3.StringUtils.leftPad(Integer.toString(month), 2, "0"));
        dobStr = dobStr.concat("/");
        dobStr = dobStr.concat(org.apache.commons.lang3.StringUtils.leftPad(Integer.toString(day), 2, "0"));
        dobStr = dobStr.concat("/");
        dobStr = dobStr.concat(Integer.toString(year));
        return dobStr;
    }

    public static String getFileExtension(MultipartFile file) {
        String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
        if (fileExtension == null || fileExtension.isEmpty()) {
            String contentType = file.getContentType();
            if (contentType != null){
                contentType = contentType.toLowerCase();
                switch (contentType){
                    case "image/jpeg":
                        fileExtension = "jpeg";
                        break;
                    case "image/jpg":
                        fileExtension = "jpg";
                        break;
                    case "image/png":
                        fileExtension = "png";
                        break;
                    default:
                        fileExtension = "";
                        break;
                }
            }
        }
        return fileExtension;
    }
    public static String getContentTypeBasedOnFileExtension(String filePath) {
        String fileExtension = FilenameUtils.getExtension(filePath);
        String contentType = "";
        if (StringUtils.isNotBlank(fileExtension)) {
            switch (fileExtension){
                case "jpeg":
                    contentType = "image/jpeg";
                    break;
                case "jpg":
                    contentType = "image/jpg";
                    break;
                case "png":
                    contentType = "image/png";
                    break;
                default:
                    contentType = "";
                    break;
            }
        }
        return contentType;
    }

    public static String getUserAgentToken(String userAgent) {
        if (StringUtils.isNotEmpty(userAgent)) {
            return userAgent.replace(Constants.API_USER_AGENT, "");
        }
        return userAgent;
    }

    public static String replaceVulnerableCharacters(String value) {
        if (StringUtils.isNotBlank(value)) {
            return value.replaceAll("[\n\r\t]", "");
        }
        return value;
    }

}