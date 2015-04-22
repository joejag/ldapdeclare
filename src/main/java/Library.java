import org.apache.directory.api.ldap.model.cursor.CursorException;
import org.apache.directory.api.ldap.model.cursor.EntryCursor;
import org.apache.directory.api.ldap.model.entry.DefaultEntry;
import org.apache.directory.api.ldap.model.entry.DefaultModification;
import org.apache.directory.api.ldap.model.entry.Modification;
import org.apache.directory.api.ldap.model.entry.ModificationOperation;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapConnectionConfig;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Library {

    public static final String OWNER = "cn=admin";
    public static final String OWNER_PASSWORD = "admin";
    public static final String LDAP_HOST = "localhost";
    public static final String SAMPLE_USER = "cn=user";
    public static final String SAMPLE_GROUP = "cn=myGroup,cn=Groups";

    public static void main(String[] args) throws LdapException, IOException, CursorException {
        LdapConnection connection = new LdapNetworkConnection(getConfig());

        connection.connect();
        connection.bind(OWNER, OWNER_PASSWORD);

        addUserToGroup(connection, SAMPLE_USER, SAMPLE_GROUP);
        removeUserFromGroup(connection, SAMPLE_USER, SAMPLE_GROUP);

        listGroupMembers(connection, SAMPLE_GROUP);

        connection.close();
    }

    private static void listGroupMembers(LdapConnection connection, String group) throws LdapException, CursorException {
        EntryCursor cursor = connection.search(group, "(objectclass=*)", SearchScope.SUBTREE);

        while (cursor.next()) {
            DefaultEntry entry = (DefaultEntry) cursor.get();
            Dn dn = entry.getDn();

            System.out.println(dn.getName());
            System.out.println(entry.get("owner"));

            if (entry.containsAttribute("uniquemember")) {
                System.out.println(entry.get("uniquemember"));
            } else {
                System.out.println("No members!");
            }

            System.out.println();
        }

        cursor.close();
    }

    private static void removeUserFromGroup(LdapConnection connection, String user, String group) throws LdapException {
        Modification addUserToGroup = new DefaultModification(ModificationOperation.REMOVE_ATTRIBUTE, "uniquemember", user);
        connection.modify(group, addUserToGroup);
    }

    private static void addUserToGroup(LdapConnection connection, String user, String group) throws LdapException {
        Modification addUserToGroup = new DefaultModification(ModificationOperation.ADD_ATTRIBUTE, "uniquemember", user);
        connection.modify(group, addUserToGroup);
    }

    private static void changeGroupOwner(LdapConnection connection, String user, String group) throws LdapException {
        Modification addUserToGroup = new DefaultModification(ModificationOperation.REPLACE_ATTRIBUTE, "owner", user);
        connection.modify(group, addUserToGroup);
    }

    private static LdapConnectionConfig getConfig() {
        LdapConnectionConfig config = new LdapConnectionConfig();
        config.setLdapHost(LDAP_HOST);
        config.setLdapPort(389);
        return config;
    }
}
