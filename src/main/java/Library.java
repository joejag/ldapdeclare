import org.apache.directory.api.ldap.model.cursor.CursorException;
import org.apache.directory.api.ldap.model.cursor.EntryCursor;
import org.apache.directory.api.ldap.model.entry.DefaultEntry;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapConnectionConfig;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;

import java.io.IOException;
import java.util.Map;

public class Library {

    public static void main(String[] args) throws LdapException, IOException, CursorException {
        LdapConnection connection = new LdapNetworkConnection(getConfig());

        connection.connect();

        EntryCursor cursor = connection.search("dc=example,dc=org", "(objectclass=*)", SearchScope.ONELEVEL);

        while (cursor.next()) {
            DefaultEntry entry = (DefaultEntry) cursor.get();
            System.out.println(entry);
        }

        cursor.close();
        connection.close();
    }

    private static LdapConnectionConfig getConfig() {
        LdapConnectionConfig config = new LdapConnectionConfig();
        config.setLdapHost("localhost");
        config.setLdapPort(389);
        config.setName("cn=admin,dc=example,dc=org");
        config.setCredentials("admin");
        return config;
    }
}
