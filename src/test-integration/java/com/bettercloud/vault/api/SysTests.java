package com.bettercloud.vault.api;

import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultException;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import java.io.IOException;

import static junit.framework.TestCase.assertEquals;

/**
 * Integration tests for the sys Vault API operations.
 */
public class SysTests {
    @ClassRule
    public static final VaultContainer container = new VaultContainer();

    @BeforeClass
    public static void setupClass() throws IOException, InterruptedException {
        container.initAndUnsealVault();
    }

    /**
     * Verify we can get the root policy, which always exists.
     *
     * @throws VaultException
     */
    @Test
    public void testGetRootPolicy() throws VaultException {
        final String policy = "root";
        final Vault vault = container.getRootVault();

        final String policyName = vault.sys().policy("root").getName();

        assertEquals(policy, policyName);
    }
}
