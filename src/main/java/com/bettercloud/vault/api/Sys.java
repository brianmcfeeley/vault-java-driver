package com.bettercloud.vault.api;

import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultConfig;
import com.bettercloud.vault.VaultException;
import com.bettercloud.vault.response.SysResponse;
import com.bettercloud.vault.rest.RestResponse;

/**
 * <p>The implementing class for operations on Vault's <code>/v1/sys/*</code> REST endpoints.</p>
 *
 * <p>This class is not intended to be constructed directly.  Rather, it is meant to used by way of <code>Vault</code>
 * in a DSL-style builder pattern.  See the Javadoc comments of each <code>public</code> method for usage examples.</p>
 *
 * @see Vault#sys()
 */
public class Sys extends Endpoint<SysResponse> {

    public Sys(final VaultConfig config) {
        super(config);
    }

    @Override
    public SysResponse response(RestResponse restResponse, int retryCount) {
        return new SysResponse(restResponse, retryCount);
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
     * }</pre>
     * </blockquote>
     *
     * @param policyName The Vault policy name from which to read (e.g. <code>root, foo-policy</code>)
     * @return The response information returned from Vault
     * @throws VaultException If any errors occurs with the REST request (e.g. non-200 status code, invalid JSON payload, etc), and the maximum number of retries is exceeded.
     */
    public SysResponse policy(final String policyName) throws VaultException {
        return getWithRetry("/v1/sys/policy/" + policyName);
    }
}
