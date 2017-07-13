package com.bettercloud.vault.api;

import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultConfig;
import com.bettercloud.vault.VaultException;
import com.bettercloud.vault.response.SysResponse;
import com.bettercloud.vault.rest.Rest;
import com.bettercloud.vault.rest.RestException;
import com.bettercloud.vault.rest.RestResponse;

import java.io.UnsupportedEncodingException;

/**
 * <p>The implementing class for operations on Vault's <code>/v1/sys/*</code> REST endpoints.</p>
 *
 * <p>This class is not intended to be constructed directly.  Rather, it is meant to used by way of <code>Vault</code>
 * in a DSL-style builder pattern.  See the Javadoc comments of each <code>public</code> method for usage examples.</p>
 *
 * @see Vault#sys()
 */
public class Sys {
    private final VaultConfig config;

    public Sys(final VaultConfig config) {
        this.config = config;
    }

    /**
     * <p>Basic system operation to read a policy.  Policies describe what parts of vault a user may access, E.g.:</p>
     *
     * <blockquote>
     * <pre>{@code
     * path "secret/foo" {
     *   policy = "read"
     *   capabilities = ["create", "sudo"]
     * }
     * </pre>
     * </blockquote>
     *
     * @param policyName The Vault policy name from which to read (e.g. <code>root, foo-policy</code>)
     * @return The response information returned from Vault
     * @throws VaultException If any errors occurs with the REST request (e.g. non-200 status code, invalid JSON payload, etc), and the maximum number of retries is exceeded.
     */
    public SysResponse policy(final String policyName) throws VaultException {
        int retryCount = 0;
        while (true) {
            try {
                final RestResponse restResponse = new Rest()//NOPMD
                        .url(config.getAddress() + "/v1/sys/policy" + policyName)
                        .header("X-Vault-Token", config.getToken())
                        .connectTimeoutSeconds(config.getOpenTimeout())
                        .readTimeoutSeconds(config.getReadTimeout())
                        .sslVerification(config.getSslConfig().isVerify())
                        .sslContext(config.getSslConfig().getSslContext())
                        .get();

                // Validate response
                if (restResponse.getStatus() != 200) {
                    throw new VaultException("Vault responded with HTTP status code: " + restResponse.getStatus()
                            + "\nResponse body: " + new String(restResponse.getBody(), "UTF-8"), restResponse.getStatus());
                }

                return new SysResponse(restResponse, retryCount);
            } catch (RuntimeException | VaultException | RestException | UnsupportedEncodingException e) {
                // If there are retries to perform, then pause for the configured interval and then execute the loop again...
                if (retryCount < config.getMaxRetries()) {
                    retryCount++;
                    try {
                        final int retryIntervalMilliseconds = config.getRetryIntervalMilliseconds();
                        Thread.sleep(retryIntervalMilliseconds);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                } else if (e instanceof VaultException) {
                    // ... otherwise, give up.
                    throw (VaultException) e;
                } else {
                    throw new VaultException(e);
                }
            }
        }
    }
}
