package com.azero.services.iot.client.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.azero.services.iot.client.AZEROIotException;

/**
 * The AZEROIotWebSocketUrlSigner class creates the SigV4 signature and builds a
 * connection URL to be used with the Paho MQTT client.
 */
public class AzeroIotWebSocketUrlSigner {

    /** Constant defining the algorithm use for hash calculation. */
    private static final String HASH_ALGORITHM = "SHA-256";
    /** Constant defining the algorithm use for MAC calculation. */
    private static final String HMAC_ALGORITHM = "HmacSHA256";
    /** Constant defining the algorithm specifier in SigV4 parameters. */
    private static final String ALGORITHM = "AWS4-HMAC-SHA256";
    /** Constant defining the key prefix string in SigV4 parameters. */
    private static final String KEY_PREFIX = "AWS4";
    /** Constant defining the terminator string in SigV4 parameters. */
    private static final String TERMINATOR = "aws4_request";
    /** Short date format pattern used in SigV4 parameters. */
    private static final String DATE_PATTERN = "yyyyMMdd";
    /** ISO 8601 date format pattern used in SigV4 signature parameters. */
    private static final String TIME_PATTERN = "yyyyMMdd'T'HHmmss'Z'";
    /** Default timezone used for converting signing date. */
    private static final TimeZone TIME_ZONE = TimeZone.getTimeZone("UTC");
    /** Default charset used for URL encoding. */
    private static final String UTF8 = "UTF-8";
    /** Constant defining the HTTP method for the WebSocket connection. */
    private static final String METHOD = "GET";
    /** URI for WebSocket endpoint when doing initial HTTP operation. */
    private static final String CANONICAL_URI = "/mqtt";
    /** endpoint pattern used for validation and extracting region. */
    private static final Pattern EndpointPattern = Pattern.compile("iot\\.([\\w-]+)\\.bj.soundai.cn\\..*");
    /** service name used for signing. */
    private static final String ServiceName = "iotdata";
    /** Blank region, placeholder until region is determined from the endpoint */
    private static final String REGION_TO_BE_DETERMINED = "";

    private String endpoint;
    private String regionName;
    private String azeroAccessKeyId;
    private String sessionToken;
    private Mac signingSecretMac;

    /**
     * Instantiates a new URL signer instance with endpoint only.
     *
     * @param endpoint
     *            service endpoint with or without customer specific URL prefix.
     */
    public AzeroIotWebSocketUrlSigner(String endpoint) {
        this(endpoint, REGION_TO_BE_DETERMINED);
    }

    /**
     * Instantiate a new URL signer with endpoint and region.
     * @param endpoint service endpoint with or without customer specific URL prefix.
     * @param region The AZERO region
     */
    public AzeroIotWebSocketUrlSigner(String endpoint, String region) {
        if (endpoint == null) {
            throw new IllegalArgumentException("Invalid endpoint provided");
        }
        this.endpoint = endpoint.trim().toLowerCase();
        if (region == null) {
            throw new IllegalArgumentException("Invalid region provided");
        } else if(region.equals(REGION_TO_BE_DETERMINED)) {
            this.regionName = getRegionFromEndpoint(this.endpoint);
            if(this.regionName == null) {
                throw new IllegalArgumentException("Could not extract region from endpoint provided");
            }
        } else {
            this.regionName = region;
        }
    }

    /**
     * Instantiates a new URL signer instance with endpoint and credentials.
     *
     * @param endpoint
     *            service endpoint with or without customer specific URL prefix.
     * @param azeroAccessKeyId
     *            AZERO access key ID used in SigV4 signature algorithm.
     * @param azeroSecretAccessKey
     *            AZERO secret access key used in SigV4 signature algorithm.
     * @param sessionToken
     *            Session token for temporary credentials.
     */
    public AzeroIotWebSocketUrlSigner(String endpoint, String azeroAccessKeyId, String azeroSecretAccessKey,
            String sessionToken) {
        this(endpoint);

        updateCredentials(azeroAccessKeyId, azeroSecretAccessKey, sessionToken);
    }

    /**
     * Instantiates a new URL signer instance with endpoint and credentials.
     *
     * @param endpoint
     *            service endpoint with or without customer specific URL prefix.
     * @param azeroAccessKeyId
     *            AZERO access key ID used in SigV4 signature algorithm.
     * @param azeroSecretAccessKey
     *            AZERO secret access key used in SigV4 signature algorithm.
     * @param sessionToken
     *            Session token for temporary credentials.
     * @param region
     *            The AZERO region
     */
    public AzeroIotWebSocketUrlSigner(String endpoint, String azeroAccessKeyId, String azeroSecretAccessKey,
                                    String sessionToken, String region) {
        this(endpoint,region);

        updateCredentials(azeroAccessKeyId, azeroSecretAccessKey, sessionToken);
    }

    /**
     * Updates the signing credentials.
     *
     * @param azeroAccessKeyId
     *            AZERO access key ID used in SigV4 signature algorithm.
     * @param azeroSecretAccessKey
     *            AZERO secret access key used in SigV4 signature algorithm.
     * @param sessionToken
     *            Session token for temporary credentials.
     */
    public void updateCredentials(String azeroAccessKeyId, String azeroSecretAccessKey, String sessionToken) {
        if (azeroAccessKeyId == null || azeroSecretAccessKey == null) {
            throw new IllegalArgumentException("Missing required data for signing");
        }

        this.azeroAccessKeyId = azeroAccessKeyId.trim();

        try {
            // secret key is stored as a hash in memory to prevent key leaking
            // from memory dump
            byte[] signingSecret = (KEY_PREFIX + azeroSecretAccessKey).getBytes(UTF8);
            this.signingSecretMac = Mac.getInstance(HMAC_ALGORITHM);
            this.signingSecretMac.init(new SecretKeySpec(signingSecret, HMAC_ALGORITHM));

            this.sessionToken = sessionToken;
            if (this.sessionToken != null) {
                this.sessionToken = URLEncoder.encode(this.sessionToken, UTF8);
            }
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new IllegalArgumentException("Error in initializing signing secret MAC");
        }
    }

    /**
     * Given the signing date return a signed connection URL to be used when
     * connecting via WebSocket to AZERO IoT.
     *
     * @param signingDate
     *            time value to be used in SigV4 calculations. System current
     *            time will be used if null.
     * @return a URL with SigV4 signature formatted to be used with AZERO IoT.
     * @throws AZEROIotException
     *             Exception thrown when signed URL can be generated with given
     *             information.
     */
    public String getSignedUrl(final Date signingDate) throws AZEROIotException {
        Date dateToUse = signingDate;
        if (dateToUse == null) {
            dateToUse = new Date();
        }

        // SigV4 canonical string uses time in two formats
        String amzDate = getAmzDate(dateToUse);
        String dateStamp = getDateStamp(dateToUse);
        // Credential scoped to date and region
        String credentialScope = dateStamp + "/" + regionName + "/" + ServiceName + "/aws4_request";
        // Now build the canonical string
        StringBuilder canonicalQueryStringBuilder = new StringBuilder();
        canonicalQueryStringBuilder.append("X-Amz-Algorithm=").append(ALGORITHM);
        canonicalQueryStringBuilder.append("&X-Amz-Credential=");
        try {
            canonicalQueryStringBuilder.append(URLEncoder.encode(azeroAccessKeyId + "/" + credentialScope, UTF8));
        } catch (UnsupportedEncodingException e) {
            throw new AZEROIotException("Error encoding URL when building WebSocket URL");
        }
        canonicalQueryStringBuilder.append("&X-Amz-Date=").append(amzDate);
        canonicalQueryStringBuilder.append("&X-Amz-SignedHeaders=host");

        // headers and payload for the signing request
        // not used in an WebSocket URL, but encoded into the signature string
        String canonicalHeaders = "host:" + endpoint + "\n";
        String payloadHash = stringToHex(hash(""));

        // The request to sign includes the HTTP method, path, query string,
        // headers and payload
        String canonicalRequest = METHOD + "\n" + CANONICAL_URI + "\n" + canonicalQueryStringBuilder.toString() + "\n"
                + canonicalHeaders + "\nhost\n" + payloadHash;

        // Create a string to sign, generate a signing key...
        String stringToSign = ALGORITHM + "\n" + amzDate + "\n" + credentialScope + "\n"
                + stringToHex(hash(canonicalRequest));
        byte[] signingKey = getSigningKey(dateStamp);
        // ...and sign the string.
        byte[] signatureBytes = sign(stringToSign, signingKey);
        String signature = stringToHex(signatureBytes);

        // Add the signature to the query string.
        canonicalQueryStringBuilder.append("&X-Amz-Signature=");
        canonicalQueryStringBuilder.append(signature);

        // Now build the URL.
        String requestUrl = "wss://" + endpoint + CANONICAL_URI + "?" + canonicalQueryStringBuilder.toString();

        // If there are session credentials (from an STS server, AssumeRole, or
        // Amazon Cognito),
        // append the session token to the end of the URL string after signing.
        if (sessionToken != null) {
            requestUrl += "&X-Amz-Security-Token=" + sessionToken;
        }

        return requestUrl;
    }

    /**
     * @return the region this signer is configured to sign against
     */
    public String getRegion() {
        return this.regionName;
    }

    private String getRegionFromEndpoint(String endpoint) {
        Matcher matcher = EndpointPattern.matcher(endpoint);
        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }

    /**
     * Converts byte data to a Hex-encoded string.
     *
     * @param data
     *            data to hex encode.
     * @return hex-encoded string.
     */
    private String stringToHex(final byte[] data) {
        StringBuilder sb = new StringBuilder(data.length * 2);
        for (int i = 0; i < data.length; i++) {
            String hex = Integer.toHexString(data[i]);
            if (hex.length() == 1) {
                // Append leading zero.
                sb.append("0");
            } else if (hex.length() == 8) {
                // Remove ff prefix from negative numbers.
                hex = hex.substring(6);
            }
            sb.append(hex);
        }
        return sb.toString().toLowerCase();
    }

    /**
     * The SigV4 signing key is made up by consecutively hashing a number of
     * unique pieces of data.
     * 
     * @param dateStamp
     *            the current date in short date format.
     * @return byte array containing the SigV4 signing key.
     * @throws AZEROIotException
     */
    private byte[] getSigningKey(String dateStamp) throws AZEROIotException {
        if (signingSecretMac == null) {
            throw new AZEROIotException("Signing credentials not provided");
        }
        // AWS4 uses a series of derived keys, formed by hashing different
        // pieces of data
        byte[] signingDate = sign(dateStamp, signingSecretMac);
        byte[] signingRegion = sign(regionName, signingDate);
        byte[] signingService = sign(ServiceName, signingRegion);
        return sign(TERMINATOR, signingService);
    }

    /**
     * Given the input epoch time returns a String of the proper format for the
     * ISO 8601 date + time in SigV4 parameters.
     * 
     * @param date
     *            desired date.
     * @return date formatted string in ISO 8601 date + time format.
     */
    private String getAmzDate(final Date date) {
        SimpleDateFormat fomatter = new SimpleDateFormat(TIME_PATTERN);
        fomatter.setTimeZone(TIME_ZONE);
        return fomatter.format(date);
    }

    /**
     * Given the input epoch time returns a String of the proper format for the
     * short date in SigV4 parameters.
     * 
     * @param date
     *            desired date.
     * @return date formatted string in short date format.
     */
    private String getDateStamp(final Date date) {
        SimpleDateFormat fomatter = new SimpleDateFormat(DATE_PATTERN);
        fomatter.setTimeZone(TIME_ZONE);
        return fomatter.format(date);
    }

    /**
     * Hashes the string contents (assumed to be UTF-8) using the SHA-256
     * algorithm.
     *
     * @param text
     *            The string to hash.
     * @return The hashed bytes from the specified string.
     * @throws AmazonClientException
     *             If the hash cannot be computed.
     */
    private byte[] hash(String text) throws AZEROIotException {
        try {
            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
            md.update(text.getBytes(UTF8));
            return md.digest();
        } catch (Exception e) {
            throw new AZEROIotException("Unable to compute hash while signing request: " + e.getMessage());
        }
    }

    /**
     * Sign the given string with the key provided.
     *
     * @param stringData
     *            String to be signed.
     * @param key
     *            the key for signing.
     * @return a byte array containing the signed string.
     * @throws AmazonClientException
     *             in the case of a signature error.
     */
    private byte[] sign(String stringData, final byte[] key) throws AZEROIotException {
        try {
            byte[] data = stringData.getBytes(UTF8);
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(key, HMAC_ALGORITHM));
            return mac.doFinal(data);
        } catch (Exception e) {
            throw new AZEROIotException("Unable to calculate a request signature: " + e.getMessage());
        }
    }

    /**
     * Sign the given data with the key provided.
     *
     * @param stringData
     *            String to be signed.
     * @param mac
     *            the signing algorithm with key initialized.
     * @return a byte array containing the signed string.
     * @throws AmazonClientException
     *             in the case of a signature error.
     */
    private byte[] sign(String stringData, final Mac mac) throws AZEROIotException {
        try {
            byte[] data = stringData.getBytes(UTF8);
            return mac.doFinal(data);
        } catch (Exception e) {
            throw new AZEROIotException("Unable to calculate a request signature: " + e.getMessage());
        }
    }

}
